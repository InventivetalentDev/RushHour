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
