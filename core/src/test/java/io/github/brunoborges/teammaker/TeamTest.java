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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Team Tests")
class TeamTest {

    private Team team;
    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void setUp() {
        team = new Team("Test Team", 2);
        player1 = new Player("Player One", 3.0);
        player2 = new Player("Player Two", 4.0);
        player3 = new Player("Player Three", 2.0);
    }

    @Test
    @DisplayName("Should create team with name and player limit")
    void shouldCreateTeamWithNameAndPlayerLimit() {
        // Given
        String teamName = "Alpha Team";
        int playerLimit = 3;
        
        // When
        Team newTeam = new Team(teamName, playerLimit);
        
        // Then
        assertEquals(teamName, newTeam.getName());
        assertEquals(0, newTeam.getScore());
        assertFalse(newTeam.isComplete());
    }

    @Test
    @DisplayName("Should not be complete when created")
    void shouldNotBeCompleteWhenCreated() {
        // Then
        assertFalse(team.isComplete());
        assertEquals(0, team.getScore());
    }

    @Test
    @DisplayName("Should add player and update strength")
    void shouldAddPlayerAndUpdateStrength() {
        // When
        team.add(player1);
        
        // Then
        assertEquals(3, team.getScore());
        assertFalse(team.isComplete());
    }

    @Test
    @DisplayName("Should be complete when player limit is reached")
    void shouldBeCompleteWhenPlayerLimitIsReached() {
        // When
        team.add(player1);
        team.add(player2);
        
        // Then
        assertTrue(team.isComplete());
        assertEquals(7, team.getScore()); // 3.0 + 4.0
    }

    @Test
    @DisplayName("Should throw exception when adding player to complete team")
    void shouldThrowExceptionWhenAddingPlayerToCompleteTeam() {
        // Given
        team.add(player1);
        team.add(player2);
        assertTrue(team.isComplete());
        
        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            team.add(player3);
        });
        assertEquals("Team 'Test Team' is already complete", exception.getMessage());
    }

    @Test
    @DisplayName("Should calculate total strength correctly")
    void shouldCalculateTotalStrengthCorrectly() {
        // When
        team.add(new Player("Strong Player", 5.0));
        team.add(new Player("Weak Player", 1.5));
        
        // Then
        assertEquals(6.5, team.getScore(), 0.001);
    }

    @Test
    @DisplayName("Should reset team properly")
    void shouldResetTeamProperly() {
        // Given
        team.add(player1);
        team.add(player2);
        assertTrue(team.isComplete());
        assertEquals(7, team.getScore());
        
        // When
        team.reset();
        
        // Then
        assertEquals(0, team.getScore());
        assertFalse(team.isComplete());
    }

    @Test
    @DisplayName("Should handle single player team")
    void shouldHandleSinglePlayerTeam() {
        // Given
        Team singlePlayerTeam = new Team("Solo Team", 1);
        
        // When
        singlePlayerTeam.add(player1);
        
        // Then
        assertTrue(singlePlayerTeam.isComplete());
        assertEquals(3, singlePlayerTeam.getScore());
    }

    @Test
    @DisplayName("Should handle large team")
    void shouldHandleLargeTeam() {
        // Given
        Team largeTeam = new Team("Large Team", 5);
        
        // When
        largeTeam.add(new Player("P1", 1.0));
        largeTeam.add(new Player("P2", 2.0));
        largeTeam.add(new Player("P3", 3.0));
        largeTeam.add(new Player("P4", 4.0));
        
        // Then
        assertFalse(largeTeam.isComplete());
        assertEquals(10, largeTeam.getScore());
        
        // Add final player
        largeTeam.add(new Player("P5", 5.0));
        assertTrue(largeTeam.isComplete());
        assertEquals(15, largeTeam.getScore());
    }

    @Test
    @DisplayName("Should return correct toString format for empty team")
    void shouldReturnCorrectToStringFormatForEmptyTeam() {
        // When
        String result = team.toString();
        
        // Then
        assertTrue(result.contains("Test Team"));
        assertTrue(result.contains("strength = 0"));
        assertTrue(result.contains("players = {}"));
    }

    @Test
    @DisplayName("Should return correct toString format for team with players")
    void shouldReturnCorrectToStringFormatForTeamWithPlayers() {
        // Given
        team.add(player1);
        team.add(player2);
        
        // When
        String result = team.toString();
        
        // Then
        assertTrue(result.contains("Test Team"));
        assertTrue(result.contains("strength = 7"));
        assertTrue(result.contains("Player One"));
        assertTrue(result.contains("(3.0)"));
        assertTrue(result.contains("Player Two"));
        assertTrue(result.contains("(4.0)"));
    }

    @Test
    @DisplayName("Should handle zero strength players")
    void shouldHandleZeroStrengthPlayers() {
        // Given
        Player zeroPlayer = new Player("Zero Player", 0.0);
        
        // When
        team.add(zeroPlayer);
        team.add(player1);
        
        // Then
        assertEquals(3, team.getScore());
        assertTrue(team.isComplete());
    }

    @Test
    @DisplayName("Should handle negative strength players")
    void shouldHandleNegativeStrengthPlayers() {
        // Given
        Player negativePlayer = new Player("Negative Player", -2.0);
        
        // When
        team.add(negativePlayer);
        team.add(player1);
        
        // Then
        assertEquals(1, team.getScore()); // -2.0 + 3.0 = 1.0
        assertTrue(team.isComplete());
    }

    @Test
    @DisplayName("Should handle team with zero player limit")
    void shouldHandleTeamWithZeroPlayerLimit() {
        // Given
        Team emptyTeam = new Team("Empty Team", 0);
        
        // Then
        assertTrue(emptyTeam.isComplete());
        
        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            emptyTeam.add(player1);
        });
        assertEquals("Team 'Empty Team' is already complete", exception.getMessage());
    }

    @Test
    @DisplayName("Should maintain state after multiple resets")
    void shouldMaintainStateAfterMultipleResets() {
        // Given
        team.add(player1);
        team.add(player2);
        
        // When
        team.reset();
        team.reset(); // Double reset
        
        // Then
        assertEquals(0, team.getScore());
        assertFalse(team.isComplete());
        
        // Should be able to add players again
        team.add(player3);
        assertEquals(2, team.getScore());
    }
}