package org.inventivetalent.rushhour.car;

import org.bukkit.DyeColor;

import static org.inventivetalent.rushhour.car.Variant.Length.CAR;
import static org.inventivetalent.rushhour.car.Variant.Length.TRUCK;

public enum Variant {

	/* "Main" car which has to escape */
	MAIN(DyeColor.RED, CAR, 'X'),

	/* Trucks */
	T_BLUE(DyeColor.BLUE, TRUCK, 'Q'),
	T_PINK(DyeColor.PINK, TRUCK, 'P'),
	T_YELLOW(DyeColor.YELLOW, TRUCK, 'O'),
	T_CYAN(DyeColor.CYAN, TRUCK, 'R'),

	/* Cars */
	C_BLUE(DyeColor.LIGHT_BLUE, CAR, 'C'),
	C_PINK(DyeColor.PINK, CAR, 'D'),
	C_PURPLE(DyeColor.PURPLE, CAR, 'E'),
	C_GREEN(DyeColor.GREEN, CAR, 'F'),
	C_LIME(DyeColor.LIME, CAR, 'A'),
	C_BROWN(DyeColor.BROWN, CAR, 'J'),
	C_SILVER(DyeColor.SILVER, CAR, 'H'),
	C_BLACK(DyeColor.BLACK, CAR, 'G'),
	C_YELLOW(DyeColor.YELLOW, CAR, 'I'),
	C_ORANGE(DyeColor.ORANGE, CAR, 'B'),
	C_GRAY(DyeColor.GRAY, CAR, 'K');

	private DyeColor color;
	private int      length;
	private char     identifier;

	Variant(DyeColor color, int length, char identifier) {
		this.color = color;
		this.length = length;
		this.identifier = identifier;
	}

	public DyeColor getColor() {
		return color;
	}

	public int getLength() {
		return length;
	}

	public char getIdentifier() {
		return identifier;
	}

	public static Variant getByIdentifier(char identifier) {
		for (Variant v : values()) {
			if (v.identifier == identifier) { return v; }
		}
		return null;
	}

	public static class Length {
		public static final int CAR   = 2;
		public static final int TRUCK = 3;
	}
}
