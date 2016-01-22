package org.inventivetalent.rushhour.puzzle.generator.inventory;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.inventivetalent.itembuilder.ItemBuilder;
import org.inventivetalent.itembuilder.MetaBuilder;
import org.inventivetalent.menubuilder.MenuBuilderPlugin;
import org.inventivetalent.menubuilder.inventory.InventoryMenuBuilder;
import org.inventivetalent.menubuilder.inventory.ItemListener;
import org.inventivetalent.rushhour.RushHour;
import org.inventivetalent.rushhour.event.PlayerBeginPuzzleEvent;
import org.inventivetalent.rushhour.puzzle.Direction;
import org.inventivetalent.rushhour.puzzle.GameCar;
import org.inventivetalent.rushhour.puzzle.Puzzle;
import org.inventivetalent.rushhour.puzzle.generator.AbstractPuzzleGenerator;
import org.inventivetalent.rushhour.puzzle.generator.CarInteractListener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class InventoryGenerator extends AbstractPuzzleGenerator {

	public static int[] WALL_SLOTS = new int[] {
			/* Left border*/
			0,
			9,
			18,
			27,
			36,
			45,

			/* Right border */
			7,
			8/*Solution item; Keep it anyway, since it will only appear if the player has permissions*/,
			16,
			17,
			/* 25 <- Exit hole */
			34,
			35,
			43,
		/*	44,*/
			52
			/*53*/ };

	public InventoryMenuBuilder menuBuilder;
	public Puzzle               puzzle;

	public int     moveCount = 0;
	public boolean finished  = false;

	public InventoryGenerator() {
		this.menuBuilder = new InventoryMenuBuilder(6 * 9, "§c§lRush§e§lHour");
	}

	public void generateBase() {
		if (this.finished) {
			for (int i = 0; i < menuBuilder.getInventory().getSize(); i++) {
				menuBuilder.withItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, DyeColor.WHITE.getData()).buildMeta().withDisplayName(RushHour.messageContainer.getMessage("inventory.game.finished.inner")).item().build(), new ItemListener() {
					@Override
					public void onInteract(Player player, ClickType clickType, ItemStack itemStack) {
					}
				}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			}
		}

		//Walls
		for (int i : WALL_SLOTS) {
			this.menuBuilder.withItem(i,//
					new ItemBuilder(Material.STAINED_GLASS_PANE, 1, finished ? DyeColor.GREEN.getData() : DyeColor.GRAY.getData()).buildMeta().withDisplayName(finished ? RushHour.messageContainer.getMessage("inventory.game.finished.outer") : " ").item().build(), new ItemListener() {
						@Override
						public void onInteract(Player player, ClickType clickType, ItemStack itemStack) {
						}
					}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		}

		//Level information
		ItemBuilder levelInfoBuilder = new ItemBuilder(Material.SIGN, 1);
		MetaBuilder levelInfoMetaBuilder = new MetaBuilder(levelInfoBuilder).withDisplayName(RushHour.messageContainer.getMessage("inventory.game.level.name", puzzle != null ? puzzle.name : "")).withLore(//
				RushHour.messageContainer.getMessage("inventory.game.level.difficulty", puzzle != null ? puzzle.difficulty : "")//
		);
		levelInfoBuilder = levelInfoMetaBuilder.item();
		this.menuBuilder.withItem(44, levelInfoBuilder.build(), new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType clickType, ItemStack itemStack) {
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);

		//Moves information
		ItemBuilder infoBuilder = new ItemBuilder(Material.WATCH, this.moveCount);
		MetaBuilder infoMetaBuilder = infoBuilder.buildMeta().withDisplayName(RushHour.messageContainer.getMessage("inventory.game.moves", this.moveCount)/*"§7Moves: §e" + this.moveCount*/);
		if (this.finished) {
			double s = this.puzzle.playerSolution.getDuration() / 1000.0D;
			infoMetaBuilder.withLore(RushHour.messageContainer.getMessage("inventory.game.finished.time", (int) (s / 3600), (int) ((s % 3600) / 60), (int) (s % 60))  /*"§7You finished this puzzle in §e" + String.format("%d:%02d:%02d", (int) (s / 3600), (int) ((s % 3600) / 60), (int) (s % 60)) + "§7!"*/);
		}
		infoBuilder = infoMetaBuilder.item();
		this.menuBuilder.withItem(53, infoBuilder.build(), new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType clickType, ItemStack itemStack) {
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);

		//Help item
		if (this.puzzle != null && this.puzzle.player != null && this.puzzle.player.hasPermission("rushhour.solution." + this.puzzle.getLevelPerm())) {
			this.menuBuilder.withItem(8, new ItemBuilder(Material.REDSTONE_TORCH_ON, 1).buildMeta().withDisplayName(RushHour.messageContainer.getMessage("inventory.game.solution.show")).item().build(), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType clickType, ItemStack itemStack) {
					if (!puzzle.player.hasPermission("rushhour.solution." + puzzle.getLevelPerm())) {
						player.sendMessage(RushHour.messageContainer.getMessage("solution.error.permission.level"));
						return;
					}
					if (puzzle.solution == null) {
						player.sendMessage(RushHour.messageContainer.getMessage("solution.error.missing"));
						return;
					}

					player.sendMessage(RushHour.messageContainer.getMessage("solution.info.solving"));
					puzzle.solution.solve(puzzle, 10);
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		}

		if (this.finished) {
			int[] R_SLOTS = new int[] {
					10,
					11,
					12,
					19,
					21,
					28,
					29,
					37,
					39 };
			int[] H_SLOTS = new int[] {
					13,
					15,
					22,
					23,
					24,
					31,
					33,
					40,
					42 };

			for (int r : R_SLOTS) {
				this.menuBuilder.withItem(r, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getData()).buildMeta().withDisplayName(RushHour.messageContainer.getMessage("inventory.game.finished.inner")).item().build());
			}

			for (int h : H_SLOTS) {
				this.menuBuilder.withItem(h, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, DyeColor.YELLOW.getData()).buildMeta().withDisplayName(RushHour.messageContainer.getMessage("inventory.game.finished.inner")).item().build());
			}

		}
	}

	public void loadPuzzle(Puzzle puzzle) {
		this.puzzle = puzzle;
		puzzle.initializeCars();
		puzzle.addCarsToInventory(this);

		//		this.menuBuilder.withTitle(RushHour.messageContainer.getMessage("inventory.title", this.puzzle.name, this.puzzle.difficulty)  /*"§c§lRush§e§lHour  §8\"" + this.puzzle.name + "\"§r  " + this.puzzle.difficulty*/);
		this.menuBuilder.withTitle(RushHour.messageContainer.getMessage("inventory.title")  /*"§c§lRush§e§lHour  §8\"" + this.puzzle.name + "\"§r  " + this.puzzle.difficulty*/);
	}

	public void loadPuzzle(File file) throws IOException {
		Puzzle puzzle = Puzzle.fromJson(new FileReader(file));

		String puzzleName = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf("\\plugins\\RushHour\\puzzles\\") + "\\plugins\\RushHour\\puzzles\\".length());
		if (puzzle.name == null || puzzle.name.isEmpty()) { puzzle.name = puzzleName.substring(0, puzzleName.length() - ".rh".length()); }
		loadPuzzle(puzzle);
	}

	@Override
	public void setCar(int x, int y, GameCar car, Direction moveDirection, final CarInteractListener listener) {
		//Don't change the cars if the game is already finished
		if (this.finished) { return; }

		String displayName = " ";
		if (moveDirection != null) {
			boolean canMove = puzzle.checkCollision(car, car.bounds.shift(moveDirection));
			displayName = RushHour.messageContainer.getMessage("inventory.game.move." + (canMove ? "enabled" : "disabled") + "." + moveDirection.name().toLowerCase());
			//			switch (moveDirection) {
			//				case LEFT:
			//					if (canMove) { displayName = " §e< "; } else { displayName = " §7< "; }
			//					break;
			//				case RIGHT:
			//					if (canMove) { displayName = " §e> "; } else { displayName = " §7> "; }
			//					break;
			//				case UP:
			//					if (canMove) { displayName = " §e^ "; } else { displayName = " §7^ "; }
			//					break;
			//				case DOWN:
			//					if (canMove) { displayName = " §ev "; } else { displayName = " §7v "; }
			//					break;
			//			}
		}

		setCar(x, y, car.variant.getColor(), displayName, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType clickType, ItemStack itemStack) {
				if (!player.getUniqueId().equals(InventoryGenerator.this.puzzle.player.getUniqueId())) { throw new IllegalStateException(); }
				listener.onInteract(player, clickType);
			}
		});
	}

	public void setCar(int x, int y, DyeColor color, String displayName, ItemListener listener) {
		//Move one to the right, since the border is there
		x += 1;

		int index = x + (y * 9);
		this.menuBuilder.withItem(index,//
				new ItemBuilder(Material.STAINED_CLAY, 1, color.getData()).buildMeta().withDisplayName(displayName).item().build(), listener, InventoryMenuBuilder.ALL_CLICK_TYPES);
	}

	@Override
	public void clearCars() {
		menuBuilder.getInventory().clear();
	}

	@Override
	public void resetListeners() {
		MenuBuilderPlugin.instance.inventoryListener.unregisterAllListeners(menuBuilder.getInventory());
	}

	@Override
	public void updateCars() {
		menuBuilder.refreshContent();
	}

	@Override
	public void updateMoves(int moves) {
		this.moveCount = moves;
	}

	@Override
	public void gameFinished(boolean solved, boolean wasSolution) {
		this.finished = true;

		resetListeners();

		generateBase();

	}

	@Override
	public void showTo(Player player) {
		this.puzzle.player = player;

		PlayerBeginPuzzleEvent event = new PlayerBeginPuzzleEvent(player, this.puzzle);
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			this.menuBuilder.show(player);

			player.setMetadata("RUSHHOUR_GENERATOR", new FixedMetadataValue(Bukkit.getPluginManager().getPlugin("RushHour"), this));
			player.setMetadata("RUSHHOUR_PUZZLE", new FixedMetadataValue(Bukkit.getPluginManager().getPlugin("RushHour"), puzzle));
		} else {
			clearCars();
			resetListeners();
			this.menuBuilder.dispose();
		}
	}

}
