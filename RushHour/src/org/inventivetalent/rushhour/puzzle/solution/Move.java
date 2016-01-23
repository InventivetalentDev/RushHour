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

import org.inventivetalent.rushhour.car.Variant;
import org.inventivetalent.rushhour.exception.InvalidSolutionException;
import org.inventivetalent.rushhour.puzzle.Direction;
import org.inventivetalent.rushhour.puzzle.GameCar;
import org.inventivetalent.rushhour.puzzle.Puzzle;

public class Move {

	public final Variant   variant;
	public final Direction direction;
	public final int       moves;

	//Only for moves performed by a player
	public long timestamp = 0L;

	public Move(Variant variant, Direction direction, int moves) {
		this.variant = variant;
		this.direction = direction;
		this.moves = moves;
	}

	public Move(char identifier, char code, int moves) {
		this.variant = Variant.getByIdentifier(identifier);
		this.direction = Direction.getByCode(code);
		this.moves = moves;
	}

	public static Move parse(String move) {
		String[] split = move.split("");
		if (split.length != 3) { throw new IllegalArgumentException(); }
		char identifier = split[0].charAt(0);
		char direction = split[1].charAt(0);
		int moves = Integer.parseInt(split[2]);

		return new Move(identifier, direction, moves);
	}

	public String asString() {
		return new StringBuilder().append(this.variant.getIdentifier()).append(this.direction.getCode()).append(this.moves).toString();
	}

	public void executeMove(Puzzle puzzle) throws InvalidSolutionException {
		GameCar targetCar = null;
		for (GameCar car : puzzle.cars) {
			if (car.variant == this.variant) { targetCar = car; }
		}
		if (targetCar == null) { throw new InvalidSolutionException("The puzzle does not contain the car of this move (" + this.variant + ")"); }
		for (int i = 0; i < this.moves; i++) {
			puzzle.moveCar(targetCar, this.direction);
		}
	}

	public void executeSingleMove(Puzzle puzzle) throws InvalidSolutionException {
		GameCar targetCar = null;
		for (GameCar car : puzzle.cars) {
			if (car.variant == this.variant) { targetCar = car; }
		}
		if (targetCar == null) { throw new InvalidSolutionException("The puzzle does not contain the car of this move (" + this.variant + ")"); }
		puzzle.moveCar(targetCar, this.direction);
	}

	@Override
	public String toString() {
		return "Move[ " + asString() + " ]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }

		Move move = (Move) o;

		if (moves != move.moves) { return false; }
		if (timestamp != move.timestamp) { return false; }
		if (variant != move.variant) { return false; }
		return direction == move.direction;

	}

	@Override
	public int hashCode() {
		int result = variant != null ? variant.hashCode() : 0;
		result = 31 * result + (direction != null ? direction.hashCode() : 0);
		result = 31 * result + moves;
		result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}
}
