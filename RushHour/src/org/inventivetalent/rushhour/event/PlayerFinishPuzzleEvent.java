package org.inventivetalent.rushhour.event;

import org.bukkit.entity.Player;
import org.inventivetalent.rushhour.puzzle.Puzzle;

public class PlayerFinishPuzzleEvent extends RushHourEvent {

	private Player  player;
	private Puzzle  puzzle;
	private int     moves;
	private boolean solved;
	private boolean usedSolution;

	private boolean cancelled;

	public PlayerFinishPuzzleEvent(Player player, Puzzle puzzle, int moves, boolean solved, boolean usedSolution) {
		this.player = player;
		this.puzzle = puzzle;
		this.moves = moves;
		this.solved = solved;
		this.usedSolution = usedSolution;
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
}
