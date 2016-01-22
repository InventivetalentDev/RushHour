package org.inventivetalent.rushhour.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.inventivetalent.rushhour.puzzle.Puzzle;

public class PlayerBeginPuzzleEvent extends RushHourEvent implements Cancellable {

	private Player player;
	private Puzzle puzzle;

	private boolean cancelled;

	public PlayerBeginPuzzleEvent(Player player, Puzzle puzzle) {
		this.player = player;
		this.puzzle = puzzle;
	}

	public Player getPlayer() {
		return player;
	}

	public Puzzle getPuzzle() {
		return puzzle;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}
}
