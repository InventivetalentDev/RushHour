package org.inventivetalent.rushhour;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.rushhour.inventory.InventoryGenerator;

import java.io.File;
import java.io.IOException;

public class RushHour extends JavaPlugin {

	File puzzleFolder = new File(getDataFolder(), "puzzles");

	@Override
	public void onEnable() {
		saveDefaultConfig();

		if (!puzzleFolder.exists()) {
			puzzleFolder.mkdirs();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		InventoryGenerator generator = new InventoryGenerator();
		generator.generateBase();
		try {
			generator.loadPuzzle(new File(puzzleFolder, "original/2.rh"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		generator.showTo((Player) sender);
		return false;
	}
}
