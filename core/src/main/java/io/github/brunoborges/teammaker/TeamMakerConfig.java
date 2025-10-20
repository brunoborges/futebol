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

import java.util.List;

/**
 * Configuration class for team maker that can be loaded from JSON.
 * Contains players, team names, and scoring configuration.
 */
public class TeamMakerConfig {
    
    private List<Player> players;
    private List<String> teamNames;
    private ScoreScale scoreScale;
    
    public TeamMakerConfig() {
        // Default constructor for Jackson
    }
    
    public TeamMakerConfig(List<Player> players, List<String> teamNames, ScoreScale scoreScale) {
        this.players = players;
        this.teamNames = teamNames;
        this.scoreScale = scoreScale;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<Player> players) {
        this.players = players;
    }
    
    public List<String> getTeamNames() {
        return teamNames;
    }
    
    public void setTeamNames(List<String> teamNames) {
        this.teamNames = teamNames;
    }
    
    public ScoreScale getScoreScale() {
        return scoreScale;
    }
    
    public void setScoreScale(ScoreScale scoreScale) {
        this.scoreScale = scoreScale;
    }
    
    /**
     * Calculate players per team based on total players and number of teams.
     * 
     * @return number of players per team
     * @throws IllegalArgumentException if players cannot be evenly divided among teams
     */
    public int calculatePlayersPerTeam() {
        if (players == null || teamNames == null || players.isEmpty() || teamNames.isEmpty()) {
            return 2; // default
        }
        
        int numPlayers = players.size();
        int numTeams = teamNames.size();
        
        if (numPlayers % numTeams != 0) {
            throw new IllegalArgumentException(
                String.format("Number of players (%d) must be evenly divisible by number of teams (%d). " +
                            "Current division results in %d players per team with %d remaining players.",
                            numPlayers, numTeams, numPlayers / numTeams, numPlayers % numTeams));
        }
        
        return numPlayers / numTeams;
    }
    
    /**
     * Validate that the configuration has a valid number of players and teams.
     * 
     * @throws IllegalArgumentException if the configuration is invalid
     */
    public void validate() {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Players list cannot be null or empty");
        }
        
        if (teamNames == null || teamNames.isEmpty()) {
            throw new IllegalArgumentException("Team names list cannot be null or empty");
        }
        
        // This will throw an exception if not evenly divisible
        calculatePlayersPerTeam();
        
        // Validate score scale if present
        if (scoreScale != null) {
            if (scoreScale.getMin() >= scoreScale.getMax()) {
                throw new IllegalArgumentException("Score scale minimum must be less than maximum");
            }
            
            // Validate all player scores are within the scale
            for (Player player : players) {
                if (!scoreScale.isValidScore(player.score())) {
                    throw new IllegalArgumentException(
                        String.format("Player %s has score %.1f which is outside the valid range [%.1f, %.1f]",
                                    player.name(), player.score(), scoreScale.getMin(), scoreScale.getMax()));
                }
            }
        }
    }
    
    /**
     * Inner class to represent the score scale configuration.
     */
    public static class ScoreScale {
        private double min;
        private double max;
        
        public ScoreScale() {
            // Default constructor for Jackson
        }
        
        public ScoreScale(double min, double max) {
            this.min = min;
            this.max = max;
        }
        
        public double getMin() {
            return min;
        }
        
        public void setMin(double min) {
            this.min = min;
        }
        
        public double getMax() {
            return max;
        }
        
        public void setMax(double max) {
            this.max = max;
        }
        
        /**
         * Validate if a score is within the allowed range.
         * 
         * @param score the score to validate
         * @return true if the score is within range
         */
        public boolean isValidScore(double score) {
            return score >= min && score <= max;
        }
    }
}