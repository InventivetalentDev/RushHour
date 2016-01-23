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

package org.inventivetalent.rushhour.puzzle.solution;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.rushhour.car.Variant;
import org.inventivetalent.rushhour.puzzle.Direction;
import org.inventivetalent.rushhour.puzzle.Puzzle;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Solution {

	public static Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Solution.class, new Solution.Serializer()).create();

	public final List<Move> moves = new ArrayList<>();

	public void trackPlayerMove(Player player, Move move) {
		move.timestamp = System.currentTimeMillis();
		moves.add(move);
	}

	public void trackPlayerMove(Player player, Variant variant, Direction direction, int moves) {
		trackPlayerMove(player, new Move(variant, direction, moves));
	}

	public void solve(final Puzzle puzzle, long interval) {
		puzzle.isSolving = true;

		//Reset the cars to their original positions
		puzzle.initializeCars();

		new BukkitRunnable() {
			final List<Move> moveList = new ArrayList<>(moves);
			int currentMove = 0;
			int currentMoveStep = 0;

			@Override
			public void run() {
				if (currentMove >= moveList.size()) {
					cancel();
					return;
				}

				Move move = moveList.get(currentMove);
				if (currentMoveStep >= move.moves) {
					currentMove += 1;
					currentMoveStep = 0;
					return;
				}

				move.executeSingleMove(puzzle);
				currentMoveStep += 1;
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("RushHour"), interval, interval);
	}

	/**
	 * Combines moves of the same car and returns the "new" Solution
	 * (e.g. CL1, CL1, CL2 --> CL4)
	 */
	public Solution combineMoves() {
		Solution solution = new Solution();

		List<Move> moveList = new ArrayList<>(this.moves);
		List<Move> newMoveList = new ArrayList<>();

		Move lastMove = null;
		for (Move move : moveList) {
			if (lastMove == null) {
				//First move, so just continue
				lastMove = move;
				continue;
			}

			if (move.variant == lastMove.variant) {
				if (move.direction == lastMove.direction) {
					//It's the same
					//Update the last move by adding both move amounts together
					lastMove = new Move(move.variant, move.direction, lastMove.moves + move.moves);
					continue;
				}
			}

			//The moves are not equal, so add the last move to the new list
			newMoveList.add(lastMove);
			//And set the lastMove to the current one
			lastMove = move;
		}
		//Add the VERY last move to the list
		if (lastMove != null) { newMoveList.add(lastMove); }

		solution.moves.addAll(newMoveList);
		return solution;
	}

	public long getDuration() {
		if (this.moves.isEmpty()) { return 0L; }
		long start = this.moves.get(0).timestamp;
		long end = this.moves.get(this.moves.size() - 1).timestamp;
		return end - start;
	}

	public JsonArray toJsonArray() {
		JsonArray array = new JsonArray();
		for (Move move : this.moves) {
			array.add(new JsonPrimitive(move.asString()));
		}
		return array;
	}

	@Override
	public String toString() {
		return toJsonArray().toString();
	}

	public static class Serializer implements JsonDeserializer<Solution>, JsonSerializer<Solution> {

		@Override
		public Solution deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			Solution solution = new Solution();
			if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				for (JsonElement element : jsonArray) {
					Move move;
					if (element.isJsonObject()) {
						move = new Gson().fromJson(element, Move.class);
					} else {
						move = Move.parse(element.getAsString());
					}

					solution.moves.add(move);
				}
			}
			return solution;
		}

		@Override
		public JsonElement serialize(Solution solution, Type type, JsonSerializationContext jsonSerializationContext) {
			return solution.toJsonArray();
		}
	}

}
