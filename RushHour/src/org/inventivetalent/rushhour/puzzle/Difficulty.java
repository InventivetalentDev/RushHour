package org.inventivetalent.rushhour.puzzle;

import org.bukkit.ChatColor;

public enum Difficulty {

	BEGINNER(ChatColor.GREEN),
	INTERMEDIATE(ChatColor.YELLOW),
	ADVANCED(ChatColor.BLUE),
	EXPERT(ChatColor.RED);

	private ChatColor color;

	Difficulty(ChatColor color) {
		this.color = color;
	}

	public ChatColor getColor() {
		return color;
	}
}
