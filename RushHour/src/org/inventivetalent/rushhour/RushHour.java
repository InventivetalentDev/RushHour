/*
 * Copyright 2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */

package org.inventivetalent.rushhour;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.messagebuilder.MessageBuilder;
import org.inventivetalent.messagebuilder.MessageContainer;
import org.inventivetalent.rushhour.listener.InventoryListener;
import org.inventivetalent.rushhour.puzzle.Puzzle;
import org.inventivetalent.rushhour.puzzle.generator.AbstractPuzzleGenerator;
import org.inventivetalent.rushhour.puzzle.generator.inventory.InventoryGenerator;
import org.inventivetalent.rushhour.puzzle.solution.Solution;
import org.inventivetalent.rushhour.score.Score;
import org.inventivetalent.rushhour.score.ScoreListener;
import org.inventivetalent.rushhour.score.ScoreManager;
import org.inventivetalent.rushhour.score.local.PlayerScore;
import org.inventivetalent.rushhour.sign.SignListener;
import org.mcstats.MetricsLite;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class RushHour extends JavaPlugin {

	File puzzleFolder = new File(getDataFolder(), "puzzles");
	File messagesFile = new File(getDataFolder(), "messages.yml");

	public static Material CAR_MATERIAL = Material.STAINED_CLAY;

	public static int    SIGN_LEVEL_LINE  = 1;
	public static int    SIGN_ACTION_LINE = 2;
	public static String SIGN_KEY_PLAY    = "play";
	public static String SIGN_KEY_STATS   = "stats";

	public static boolean LOCAL_STATS_ENABLED = true;

	public static SimpleDateFormat DATE_FORMAT;

	public static long SOLUTION_INTERVAL = 20;

	public static MessageContainer messageContainer;

	public ScoreManager scoreManager;

	@Override
	public void onEnable() {
		//ItemBuilder and MessageBuilder are included in the jar file, so just check if the MenuBuilder plugin is enabled
		if (!Bukkit.getPluginManager().isPluginEnabled("MenuBuilder") || !doesClassExist("org.inventivetalent.menubuilder.MenuBuilder") || !doesClassExist("org.inventivetalent.itembuilder.ItemBuilder") || !doesClassExist("org.inventivetalent.messagebuilder.MessageBuilder")) {
			getLogger().severe("*******************************************");
			getLogger().severe("                                           ");
			getLogger().severe("          This plugin depends on           ");
			getLogger().severe(" MenuBuilder, ItemBuilder & MessageBuilder ");
			getLogger().severe("         please download them here:        ");
			getLogger().severe("  https://r.spiget.org/12995/12502/12821   ");
			getLogger().severe("                                           ");
			getLogger().severe("*******************************************");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		saveDefaultConfig();

		CAR_MATERIAL = Material.valueOf(getConfig().getString("puzzle.inventory.car.material"));
		SIGN_LEVEL_LINE = getConfig().getInt("sign.levelLine");
		SIGN_ACTION_LINE = getConfig().getInt("sign.actionLine");
		SIGN_KEY_PLAY = getConfig().getString("sign.key.play");
		SIGN_KEY_STATS = getConfig().getString("sign.key.stats");
		LOCAL_STATS_ENABLED = getConfig().getBoolean("stats.local.enabled");
		DATE_FORMAT = new SimpleDateFormat(getConfig().getString("dateFormat"));
		SOLUTION_INTERVAL = getConfig().getLong("solutionInterval");

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
				.withMessage("command.play.help", "§aPlay a specific level")//
				.withMessage("command.play.info.loading", "§aLoading level %s...")//
				.withMessage("command.play.error.level.missing", "§cPlease specify the level name")//
				.withMessage("command.play.error.level.notFound", "§cLevel could not be found")//
				.withMessage("command.play.error.noPlayer", "§cYou must be a player to play")//
				.withMessage("command.play.error.permission.command", "§cYou are not permitted to use this command")//
				.withMessage("command.play.error.permission.level", "§cYou are not permitted to play this level")//

				.withMessage("command.stats.info.stats.levels.played.self", "§eYou already played these levels:")//
				.withMessage("command.stats.info.stats.levels.played.other", "§e%s already played these levels:")//
				.withMessage("command.stats.level.name", "§7Level name: §e%s")//
				.withMessage("command.stats.level.difficulty", "§7Difficulty: §e%s")//
				.withMessage("command.stats.level.played.amount", "§7Times played: §e%s")//
				.withMessage("command.stats.level.time.best", "§7Best time: §e%d:%02d:%02d.%d §7(%s)")//
				.withMessage("command.stats.level.solution.best", "§7Shortest solution: §e%s Moves §7(%s individual moves)")//
				.withMessage("command.stats.info.loading.general", "§eLoading stats...")//
				.withMessage("command.stats.info.loading.level", "§eLoading stats for %s....")//
				.withMessage("command.stats.info.loading.player.general", "§eLoading stats for %s...")//
				.withMessage("command.stats.info.loading.player.level", "§eLoading stats for %s-%s...")//
				.withMessage("command.stats.error.notEnabled", "&cLocal stats are not enabled")//
				.withMessage("command.stats.error.noPlayer", "§cYou must be a player to view stats")//
				.withMessage("command.stats.error.player.notFound", "§cPlayer not found")//
				.withMessage("command.stats.error.permission.command", "§cYou are not permitted to view stats")//
				.withMessage("command.stats.error.permission.other", "§cYou are not permitted to view stats of other players")//
				.withMessage("command.stats.error.level.notFound", "§cLevel could not be found")//
				.withMessage("command.stats.error.level.notPlayed", "§cYou haven't played this level")//

				.withMessage("command.spectate.error.noPlayer", "§cYou must be a player to spectate")//
				.withMessage("command.spectate.error.permission.command", "§cYou are not permitted to spectate")//
				.withMessage("command.spectate.error.notPlaying", "§c%s is not playing a puzzle right now")//
				.withMessage("command.spectate.error.player.missing", "§cPlease specify the player you want to spectate")//
				.withMessage("command.spectate.error.player.notFound", "§cPlayer not found")//
				.withMessage("command.spectate.info.spectating", "§eYou are now spectating %s")//

				.withMessage("inventory.title", "§c§lRush§e§lHour")//
				.withMessage("inventory.game.finished.inner", " §2Game Finished! ")//
				.withMessage("inventory.game.finished.outer", " §aGame Finished! ")//
				.withMessage("inventory.game.finished.time", "§7You finished this puzzle in §e%d:%02d:%02d§7!")//
				.withMessage("inventory.game.solution.show", "§aShow solution")//
				.withMessage("inventory.game.moves", "§7Moves: §e%s (%s)")//
				.withMessage("inventory.game.level.name", "§7Level: §e%s")//
				.withMessage("inventory.game.level.difficulty", "§7Difficulty: %s")//
				.withMessage("inventory.game.move.disabled.left", " §7< ")//
				.withMessage("inventory.game.move.disabled.right", " §7> ")//
				.withMessage("inventory.game.move.disabled.up", " §7^ ")//
				.withMessage("inventory.game.move.disabled.down", " §7v ")//
				.withMessage("inventory.game.move.enabled.left", " §e< ")//
				.withMessage("inventory.game.move.enabled.right", " §e> ")//
				.withMessage("inventory.game.move.enabled.up", " §e^ ")//
				.withMessage("inventory.game.move.enabled.down", " §ev ")//

				.withMessage("solution.beginner.name", "§2BEGINNER")//
				.withMessage("solution.intermediate.name", "§6INTERMEDIATE")//
				.withMessage("solution.advanced.name", "§9ADVANCED")//
				.withMessage("solution.expert.name", "§cEXPERT")//

				.withMessage("solution.error.permission.level", "§cYou are not permitted to view the solution for this level")//
				.withMessage("solution.error.missing", "§cSorry, I don't know how to solve this level")//
				.withMessage("solution.info.solving", "§ePlaying solution...")//

				.withMessage("sign.title", "§7[§c§lRush§eHour§7]")//
				.withMessage("sign.created", "§aCreated sign for §e%s")//
				.withMessage("sign.error.permission.create", "§cYou are not permitted to create signs")//
				.withMessage("sign.error.permission.use", "§cYou are not permitted to use signs")//
				.withMessage("sign.error.level.missing", "§cPlease specify the level of this sign")//

				.fromConfig(YamlConfiguration.loadConfiguration(messagesFile)).build();

		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
		Bukkit.getPluginManager().registerEvents(new SignListener(), this);

		if (LOCAL_STATS_ENABLED) {
			scoreManager = new ScoreManager(this);
			Bukkit.getPluginManager().registerEvents(new ScoreListener(this), this);
		}

		try {
			MetricsLite metrics = new MetricsLite(this);
			if (metrics.start()) {
				getLogger().info("Metrics started");
			}
		} catch (Exception e) {
		}
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
			if (sender.hasPermission("rushhour.stats") && LOCAL_STATS_ENABLED) {
				sender.sendMessage("  ");
				sender.sendMessage("§aShow your stats");
				sender.sendMessage("§e/rushhour stats [level]");

				if (sender.hasPermission("rushhour.stats.other")) {
					sender.sendMessage("  ");
					sender.sendMessage("§aShow another player's stats");
					sender.sendMessage("§e/rushhour stats <player> [level]");
				}
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

			if (!player.hasPermission("rushhour.play." + levelName.replace(File.separator, "."))) {
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

		if ("spectate".equalsIgnoreCase(args[0])) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(messageContainer.getMessage("command.spectate.error.noPlayer"));
				return false;
			}
			if (!sender.hasPermission("rushhour.spectate")) {
				sender.sendMessage(messageContainer.getMessage("command.spectate.error.permission.command"));
				return false;
			}
			if (args.length == 1) {
				sender.sendMessage(messageContainer.getMessage("command.spectate.error.player.missing"));
				return false;
			}
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null || !target.isOnline()) {
				sender.sendMessage(messageContainer.getMessage("command.spectate.error.player.notFound"));
				return false;
			}
			if (!target.hasMetadata("RUSHHOUR_PUZZLE") || !target.hasMetadata("RUSHHOUR_GENERATOR")) {
				sender.sendMessage(messageContainer.getMessage("command.spectate.error.notPlaying", sender.getName()));
				return false;
			}
			List<MetadataValue> values = target.getMetadata("RUSHHOUR_GENERATOR");
			if (values.isEmpty()) {
				sender.sendMessage(messageContainer.getMessage("command.spectate.error.notPlaying", target.getName()));
				return false;
			}
			Object value = values.get(0).value();
			if (value instanceof AbstractPuzzleGenerator) {
				AbstractPuzzleGenerator generator = (AbstractPuzzleGenerator) value;
				generator.spectate((Player) sender);

				sender.sendMessage(messageContainer.getMessage("command.spectate.info.spectating", target.getName()));
				return true;
			}
			sender.sendMessage(messageContainer.getMessage("command.spectate.error.notPlaying"));
			return false;
		}

		if ("stats".equalsIgnoreCase(args[0])) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(messageContainer.getMessage("command.stats.error.noPlayer"));
				return false;
			}
			if (!sender.hasPermission("rushhour.stats")) {
				sender.sendMessage(messageContainer.getMessage("command.stats.error.permission.command"));
				return false;
			}
			if (!LOCAL_STATS_ENABLED) {
				sender.sendMessage(messageContainer.getMessage("commands.stats.error.notEnabled"));
				return false;
			}
			Player player = null;
			File levelFile = null;
			String levelName = null;
			if (args.length == 1) {// /rh stats
				player = (Player) sender;
			} else if (args.length == 2) {
				Player player1 = Bukkit.getPlayer(args[1]);
				if (player1 != null) {// /rh stats <player name>
					player = player1;
					if (!sender.hasPermission("rushhour.stats.other")) {
						sender.sendMessage(messageContainer.getMessage("commands.stats.error.permission.other"));
						return false;
					}
				} else {// /rh stats <level name>
					player = (Player) sender;

					//Level name (first with optional extension)
					String level = args[1];
					//Level name without extension
					levelName = level;
					if (level.endsWith(".rh")) {
						levelName = level.substring(0, level.length() - ".rh".length());
					} else {
						levelName = level;
						level = level + ".rh";
					}

					levelFile = new File(puzzleFolder, level);
					if (!levelFile.exists()) {
						sender.sendMessage(messageContainer.getMessage("command.stats.error.level.notFound"));
						return false;
					}
				}
			} else if (args.length == 3) {// /rh stats <player name> <level name>
				player = Bukkit.getPlayer(args[1]);
				if (player == null) {
					sender.sendMessage(messageContainer.getMessage("command.stats.error.player.notFound"));
					return false;
				}

				//Level name (first with optional extension)
				String level = args[2];
				//Level name without extension
				levelName = level;
				if (level.endsWith(".rh")) {
					levelName = level.substring(0, level.length() - ".rh".length());
				} else {
					levelName = level;
					level = level + ".rh";
				}

				levelFile = new File(puzzleFolder, level);
				if (!levelFile.exists()) {
					sender.sendMessage(messageContainer.getMessage("command.stats.error.level.notFound"));
					return false;
				}
			}

			if (player == sender) {// /rh stats | /rh stats <level name>
				if (levelFile == null) {// /rh stats
					sender.sendMessage(messageContainer.getMessage("command.stats.info.loading.general"));

					sender.sendMessage(messageContainer.getMessage("command.stats.info.stats.levels.played.self"));
					for (String level : this.scoreManager.getLocalScoreManager().getPlayedPuzzleNames(player)) {
						sender.sendMessage("§e- " + level);
					}
					return true;
				} else {// /rh stats <level name>
					sender.sendMessage(messageContainer.getMessage("command.stats.info.loading.level", levelFile.getName()));
				}
			} else {// /rh stats <player name> | /rh stats <player name> <level name>
				if (levelFile == null) {// /rh stats <player name>
					sender.sendMessage(messageContainer.getMessage("command.stats.info.loading.player.general", player.getName()));

					sender.sendMessage(messageContainer.getMessage("command.stats.info.stats.levels.played.other", player.getName()));
					for (String level : this.scoreManager.getLocalScoreManager().getPlayedPuzzleNames(player)) {
						sender.sendMessage("§e- " + level);
					}
					return true;
				} else {// /rh stats <player name> <level name>
					sender.sendMessage(messageContainer.getMessage("command.stats.info.loading.player.level", player.getName(), levelFile.getName()));
				}
			}

			//Level info doesn't need specific self/other messages

			Puzzle puzzle;
			try {
				puzzle = Puzzle.fromJson(new FileReader(levelFile));
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, "Error while loading puzzle", e);
				return false;
			}
			if (puzzle.name == null) { puzzle.name = levelName; }

			List<Score> scores = this.scoreManager.getLocalScoreManager().getScores(player, levelName);
			if (scores.isEmpty()) {
				sender.sendMessage(messageContainer.getMessage("command.stats.error.level.notPlayed"));
				return false;
			}

			long bestTime = Long.MAX_VALUE;
			Score bestTimeScore = null;

			int bestSolutionLength = Integer.MAX_VALUE;
			Solution bestSolution = null;

			for (Score score : scores) {
				if (score.duration < bestTime) {
					bestTime = score.duration;
					bestTimeScore = score;
				}

				if (score.solution.moves.size() < bestSolutionLength) {
					bestSolutionLength = score.solution.moves.size();
					bestSolution = score.solution;
				}
			}

			sender.sendMessage(messageContainer.getMessage("command.stats.info.stats.level.name", puzzle.name));
			sender.sendMessage(messageContainer.getMessage("command.stats.info.stats.level.difficulty", puzzle.difficulty));
			sender.sendMessage(messageContainer.getMessage("command.stats.info.stats.level.played.amount", scores.size()));
			sender.sendMessage(messageContainer.getMessage("command.stats.info.stats.level.time.best", (int) ((bestTime / 1000) / 3600), (int) (((bestTime / 1000) % 3600) / 60), (int) ((bestTime / 1000) % 60), bestTime, DATE_FORMAT.format(bestTimeScore != null ? bestTimeScore.timestamp : 0L)));
			sender.sendMessage(messageContainer.getMessage("command.stats.info.stats.level.solution.best", (bestSolution != null ? bestSolution.combineMoves().moves.size() : 0), bestSolutionLength));

			return true;
		}

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		ArrayList<String> list = new ArrayList<>();
		if (!(sender instanceof Player)) { return list; }

		if (args.length == 1) {
			if (sender.hasPermission("rushhour.play")) {
				list.add("play");
			}
			if (sender.hasPermission("rushhour.spectate")) {
				list.add("spectate");
			}
			if (sender.hasPermission("rushhour.stats")) {
				list.add("stats");
			}
		}
		if (args.length >= 2) {
			if ("play".equalsIgnoreCase(args[0])) {
				if (sender.hasPermission("rushhour.play")) {
					for (Iterator<File> iterator = FileUtils.iterateFiles(puzzleFolder, null, true); iterator.hasNext(); ) {
						File file = iterator.next();
						if (file.getName().endsWith(".rh")) {
							String puzzleName = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(File.separator + "plugins" + File.separator + "RushHour" + File.separator + "puzzles" + File.separator) + (File.separator + "plugins" + File.separator + "RushHour" + File.separator + "puzzles" + File.separator).length());
							puzzleName = puzzleName.substring(0, puzzleName.length() - ".rh".length());
							list.add(puzzleName);
						}
					}
				}
			}
			if ("spectate".equalsIgnoreCase(args[0])) {
				if (sender.hasPermission("rushhour.spectate")) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (((Player) sender).canSee(player)) {
							if (player.hasMetadata("RUSHHOUR_PUZZLE")) {
								list.add(player.getName());
							}
						}
					}
				}
			}
			if ("stats".equalsIgnoreCase(args[0])) {
				if (sender.hasPermission("rushhour.stats")) {
					List<String> levels = this.scoreManager.getLocalScoreManager().getPlayedPuzzleNames((Player) sender);
					list.addAll(levels);

					if (args.length == 2 && sender.hasPermission("rushhour.stats.other")) {
						for (Player player : Bukkit.getOnlinePlayers()) {
							if (((Player) sender).canSee(player)) {
								list.add(player.getName());
							}
						}
					}
				}
			}
		}

		return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, list.toArray(new String[list.size()]));
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

	boolean doesClassExist(String name) {
		try {
			return Class.forName(name) != null;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<>();
		if (LOCAL_STATS_ENABLED) {
			list.add(PlayerScore.class);
		}
		return list;
	}

	@Override
	public void installDDL() {
		super.installDDL();
	}
}
