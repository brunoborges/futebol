/*
 * Copyright (C) 2025 Bruno Borges
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package io.github.brunoborges.teammaker;

import java.util.ArrayList;
import java.util.List;

public class Team {

	private List<Player> players = new ArrayList<Player>();
	private double score = 0;
	private String name;
	private int playerLimit;

	public Team(String name, int playersPerTeam) {
		this.name = name;
		this.playerLimit = playersPerTeam;
	}

	public boolean isComplete() {
		return players.size() == playerLimit;
	}

	public void add(Player p) {
		if (isComplete()) {
			throw new IllegalStateException("Team '" + getName() + "' is already complete");
		}
		score += p.score();
		players.add(p);
	}

	public double getScore() {
		return score;
	}

	public String getName() {
		return name;
	}

	public List<Player> getPlayers() {
		return new ArrayList<>(players); // Return defensive copy
	}

	public String toString() {
		var playersText = new StringBuilder();
		players.forEach(p -> playersText.append("\n\t\t%s (%.1f),".formatted(p.name(), p.score())));

		if (!players.isEmpty()) {
			playersText.deleteCharAt(playersText.length() - 1); // Remove last comma
		}

		return """
				%s [strength = %.1f, players = {%s}]""".formatted(getName(), getScore(), playersText.toString());
	}

	public void reset() {
		this.score = 0;
		players.clear();
	}
}