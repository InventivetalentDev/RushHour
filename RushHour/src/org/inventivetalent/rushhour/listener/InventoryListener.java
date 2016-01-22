package org.inventivetalent.rushhour.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.MetadataValue;
import org.inventivetalent.rushhour.event.PlayerBeginPuzzleEvent;
import org.inventivetalent.rushhour.event.PlayerFinishPuzzleEvent;
import org.inventivetalent.rushhour.puzzle.generator.AbstractPuzzleGenerator;
import org.inventivetalent.rushhour.puzzle.generator.inventory.InventoryGenerator;

import java.util.List;

public class InventoryListener implements Listener {

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		Inventory inventory = event.getInventory();

		System.out.println("InventoryClose");

		if (player.hasMetadata("RUSHHOUR_GENERATOR")) {
			System.out.println("has meta");

			List<MetadataValue> values = player.getMetadata("RUSHHOUR_GENERATOR");
			if (values.isEmpty()) {
				return;
			}
			AbstractPuzzleGenerator generator = (AbstractPuzzleGenerator) values.get(0).value();
			System.out.println(generator);
			if (generator instanceof InventoryGenerator) {
				((InventoryGenerator) generator).puzzle.puzzleFinished(false);
			}
		}
	}

	//TODO: Remove test events

	@EventHandler
	public void onBegin(PlayerBeginPuzzleEvent event) {
		System.out.println(event);
		System.out.println(event.getPlayer());
		System.out.println(event.getPuzzle());
	}

	@EventHandler
	public void onFinish(PlayerFinishPuzzleEvent event) {
		System.out.println(event);
		System.out.println(event.getPlayer());
		System.out.println(event.getPuzzle());
		System.out.println(event.getMoves());
		System.out.println(event.isSolved());
		System.out.println(event.isUsedSolution());
	}

}
