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

package org.inventivetalent.rushhour.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.MetadataValue;
import org.inventivetalent.rushhour.RushHour;
import org.inventivetalent.rushhour.puzzle.generator.AbstractPuzzleGenerator;
import org.inventivetalent.rushhour.puzzle.generator.inventory.InventoryGenerator;

import java.util.ArrayList;
import java.util.List;

public class InventoryListener implements Listener {

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		Inventory inventory = event.getInventory();

		if (player.hasMetadata("RUSHHOUR_GENERATOR")) {
			List<MetadataValue> values = player.getMetadata("RUSHHOUR_GENERATOR");
			if (values.isEmpty()) {
				return;
			}
			Object value = values.get(0).value();
			if (value instanceof AbstractPuzzleGenerator) {
				final AbstractPuzzleGenerator generator = (AbstractPuzzleGenerator) value;
				if (generator instanceof InventoryGenerator) {
					if (((InventoryGenerator) generator).menuBuilder.getInventory().equals(inventory)) {
						((InventoryGenerator) generator).puzzle.puzzleFinished(false);

						Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("RushHour"), new Runnable() {
							@Override
							public void run() {
								List<HumanEntity> viewers = new ArrayList<>(((InventoryGenerator) generator).menuBuilder.getInventory().getViewers());
								for (HumanEntity entity : viewers) {
									entity.closeInventory();
								}
							}
						}, 1);
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getClickedInventory();
		Inventory inventory1 = event.getInventory();

		String title = RushHour.messageContainer.getMessage("inventory.title");
		if ((inventory != null && title.equals(inventory.getTitle())) || (inventory1 != null && title.equals(inventory1.getTitle()))) {
			event.setCancelled(true);
		}
	}

}
