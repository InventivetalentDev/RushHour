package org.inventivetalent.rushhour.puzzle.generator;

import org.bukkit.entity.Player;
import org.inventivetalent.rushhour.puzzle.Direction;
import org.inventivetalent.rushhour.puzzle.GameCar;
import org.inventivetalent.rushhour.puzzle.Puzzle;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class AbstractPuzzleGenerator {

	public void generateBase() {
	}

	public void loadPuzzle(Puzzle puzzle) {
	}

	public void loadPuzzle(File file) throws IOException {
		loadPuzzle(Puzzle.fromJson(new FileReader(file)));
	}

	public abstract void setCar(int x, int y, GameCar car, Direction moveDirection, CarInteractListener listener);

	public void clearCars() {
	}

	public void resetListeners() {
	}

	public void updateCars() {
	}

	public void updateMoves(int combinedMoves,int individualMoves) {
	}

	public void gameFinished(boolean solved, boolean wasSolution) {
	}

	public void showTo(Player player) {
	}

}
