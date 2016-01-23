/*
 * Copyright 2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */

package org.inventivetalent.rushhour.puzzle;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.inventivetalent.rushhour.car.Car;
import org.inventivetalent.rushhour.car.Rotation;
import org.inventivetalent.rushhour.car.Variant;
import org.inventivetalent.rushhour.event.PlayerFinishPuzzleEvent;
import org.inventivetalent.rushhour.puzzle.generator.AbstractPuzzleGenerator;
import org.inventivetalent.rushhour.puzzle.generator.CarInteractListener;
import org.inventivetalent.rushhour.puzzle.solution.Solution;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class Puzzle {

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Puzzle.class, new Puzzle.Serializer()).registerTypeAdapter(Solution.class, new Solution.Serializer()).create();

	public static final Bounds[] WALL_BOUNDS   = new Bounds[] {
	/*Left wall*/
			new Bounds(-1, 0, Rotation.VERTICAL, 6),

	/*Right wall (upper)*/
			new Bounds(6, 0, Rotation.VERTICAL, 1),
	/*Right wall (lower)*/
			new Bounds(6, 3, Rotation.VERTICAL, 3),

	/*Top wall*/
			new Bounds(0, -1, Rotation.HORIZONTAL, 6),
	/*Bottom wall*/
			new Bounds(0, 6, Rotation.HORIZONTAL, 6) };
	public static final Bounds   FINISH_BOUNDS = new Bounds(7, 2, Rotation.HORIZONTAL, 1);

	public String     name;
	public Difficulty difficulty;
	public Set<GameCar> cars = new HashSet<>();

	//The pre-defined puzzle solution
	public Solution solution;
	//Solution the player used
	public Solution playerSolution = new Solution();

	//If the puzzle is currently being solved by the solution
	public boolean isSolving = false;

	public boolean isFinished = false;

	public Player player;

	public AbstractPuzzleGenerator generator;

	private String hash;

	public Puzzle() {
	}

	public void initializeCars() {
		for (final GameCar car : cars) {
			car.bounds = new Bounds(car.x, car.y, car.rotation, car.variant.getLength());
		}
	}

	public void addCarsToInventory(AbstractPuzzleGenerator generator) {
		this.generator = generator;

		//Clear all old listeners
		generator.resetListeners();
		//Remove all old items
		generator.clearCars();

		generator.generateBase();

		for (final GameCar car : cars) {
			final int length = car.variant.getLength();
			for (int i = 0; i < length; i++) {
				final int j = i;
				final int x = car.bounds.x() + (car.rotation.getShiftX() * i);
				final int y = car.bounds.y() + (car.rotation.getShiftY() * i);

				if ((x < 0 || y < 0) || (x > 5 || y > 5)) {
					//Exception for the exit
					if ((x != 6 && x != 7) || y != 2) { continue; }
				}

				Direction direction = null;
				if (j == 0) {//Move left/up
					if (car.rotation == Rotation.HORIZONTAL) {//Left
						direction = Direction.LEFT;
					} else if (car.rotation == Rotation.VERTICAL) {//Up
						direction = Direction.UP;
					}
				} else if (j == length - 1) {//Move right/down
					if (car.rotation == Rotation.HORIZONTAL) {//Right
						direction = Direction.RIGHT;
					} else if (car.rotation == Rotation.VERTICAL) {//Down
						direction = Direction.DOWN;
					}
				}

				final Direction finalDirection = direction;
				generator.setCar(x, y, car, direction, new CarInteractListener() {
					@Override
					public void onInteract(Player player, ClickType clickType) {
						if (!player.getUniqueId().equals(Puzzle.this.player.getUniqueId())) { throw new IllegalStateException(); }
						if (finalDirection != null) {
							moveCar(car, finalDirection);
						}
					}
				});
			}
		}

		generator.updateCars();
	}

	public void moveCar(GameCar car, Direction direction) {
		Bounds prevBounds = car.bounds;
		Bounds target = car.bounds.shift(direction);
		if (!checkCollision(car, target)) {
			player.playSound(player.getEyeLocation(), Sound.NOTE_BASS, 0.8f, 0.8f);
			player.playSound(player.getEyeLocation(), Sound.NOTE_BASS_DRUM, 0.5f, 0.7f);
			return;
		}
		car.bounds = target;

		player.playSound(player.getEyeLocation(), Sound.NOTE_STICKS, 1.0f, 0.8f);
		player.playSound(player.getEyeLocation(), Sound.PISTON_EXTEND, 0.05f, 0.7f);

		System.out.println("Car " + car + " moved " + direction + " (from " + prevBounds.x() + "|" + prevBounds.y() + " to " + target.x() + "|" + target.y() + " )");

		playerSolution.trackPlayerMove(null, car.variant, direction, 1);
		generator.updateMoves(playerSolution.combineMoves().moves.size(), playerSolution.moves.size());

		if (FINISH_BOUNDS.collidesWith(target)) {
			if (car.variant == Variant.MAIN) {
				System.out.println("Puzzle finished!!!");
				puzzleFinished(true);
			} else {
				throw new IllegalStateException("Car managed to reach finish position but it's not the MAIN variant!");
			}
		}

		//Update car positions
		addCarsToInventory(this.generator);
	}

	public boolean checkCollision(Car movingCar, Bounds targetBounds) {
		for (Bounds bounds : WALL_BOUNDS) {
			if (targetBounds.collidesWith(bounds)) {
				System.out.println("Wall collision");
				return false;
			}
		}
		for (GameCar car : this.cars) {
			if (movingCar == car) {
				continue;
			}
			if (car.bounds.collidesWith(targetBounds)) {
				System.out.println("Car collision " + movingCar);
				System.out.println("  With " + car);
				return false;
			}
		}

		return true;
	}

	public void puzzleFinished(boolean solved) {
		if (!isFinished) {
			PlayerFinishPuzzleEvent event = new PlayerFinishPuzzleEvent(this.player, this, this.playerSolution.moves.size(), solved, this.isSolving, this.playerSolution, this.solution);
			Bukkit.getPluginManager().callEvent(event);
		}

		if (isFinished || !solved/*!solved means the inventory was closed*/) {
			this.player.removeMetadata("RUSHHOUR_GENERATOR", Bukkit.getPluginManager().getPlugin("RushHour"));
			this.player.removeMetadata("RUSHHOUR_PUZZLE", Bukkit.getPluginManager().getPlugin("RushHour"));

			this.generator.resetListeners();

			Runtime.getRuntime().gc();
		} else {
			System.out.println("!isFinished || solved");
		}

		this.generator.gameFinished(solved, this.isSolving);

		isFinished = true;
	}

	public String getLevelPerm() {
		return this.name.replace(File.separator, ".");
	}

	public String getHash() {
		if (hash == null) {
			hash = Base64.getEncoder().encodeToString((this.name + (this.solution != null ? this.solution.toJsonArray().toString() : "[]")).getBytes());
		}
		return hash;
	}

	public void toJson(Writer writer) {
		GSON.toJson(this, writer);
	}

	public String toJson() {
		return GSON.toJson(this);
	}

	public static Puzzle fromJson(Reader reader) {
		return GSON.fromJson(reader, Puzzle.class);
	}

	public static Puzzle fromJson(String string) {
		return GSON.fromJson(string, Puzzle.class);
	}

	public static class Serializer implements JsonSerializer<Puzzle> {

		@Override
		public JsonElement serialize(Puzzle puzzle, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty("name", puzzle.name);
			jsonObject.addProperty("difficulty", puzzle.difficulty.name());

			JsonArray carArray = new JsonArray();
			for (Car car : puzzle.cars) {
				JsonObject carObject = new JsonObject();
				carObject.addProperty("x", car.x);
				carObject.addProperty("y", car.y);
				carObject.addProperty("variant", car.variant.name());
				carObject.addProperty("rotation", car.rotation.name());

				carArray.add(carObject);
			}
			jsonObject.add("cars", carArray);

			if (puzzle.solution == null) {
				jsonObject.add("solution", new JsonArray());
			} else {
				jsonObject.add("solution", puzzle.solution.toJsonArray());
			}

			return jsonObject;
		}

	}

}
