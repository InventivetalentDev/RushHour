package org.inventivetalent.rushhour.puzzle.generator.inventory;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.itembuilder.ItemBuilder;
import org.inventivetalent.menubuilder.MenuBuilderPlugin;
import org.inventivetalent.menubuilder.inventory.InventoryMenuBuilder;
import org.inventivetalent.menubuilder.inventory.ItemListener;
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
			8,
			16,
			17,
			/* 25 <- Exit hole */
			34,
			35,
			43,
			44,
			52
			/*53*/ };

	public InventoryMenuBuilder menuBuilder;
	public Puzzle               puzzle;

	public int     moveCount = 0;
	public boolean finished  = false;

	public InventoryGenerator() {
		this.menuBuilder = new InventoryMenuBuilder(6 * 9, "RushHour"/*TODO: Title*/);
	}

	public void generateBase() {
		for (int i : WALL_SLOTS) {
			this.menuBuilder.withItem(i,//
					new ItemBuilder(Material.STAINED_GLASS_PANE, 1, finished ? DyeColor.LIME.getData() : DyeColor.GRAY.getData()).buildMeta().withDisplayName(finished ? " §aGame Finished " : " ").item().build(), new ItemListener() {
						@Override
						public void onInteract(Player player, ClickType clickType, ItemStack itemStack) {
						}
					}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		}

		this.menuBuilder.withItem(53, new ItemBuilder(Material.WATCH, this.moveCount).buildMeta().withDisplayName("§7Moves: §e" + this.moveCount).item().build(), new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType clickType, ItemStack itemStack) {
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
	}

	public void loadPuzzle(Puzzle puzzle) {
		this.puzzle = puzzle;
		puzzle.initializeCars();
		puzzle.addCarsToInventory(this);
	}

	public void loadPuzzle(File file) throws IOException {
		loadPuzzle(Puzzle.fromJson(new FileReader(file)));
	}

	@Override
	public void setCar(int x, int y, GameCar car, Direction moveDirection, final CarInteractListener listener) {
		//Don't change the cars if the game is already finished
		if (this.finished) { return; }

		String displayName = " ";
		if (moveDirection != null) {
			boolean canMove = puzzle.checkCollision(car, car.bounds.shift(moveDirection));
			switch (moveDirection) {
				case LEFT:
					if (canMove) { displayName = " §e< "; } else { displayName = " §7< "; }
					break;
				case RIGHT:
					if (canMove) { displayName = " §e> "; } else { displayName = " §7> "; }
					break;
				case UP:
					if (canMove) { displayName = " §e^ "; } else { displayName = " §7^ "; }
					break;
				case DOWN:
					if (canMove) { displayName = " §ev "; } else { displayName = " §7v "; }
					break;
			}
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
				new ItemBuilder(Material.WOOL, 1, color.getData()).buildMeta().withDisplayName(displayName).item().build(), listener, InventoryMenuBuilder.ALL_CLICK_TYPES);
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
		//		this.menuBuilder.withTitle("RushHour | Moves: " + moves);//TODO: Title
	}

	@Override
	public void gameFinished(boolean wasSolution) {
		this.finished = true;

		resetListeners();

		for (int i = 0; i < menuBuilder.getInventory().getSize(); i++) {
			menuBuilder.withItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, DyeColor.GREEN.getData()).buildMeta().withDisplayName(" §2Game Finished! ").item().build(), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType clickType, ItemStack itemStack) {
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		}

		generateBase();
	}

	@Override
	public void showTo(Player player) {
		this.puzzle.player = player;

		PlayerBeginPuzzleEvent event = new PlayerBeginPuzzleEvent(player, this.puzzle);
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			this.menuBuilder.show(player);
		} else {
			clearCars();
			resetListeners();
			this.menuBuilder.dispose();
		}
	}

}
