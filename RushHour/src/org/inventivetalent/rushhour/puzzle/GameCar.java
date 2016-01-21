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

}
