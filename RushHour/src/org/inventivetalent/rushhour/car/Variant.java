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
