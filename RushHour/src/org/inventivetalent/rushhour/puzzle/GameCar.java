package org.inventivetalent.rushhour.puzzle;

import org.inventivetalent.rushhour.car.Car;
import org.inventivetalent.rushhour.car.Rotation;
import org.inventivetalent.rushhour.car.Variant;

public class GameCar extends Car {

	//	public int currentX, currentY;
	public Bounds bounds;

	public GameCar(Variant variant, Rotation rotation) {
		super(variant, rotation);
	}

	@Override
	public String toString() {
		return "GameCar[v=" + variant.name() + "]@[" + bounds.toString() + "]#" + hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		if (!super.equals(o)) { return false; }

		GameCar car = (GameCar) o;

		return !(bounds != null ? !bounds.equals(car.bounds) : car.bounds != null);

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (bounds != null ? bounds.hashCode() : 0);
		return result;
	}
}
