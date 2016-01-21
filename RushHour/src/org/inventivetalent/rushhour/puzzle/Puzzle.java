package org.inventivetalent.rushhour.puzzle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.menubuilder.MenuBuilderPlugin;
import org.inventivetalent.menubuilder.inventory.ItemListener;
import org.inventivetalent.rushhour.car.Car;
import org.inventivetalent.rushhour.car.Rotation;
import org.inventivetalent.rushhour.car.Variant;
import org.inventivetalent.rushhour.inventory.InventoryGenerator;

import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

public class Puzzle {

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting()/*.excludeFieldsWithModifiers(Modifier.TRANSIENT)*/.create();

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
	public static final Bounds   FINISH_BOUNDS = new Bounds(6, 2, Rotation.HORIZONTAL, 1);

	public String     name;
	public Difficulty difficulty;
	public Set<GameCar> cars = new HashSet<>();

	public InventoryGenerator generator;

	public Puzzle() {
	}

	public void initializeCars() {
		for (final GameCar car : cars) {
			car.bounds = new Bounds(car.x, car.y, car.rotation, car.variant.getLength());
		}
	}

	public void addCarsToInventory(InventoryGenerator generator) {
		this.generator = generator;

		//Clear all old listeners
		MenuBuilderPlugin.instance.inventoryListener.unregisterAllListeners(generator.menuBuilder.getInventory());
		//Remove all old items
		generator.menuBuilder.getInventory().clear();

		generator.generateBase();

		for (final GameCar car : cars) {
			final int length = car.variant.getLength();
			for (int i = 0; i < length; i++) {
				final int j = i;
				final int x = car.bounds.x() + (car.rotation.getShiftX() * i);
				final int y = car.bounds.y() + (car.rotation.getShiftY() * i);

				if ((x < 0 || y < 0) || (x > 5 || y > 5)) {
					//Exception for the exit
					if (x != 6 || y != 2) { continue; }
				}

				generator.setCar(x, y, car.variant.getColor(), new ItemListener() {
					@Override
					public void onInteract(Player player, ClickType clickType, ItemStack itemStack) {
						if (j == 0) {//Move left/up
							if (car.rotation == Rotation.HORIZONTAL) {//Left
								moveCar(car, Direction.LEFT);
							} else if (car.rotation == Rotation.VERTICAL) {//Up
								moveCar(car, Direction.UP);
							}
						} else if (j == length - 1) {//Move right/down
							if (car.rotation == Rotation.HORIZONTAL) {//Right
								moveCar(car, Direction.RIGHT);
							} else if (car.rotation == Rotation.VERTICAL) {//Down
								moveCar(car, Direction.DOWN);
							}
						}
					}
				});
			}
		}

		generator.menuBuilder.refreshContent();
	}

	public void moveCar(GameCar car, Direction direction) {
		Bounds prevBounds = car.bounds;
		Bounds target = car.bounds.shift(direction);
		if (!checkCollision(car, target)) {
			return;
		}
		car.bounds = target;
		//		car.currentX = car.currentX + direction.getShiftX();
		//		car.currentY = car.currentY + direction.getShiftY();

		System.out.println("Car " + car + " moved " + direction + " (from " + prevBounds.x() + "|" + prevBounds.y() + " to " + target.x() + "|" + target.y() + " )");

		if (FINISH_BOUNDS.collidesWith(target)) {
			if (car.variant == Variant.MAIN) {
				System.out.println("Puzzle finished!!!");
				//TODO
			} else {
				throw new IllegalStateException("Car manged to reach finish position but it's not the MAIN variant!");
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
				System.out.println("Car collision");
				return false;
			}
		}

		return true;
	}

	public void toJson(Writer writer) {
		GSON.toJson(this, writer);
	}

	public static Puzzle fromJson(Reader reader) {
		return GSON.fromJson(reader, Puzzle.class);
	}

}
