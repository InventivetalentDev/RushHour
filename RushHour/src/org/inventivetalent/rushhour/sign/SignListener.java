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

package org.inventivetalent.rushhour.sign;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.inventivetalent.rushhour.RushHour;

public class SignListener implements Listener {

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		if (event.getLine(0).equalsIgnoreCase("[RushHour]")) {
			if (!player.hasPermission("rushhour.sign.create")) {
				player.sendMessage(RushHour.messageContainer.getMessage("sign.error.permission.create"));
				event.setCancelled(true);
				return;
			}
			event.setLine(0, RushHour.messageContainer.getMessage("sign.title"));

			String levelName = event.getLine(RushHour.SIGN_LEVEL_LINE);
			if (levelName == null || levelName.isEmpty()) {
				player.sendMessage(RushHour.messageContainer.getMessage("sign.error.level.missing"));
				event.setCancelled(true);
				return;
			}

			player.sendMessage(RushHour.messageContainer.getMessage("sign.created", levelName));
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) { return; }
		if (event.getClickedBlock() == null) { return; }
		if (event.getClickedBlock().getType() != Material.WALL_SIGN && event.getClickedBlock().getType() != Material.SIGN_POST) {
			return;
		}

		Player player = event.getPlayer();
		Sign sign = (Sign) event.getClickedBlock().getState();
		if (RushHour.messageContainer.getMessage("sign.title").equals(sign.getLine(0))) {
			event.setCancelled(true);

			if (!player.hasPermission("rushhour.sign.use")) {
				player.sendMessage(RushHour.messageContainer.getMessage("sign.error.permission.use"));
				return;
			}

			String levelName = sign.getLine(RushHour.SIGN_LEVEL_LINE);
			if (levelName == null || levelName.isEmpty()) {
				player.sendMessage(RushHour.messageContainer.getMessage("sign.error.level.missing"));
				return;
			}

			String action = "play";
			String actionLine = sign.getLine(RushHour.SIGN_ACTION_LINE);
			if (actionLine != null && !actionLine.isEmpty()) {
				if (actionLine.toLowerCase().contains("play")) { action = "play"; }
				if (actionLine.toLowerCase().contains("stats")) { action = "stats"; }
			}

			player.chat("/rushhour " + action + " " + levelName);
		}
	}

}
