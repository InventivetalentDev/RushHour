package org.inventivetalent.rushhour.puzzle;

public enum Direction {

	LEFT(-1, 0, 'L'),
	RIGHT(1, 0, 'R'),
	UP(0, -1, 'U'),
	DOWN(0, 1, 'D');

	private int shiftX, shiftY;
	private char code;

	Direction(int shiftX, int shiftY, char code) {
		this.shiftX = shiftX;
		this.shiftY = shiftY;
		this.code = code;
	}

	public int getShiftX() {
		return shiftX;
	}

	public int getShiftY() {
		return shiftY;
	}

	public char getCode() {
		return code;
	}

	public static Direction getByCode(char code) {
		for (Direction d : values()) {
			if (d.code == code) { return d; }
		}
		return null;
	}
}

