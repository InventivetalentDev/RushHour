package org.inventivetalent.rushhour.puzzle;

import org.inventivetalent.rushhour.car.Rotation;

import java.awt.*;

public class Bounds extends Rectangle {

	public int length;

	public Rotation rotation;

	public Bounds(int x, int y, Rotation rotation, int length) {
		super(x, y, rotation.getShiftX() * (length - 1), rotation.getShiftY() * (length - 1));
		this.rotation = rotation;
		this.length = length;
	}

	public Bounds shift(Direction direction) {
		return new Bounds((int) (getMinX() + direction.getShiftX()), (int) (getMinY() + direction.getShiftY()), this.rotation, this.length);
	}

	public int x() {
		return (int) getX();
	}

	public int y() {
		return (int) getY();
	}

	@Override
	public double getMinX() {
		return Math.min(super.getMinX(), super.getMaxX());
	}

	@Override
	public double getMaxX() {
		return Math.max(super.getMaxX(), super.getMinX());
	}

	@Override
	public double getMinY() {
		return Math.min(super.getMinY(), super.getMaxY());
	}

	@Override
	public double getMaxY() {
		return Math.max(super.getMaxY(), super.getMinY());
	}

	public boolean collidesWith(Bounds other) {
		//		System.out.println("Other:");
		//		System.out.println("+" + (int) other.getMinX() + "|" + (int) other.getMinY() + "\t\t+");
		//		System.out.println("+    \t\t+" + (int) other.getMaxX() + "|" + (int) other.getMaxY());
		//
		//		System.out.println("This:");
		//		System.out.println("+" + (int) getMinX() + "|" + (int) getMinY() + "\t\t+");
		//		System.out.println("+    \t\t+" + (int) getMaxX() + "|" + (int) getMaxY());
		//		System.out.println("   ");

		return this.getMaxX() >= other.getMinX() &&//
				this.getMinX() <= other.getMaxX() &&//
				this.getMaxY() >= other.getMinY() &&//
				this.getMinY() <= other.getMaxY();
	}

	@Override
	public String toString() {
		return "Bounds(" + (int) getMinX() + "|" + (int) getMinY() + ")(" + (int) getMaxX() + "|" + (int) getMaxY() + ")";
	}
}
