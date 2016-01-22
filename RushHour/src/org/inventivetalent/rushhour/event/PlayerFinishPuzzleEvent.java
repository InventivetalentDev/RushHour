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

package org.inventivetalent.rushhour.event;

import org.bukkit.entity.Player;
import org.inventivetalent.rushhour.puzzle.Puzzle;
import org.inventivetalent.rushhour.puzzle.solution.Solution;

/**
 * Event called when a player finished/cancelled a puzzle
 */
public class PlayerFinishPuzzleEvent extends RushHourEvent {

	private Player   player;
	private Puzzle   puzzle;
	private int      moves;
	private boolean  solved;
	private boolean  usedSolution;
	private Solution playerSolution;
	private Solution puzzleSolution;

	public PlayerFinishPuzzleEvent(Player player, Puzzle puzzle, int moves, boolean solved, boolean usedSolution, Solution playerSolution, Solution puzzleSolution) {
		this.player = player;
		this.puzzle = puzzle;
		this.moves = moves;
		this.solved = solved;
		this.usedSolution = usedSolution;
		this.playerSolution = playerSolution;
		this.puzzleSolution = puzzleSolution;
	}

	/**
	 * @return The player involved
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return The finished puzzle
	 */
	public Puzzle getPuzzle() {
		return puzzle;
	}

	/**
	 * @return The amount of moves until the puzzle was finished
	 */
	public int getMoves() {
		return moves;
	}

	/**
	 * @return Whether the puzzle has been successfully solved
	 */
	public boolean isSolved() {
		return solved;
	}

	/**
	 * @return <code>true</code> if the player used the solution to solve the puzzle, <code>false</code> if the player solved the puzzle by themselves
	 */
	public boolean isUsedSolution() {
		return usedSolution;
	}

	/**
	 * @return The solution the player found
	 */
	public Solution getPlayerSolution() {
		return playerSolution;
	}

	/**
	 * @return The "combined" solution the player found (after calling {@link Solution#combineMoves()} on the solution)
	 */
	public Solution getCombinedPlayerSolution() {
		return getPlayerSolution().combineMoves();
	}

	/**
	 * @return The solution which is pre-defined for this puzzle
	 */
	public Solution getPuzzleSolution() {
		return puzzleSolution;
	}
}
