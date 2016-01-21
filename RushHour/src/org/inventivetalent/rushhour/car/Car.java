package org.inventivetalent.rushhour.car;

public class Car {

	public int x;
	public int y;

	public final Variant  variant;
	public final Rotation rotation;

	public Car(Variant variant, Rotation rotation) {
		this.variant = variant;
		this.rotation = rotation;
	}

}
