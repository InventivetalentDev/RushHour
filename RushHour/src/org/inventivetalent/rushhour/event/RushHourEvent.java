package org.inventivetalent.rushhour.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract class RushHourEvent extends Event {

	public RushHourEvent() {
	}

	public RushHourEvent(boolean isAsync) {
		super(isAsync);
	}

	private static HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
