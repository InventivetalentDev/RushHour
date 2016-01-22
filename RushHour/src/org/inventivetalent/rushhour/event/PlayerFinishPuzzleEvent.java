package org.inventivetalent.rushhour.event;

import org.bukkit.entity.Player;
import org.inventivetalent.rushhour.puzzle.Puzzle;
import org.inventivetalent.rushhour.puzzle.solution.Solution;

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

	public Player getPlayer() {
		return player;
	}

	public Puzzle getPuzzle() {
		return puzzle;
	}

	public int getMoves() {
		return moves;
	}

	public boolean isSolved() {
		return solved;
	}

	public boolean isUsedSolution() {
		return usedSolution;
	}

	public Solution getPlayerSolution() {
		return playerSolution;
	}

	public Solution getPuzzleSolution() {
		return puzzleSolution;
	}
}
