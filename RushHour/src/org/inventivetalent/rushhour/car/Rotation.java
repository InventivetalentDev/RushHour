package org.inventivetalent.rushhour.car;

public enum Rotation {

	HORIZONTAL(1, 0),
	VERTICAL(0, 1);

	private int shiftX, shiftY;

	Rotation(int shiftX, int shiftY) {
		this.shiftX = shiftX;
		this.shiftY = shiftY;
	}

	public int getShiftX() {
		return shiftX;
	}

	public int getShiftY() {
		return shiftY;
	}
}
