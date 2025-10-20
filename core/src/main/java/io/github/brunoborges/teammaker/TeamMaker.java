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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Service class responsible for creating balanced teams from a list of players.
 * This class contains the core team-making logic without any UI concerns.
 */
public class TeamMaker {

	private static final int PLAYERS_PER_TEAM = 2;
	private static final char[] ALPHABET = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
			'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	private List<Player> players;
	private List<Team> teams;
	private double averageStrength;
	private final int playersPerTeam;
	private List<String> teamNames;

	public TeamMaker(int playersPerTeam) {
		this.players = new ArrayList<>();
		this.teams = new ArrayList<>();
		this.playersPerTeam = playersPerTeam;
		this.teamNames = null; // Will use default alphabet-based names
	}

	public TeamMaker() {
		this(PLAYERS_PER_TEAM);
	}

	/**
	 * Creates a TeamMaker instance from JSON configuration.
	 * 
	 * @param config the configuration loaded from JSON
	 */
	public TeamMaker(TeamMakerConfig config) {
		this.players = new ArrayList<>();
		this.teams = new ArrayList<>();
		this.playersPerTeam = config.calculatePlayersPerTeam();
		this.teamNames = new ArrayList<>(config.getTeamNames());
	}

	/**
	 * Creates balanced teams using the default player list.
	 * 
	 * @return TeamMakerResult containing the teams and balance information
	 */
	public TeamMakerResult createBalancedTeams() {
		return createBalancedTeams(DefaultPlayers.get());
	}

	/**
	 * Creates balanced teams from JSON configuration file.
	 * 
	 * @param configPath path to the JSON configuration file
	 * @return TeamMakerResult containing the teams and balance information
	 * @throws IOException if the configuration file cannot be loaded
	 */
	public static TeamMakerResult createBalancedTeamsFromConfig(String configPath) throws IOException {
		TeamMakerConfig config = JsonConfigLoader.loadFromFile(configPath);
		TeamMaker teamMaker = new TeamMaker(config);
		return teamMaker.createBalancedTeams(config.getPlayers());
	}

	/**
	 * Creates balanced teams from JSON configuration resource.
	 * 
	 * @param resourceName name of the JSON configuration resource
	 * @return TeamMakerResult containing the teams and balance information
	 * @throws IOException if the configuration resource cannot be loaded
	 */
	public static TeamMakerResult createBalancedTeamsFromResource(String resourceName) throws IOException {
		TeamMakerConfig config = JsonConfigLoader.loadFromResource(resourceName);
		TeamMaker teamMaker = new TeamMaker(config);
		return teamMaker.createBalancedTeams(config.getPlayers());
	}

	/**
	 * Creates balanced teams from the provided configuration.
	 * 
	 * @param config the team maker configuration
	 * @return TeamMakerResult containing the teams and balance information
	 */
	public static TeamMakerResult createBalancedTeamsFromConfig(TeamMakerConfig config) {
		TeamMaker teamMaker = new TeamMaker(config);
		return teamMaker.createBalancedTeams(config.getPlayers());
	}

	/**
	 * Creates balanced teams from the provided list of players.
	 * 
	 * @param playerList the list of players to organize into teams
	 * @return TeamMakerResult containing the teams and balance information
	 * @throws IllegalArgumentException if the number of players is not evenly divisible by playersPerTeam
	 */
	public TeamMakerResult createBalancedTeams(List<Player> playerList) {
		// Validate that players can be evenly divided into teams
		if (playerList.size() % playersPerTeam != 0) {
			throw new IllegalArgumentException(
				String.format("Number of players (%d) must be evenly divisible by players per team (%d). " +
							"Current division results in %d complete teams with %d remaining players.",
							playerList.size(), playersPerTeam, playerList.size() / playersPerTeam, 
							playerList.size() % playersPerTeam));
		}
		
		initializePlayers(playerList);
		prepareTeams();
		assembleTeams();

		boolean balanced = calculateBalance();
		double minStrength = teams.stream().mapToDouble(Team::getScore).min().orElse(0);
		double maxStrength = teams.stream().mapToDouble(Team::getScore).max().orElse(0);

		return new TeamMakerResult(new ArrayList<>(teams), balanced, minStrength, maxStrength);
	}

