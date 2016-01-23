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

package org.inventivetalent.rushhour.score.local;

import org.bukkit.OfflinePlayer;
import org.inventivetalent.rushhour.RushHour;
import org.inventivetalent.rushhour.puzzle.Puzzle;
import org.inventivetalent.rushhour.puzzle.solution.Solution;
import org.inventivetalent.rushhour.score.AbstractScoreManager;
import org.inventivetalent.rushhour.score.Score;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class LocalScoreManager extends AbstractScoreManager {

	private RushHour plugin;

	public LocalScoreManager(RushHour plugin) {
		this.plugin = plugin;

		int count = -1;
		try {
			count = plugin.getDatabase().find(PlayerScore.class).findRowCount();
		} catch (PersistenceException e) {
			plugin.getLogger().info("Initializing Database");
			plugin.installDDL();
		}
		if (count > 0) {
			plugin.getLogger().info("Found " + count + " scores in database");
		}
	}

	@Override
	public void track(OfflinePlayer player, Puzzle puzzle, Solution solution) {
		PlayerScore score = new PlayerScore();
		score.setPlayer(player.getUniqueId().toString());
		score.setPuzzleName(puzzle.name);
		score.setPuzzleHash(puzzle.getHash());
		score.setSolution(solution.toJsonArray().toString());
		score.setTimestamp(System.currentTimeMillis());
		score.setDuration(solution.getDuration());

		try {
			plugin.getDatabase().save(score);
			plugin.getLogger().info("Saved score for " + player.getName());
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to save score for " + player.getName(), e);
		}
	}

	@Override
	public List<String> getPlayedPuzzleNames(OfflinePlayer player) {
		List<String> puzzles = new ArrayList<>();

		List<PlayerScore> scores = getPlayerScores(player);

		for (PlayerScore score : scores) {
			puzzles.add(score.getPuzzleName());
		}

		return puzzles;
	}

	@Override
	public List<Solution> getPuzzleSolutions(OfflinePlayer player, String puzzleName) {
		List<PlayerScore> scores = getPlayerScores(player);

		List<Solution> solutions = new ArrayList<>();
		for (PlayerScore score : scores) {
			if (score.getPuzzleName().equals(puzzleName)) {
				solutions.add(Solution.GSON.fromJson(score.getSolution(), Solution.class));
			}
		}
		return solutions;
	}

	@Override
	public List<Score> getScores(OfflinePlayer player) {
		List<PlayerScore> playerScores = getPlayerScores(player);

		List<Score> scores = new ArrayList<>();
		for (PlayerScore playerScore : playerScores) {
			Score score = new Score();
			score.player = UUID.fromString(playerScore.getPlayer());
			score.puzzleName = playerScore.getPuzzleName();
			score.solution = Solution.GSON.fromJson(playerScore.getSolution(), Solution.class);
			score.timestamp = playerScore.getTimestamp();
			score.duration = playerScore.getDuration();

			scores.add(score);
		}

		return scores;
	}

	@Override
	public List<Score> getScores(OfflinePlayer player, String puzzleName) {
		List<Score> allScores = getScores(player);

		List<Score> scores = new ArrayList<>();
		for (Score score : allScores) {
			if (score.puzzleName.equals(puzzleName)) {
				scores.add(score);
			}
		}
		return scores;
	}

	public List<PlayerScore> getPlayerScores(OfflinePlayer player) {
		List<PlayerScore> allScores = plugin.getDatabase().find(PlayerScore.class).findList();
		List<PlayerScore> scores = new ArrayList<>();
		for (PlayerScore score : allScores) {
			if (score.getPlayer().equals(player.getUniqueId().toString())) {
				scores.add(score);
			}
		}
		return scores;
	}

}
