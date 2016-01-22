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

	@Override
	public String toString() {
		return "Car[v=" + variant.name() + ",r=" + rotation.name() + "]@(" + x + "|" + y + ")#" + hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }

		Car car = (Car) o;

		if (x != car.x) { return false; }
		if (y != car.y) { return false; }
		if (variant != car.variant) { return false; }
		return rotation == car.rotation;

	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + (variant != null ? variant.hashCode() : 0);
		result = 31 * result + (rotation != null ? rotation.hashCode() : 0);
		return result;
	}
}