	/**
	 * Get the default list of players.
	 * 
	 * @return list of default players
	 */
	public List<Player> getDefaultPlayers() {
		return DefaultPlayers.get();
	}

	private void initializePlayers(List<Player> playerList) {
		players.clear();
		players.addAll(playerList);
		calculateAverage();
	}

	private void prepareTeams() {
		teams.clear();
		int totalTeams = players.size() / playersPerTeam;
		
		for (int i = 0; i < totalTeams; i++) {
			String teamName;
			if (teamNames != null && i < teamNames.size()) {
				teamName = teamNames.get(i);
			} else {
				teamName = "Team " + ALPHABET[i % ALPHABET.length];
			}
			teams.add(new Team(teamName, playersPerTeam));
		}
	}

	private void calculateAverage() {
		double totalStrength = 0;

		for (Player p : players) {
			totalStrength += p.score();
		}

		averageStrength = totalStrength / players.size();
	}

	private boolean calculateBalance() {
		double minimumStrength = Double.MAX_VALUE;
		double maximumStrength = Double.MIN_VALUE;

		for (Team team : teams) {
			minimumStrength = Math.min(team.getScore(), minimumStrength);
			maximumStrength = Math.max(team.getScore(), maximumStrength);
		}

		return !(minimumStrength < 0.7 * maximumStrength);
	}

	private void assembleTeams() {
		// Randomize the list of players
		Collections.shuffle(players, new Random(System.currentTimeMillis()));
		Collections.shuffle(teams, new Random(System.currentTimeMillis()));

		Iterator<Team> itTeam = teams.iterator();
		while (players.size() > 0) {
			if (!itTeam.hasNext()) {
				// Check if any teams can still accept players
				boolean anyIncompleteTeam = teams.stream().anyMatch(team -> !team.isComplete());
				if (!anyIncompleteTeam) {
					// All teams are complete, but we have remaining players
					break;
				}
				itTeam = teams.iterator();
			}

			Team currentTeam = itTeam.next();
			if (currentTeam.isComplete()) {
				continue;
			}

			Player p = getPlayer(currentTeam);
			currentTeam.add(p);
		}
	}

	private Player getPlayer(double strength) {
		if (players.size() == 0)
			return null;
		else if (players.size() == 1)
			return players.remove(0);

		Player bestPlayer = null;
		double bestDifference = Double.MAX_VALUE;

		// Find the player with score closest to the desired strength
		for (Player p : players) {
			double difference = Math.abs(p.score() - strength);
			if (difference < bestDifference) {
				bestDifference = difference;
				bestPlayer = p;
			}
		}

		if (bestPlayer != null)
			players.remove(bestPlayer);

		return bestPlayer;
	}

	private Player getPlayer(Team currentTeam) {
		double weight = Math.random();

		if (currentTeam.getScore() < averageStrength) {
			weight += Math.random();
		} else {
			weight -= Math.random();
		}

		double strength = 0;
		if (weight >= 0 && weight < 0.10) {
			// 10%
			strength = 1;
		} else if (weight >= 0.10 && weight < 0.25) {
			// 15%
			strength = 2;
		} else if (weight >= 0.25 && weight < 0.65) {
			// 40%
			strength = 3;
		} else if (weight >= 0.65 && weight < 0.95) {
			// 30%
			strength = 4;
		} else if (weight >= 0.95) {
			// 5%
			strength = 5;
		}

		Player player = getPlayer(strength);
		while (player == null && players.size() > 0) {
			// Try different strength values if no player found
			strength = Math.random() * 5 + 1;
			player = getPlayer(strength);
		}

		return player;
	}
}