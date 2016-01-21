package org.inventivetalent.rushhour.inventory;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.itembuilder.ItemBuilder;
import org.inventivetalent.menubuilder.inventory.InventoryMenuBuilder;
import org.inventivetalent.menubuilder.inventory.ItemListener;
import org.inventivetalent.rushhour.puzzle.Puzzle;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class InventoryGenerator {

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
			16,
			/* 25 <- Exit hole */
			34,
			43,
			52 };

	public  InventoryMenuBuilder menuBuilder;
	private Puzzle               puzzle;

	public InventoryGenerator() {
		this.menuBuilder = new InventoryMenuBuilder(6 * 9, "RushHour"/*TODO: Title*/);
	}

	public void generateBase() {
		for (int i : WALL_SLOTS) {
			this.menuBuilder.withItem(i,//
					new ItemBuilder(Material.STAINED_GLASS_PANE, 1, DyeColor.GRAY.getData()).buildMeta().withDisplayName(" ").item().build(), new ItemListener() {
						@Override
						public void onInteract(Player player, ClickType clickType, ItemStack itemStack) {
						}
					}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		}
	}

	public void loadPuzzle(Puzzle puzzle) {
		this.puzzle = puzzle;
		puzzle.initializeCars();
		puzzle.addCarsToInventory(this);
	}

	public void loadPuzzle(File file) throws IOException {
		loadPuzzle(Puzzle.fromJson(new FileReader(file)));
	}

	public void setCar(int x, int y, DyeColor color, ItemListener listener) {
		//Move one to the right, since the border is there
		x += 1;

		int index = x + (y * 9);
		this.menuBuilder.withItem(index,//
				new ItemBuilder(Material.WOOL, 1, color.getData()).buildMeta().withDisplayName(" ").item().build(), listener, InventoryMenuBuilder.ALL_CLICK_TYPES);
	}

	public void showTo(Player player) {
		this.menuBuilder.show(player);
	}

}
