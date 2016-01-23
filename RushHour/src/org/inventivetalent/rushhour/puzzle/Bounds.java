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
