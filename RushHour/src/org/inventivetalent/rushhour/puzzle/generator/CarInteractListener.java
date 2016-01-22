package org.inventivetalent.rushhour.puzzle.generator;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface CarInteractListener {

	void onInteract(Player player, ClickType clickType);

}
