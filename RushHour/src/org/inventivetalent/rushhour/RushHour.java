package org.inventivetalent.rushhour;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.rushhour.listener.InventoryListener;
import org.inventivetalent.rushhour.puzzle.generator.inventory.InventoryGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RushHour extends JavaPlugin {

	File puzzleFolder = new File(getDataFolder(), "puzzles");

	@Override
	public void onEnable() {
		saveDefaultConfig();

		if (!puzzleFolder.exists()) {
			//			puzzleFolder.mkdirs();

			//Save the included puzzles
			try {
				copyResource("puzzles");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		final InventoryGenerator generator = new InventoryGenerator();
		generator.generateBase();
		try {
			generator.loadPuzzle(new File(puzzleFolder, "original/2.rh"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				System.out.println("Solving puzzle...");
				//				generator.puzzle.solution.solve(generator.puzzle, 10);
			}
		}, 80);

		generator.showTo((Player) sender);
		return false;
	}

	//https://stackoverflow.com/questions/11012819/how-can-i-get-a-resource-folder-from-inside-my-jar-file
	public void copyResource(String path) throws IOException {
		final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) {  // Run with JAR file
			final JarFile jar = new JarFile(jarFile);
			final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			while (entries.hasMoreElements()) {
				final String name = entries.nextElement().getName();
				if (name.startsWith(path + "/")) { //filter according to the path
					saveResource(name, false);
				}
			}
			jar.close();
		}
	}

}
