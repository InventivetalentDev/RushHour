package org.inventivetalent.rushhour;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.messagebuilder.MessageBuilder;
import org.inventivetalent.messagebuilder.MessageContainer;
import org.inventivetalent.rushhour.listener.InventoryListener;
import org.inventivetalent.rushhour.puzzle.generator.inventory.InventoryGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RushHour extends JavaPlugin {

	File puzzleFolder = new File(getDataFolder(), "puzzles");
	File messagesFile = new File(getDataFolder(), "messages.yml");

	public static MessageContainer messageContainer;

	@Override
	public void onEnable() {
		saveDefaultConfig();

		if (!puzzleFolder.exists()) {
			//Save the included puzzles
			try {
				copyResource("puzzles");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!messagesFile.exists()) {
			saveResource("messages.yml", false);
		}

		messageContainer = new MessageBuilder()//
				.withMessage("command.play.help", "&aPlay a specific level")//
				.withMessage("command.play.info.loading", "&aLoading level %s...")//
				.withMessage("command.play.error.level.missing", "&cPlease specify the level name")//
				.withMessage("command.play.error.level.notFound", "&cLevel could not be found")//
				.withMessage("command.play.error.noPlayer", "&cYou must be a player to play")//
				.withMessage("command.play.error.permission.command", "&cYou are not permitted to use this command")//
				.withMessage("command.play.error.permission.level", "&cYou are not permitted to play this level")//

				.withMessage("inventory.title", "&c&lRush&e&lHour  &8\"%s\"&r  %s")//
				.withMessage("inventory.game.finished.inner", " &2Game Finished! ")//
				.withMessage("inventory.game.finished.outer", " &aGame Finished! ")//
				.withMessage("inventory.game.finished.time", "&7You finished this puzzle in &e%s&7!")//
				.withMessage("inventory.game.solution.show", "&aShow solution")//
				.withMessage("inventory.game.moves", "&7Moves: &e%s")//
				.withMessage("inventory.game.move.disabled.left", " &7< ")//
				.withMessage("inventory.game.move.disabled.right", " &7> ")//
				.withMessage("inventory.game.move.disabled.up", " &7^ ")//
				.withMessage("inventory.game.move.disabled.down", " &7v ")//
				.withMessage("inventory.game.move.enabled.left", " &e< ")//
				.withMessage("inventory.game.move.enabled.right", " &e> ")//
				.withMessage("inventory.game.move.enabled.up", " &e^ ")//
				.withMessage("inventory.game.move.enabled.down", " &ev ")//

				.fromConfig(YamlConfiguration.loadConfiguration(messagesFile)).build();

		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			if (sender.hasPermission("rushhour.play")) {
				sender.sendMessage("  ");
				sender.sendMessage(messageContainer.getMessage("command.play.help"));
				sender.sendMessage("§e/rushhour play <level name>");
			}
			if (sender.hasPermission("rushhour.spectate")) {
				sender.sendMessage("  ");
				sender.sendMessage("§aSpectate a player");
				sender.sendMessage("§e/rushhour spectate <player>");
			}

			return true;
		}

		if ("play".equalsIgnoreCase(args[0])) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(messageContainer.getMessage("command.play.error.noPlayer"));
				return false;
			}
			Player player = (Player) sender;

			if (!player.hasPermission("rushhour.play")) {
				sender.sendMessage(messageContainer.getMessage("command.play.error.permission.command"));
				return false;
			}

			if (args.length == 1) {
				sender.sendMessage(messageContainer.getMessage("command.play.error.level.missing"));
				return false;
			}
			//Level name (first with optional extension)
			String level = args[1];
			//Level name without extension
			String levelName = level;
			if (level.endsWith(".rh")) {
				levelName = level.substring(0, level.length() - ".rh".length());
			} else {
				levelName = level;
				level = level + ".rh";
			}

			sender.sendMessage(messageContainer.getMessage("command.play.info.loading", level));

			File levelFile = new File(puzzleFolder, level);
			if (!levelFile.exists()) {
				sender.sendMessage(messageContainer.getMessage("command.play.error.level.notFound"));
				return false;
			}

			if (!player.hasPermission("rushhour.play." + levelName.replace("/", "."))) {
				sender.sendMessage(messageContainer.getMessage("command.play.error.permission.level"));
				return false;
			}

			InventoryGenerator generator = new InventoryGenerator();
			generator.generateBase();
			try {
				generator.loadPuzzle(levelFile);
			} catch (IOException e) {
				sender.sendMessage("§cError while loading level");
				e.printStackTrace();
				return false;
			}

			generator.showTo(player);
			return true;
		}

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
