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

import java.util.ArrayList;
import java.util.List;

@DisplayName("TeamMakerResult Tests")
class TeamMakerResultTest {

    private List<Team> sampleTeams;
    private TeamMakerResult result;

    @BeforeEach
    void setUp() {
        sampleTeams = new ArrayList<>();
        
        Team team1 = new Team("Team A", 2);
        team1.add(new Player("Player 1", 3.0));
        team1.add(new Player("Player 2", 3.0));
        
        Team team2 = new Team("Team B", 2);
        team2.add(new Player("Player 3", 4.0));
        team2.add(new Player("Player 4", 2.0));
        
        sampleTeams.add(team1);
        sampleTeams.add(team2);
        
        result = new TeamMakerResult(sampleTeams, true, 6.0, 6.0);
    }

    @Test
    @DisplayName("Should create result with all properties")
    void shouldCreateResultWithAllProperties() {
        // Given (from setUp)
        // When/Then
        assertNotNull(result);
        assertEquals(sampleTeams, result.getTeams());
        assertTrue(result.isBalanced());
        assertEquals(6.0, result.getMinimumStrength(), 0.001);
        assertEquals(6.0, result.getMaximumStrength(), 0.001);
    }

    @Test
    @DisplayName("Should handle empty teams list")
    void shouldHandleEmptyTeamsList() {
        // Given
        List<Team> emptyTeams = new ArrayList<>();
        
        // When
        TeamMakerResult emptyResult = new TeamMakerResult(emptyTeams, false, 0.0, 0.0);
        
        // Then
        assertNotNull(emptyResult.getTeams());
        assertTrue(emptyResult.getTeams().isEmpty());
        assertFalse(emptyResult.isBalanced());
        assertEquals(0.0, emptyResult.getMinimumStrength(), 0.001);
        assertEquals(0.0, emptyResult.getMaximumStrength(), 0.001);
    }

    @Test
    @DisplayName("Should handle unbalanced teams")
    void shouldHandleUnbalancedTeams() {
        // Given
        List<Team> unbalancedTeams = new ArrayList<>();
        
        Team strongTeam = new Team("Strong Team", 2);
        strongTeam.add(new Player("Strong Player 1", 5.0));
        strongTeam.add(new Player("Strong Player 2", 5.0));
        
        Team weakTeam = new Team("Weak Team", 2);
        weakTeam.add(new Player("Weak Player 1", 1.0));
        weakTeam.add(new Player("Weak Player 2", 1.0));
        
        unbalancedTeams.add(strongTeam);
        unbalancedTeams.add(weakTeam);
        
        // When
        TeamMakerResult unbalancedResult = new TeamMakerResult(unbalancedTeams, false, 2.0, 10.0);
        
        // Then
        assertFalse(unbalancedResult.isBalanced());
        assertEquals(2.0, unbalancedResult.getMinimumStrength(), 0.001);
        assertEquals(10.0, unbalancedResult.getMaximumStrength(), 0.001);
        assertEquals(2, unbalancedResult.getTeams().size());
    }

    @Test
    @DisplayName("Should handle single team")
    void shouldHandleSingleTeam() {
        // Given
        List<Team> singleTeamList = new ArrayList<>();
        Team singleTeam = new Team("Only Team", 2);
        singleTeam.add(new Player("Player 1", 3.0));
        singleTeam.add(new Player("Player 2", 3.0));
        singleTeamList.add(singleTeam);
        
        // When
        TeamMakerResult singleResult = new TeamMakerResult(singleTeamList, true, 6.0, 6.0);
        
        // Then
        assertEquals(1, singleResult.getTeams().size());
        assertTrue(singleResult.isBalanced()); // Single team is always balanced
        assertEquals(6.0, singleResult.getMinimumStrength(), 0.001);
        assertEquals(6.0, singleResult.getMaximumStrength(), 0.001);
    }

    @Test
    @DisplayName("Should maintain immutability of teams list")
    void shouldMaintainImmutabilityOfTeamsList() {
        // Given
        List<Team> originalTeams = new ArrayList<>(sampleTeams);
        
        // When
        List<Team> resultTeams = result.getTeams();
        
        // Then
        assertEquals(originalTeams, resultTeams);
        
        // Verify that the returned list is the same reference (not defensive copy)
        // This depends on the implementation - if you want defensive copying, change this test
        assertSame(sampleTeams, resultTeams);
    }

    @Test
    @DisplayName("Should handle null teams list gracefully")
    void shouldHandleNullTeamsListGracefully() {
        // Given/When/Then
        assertDoesNotThrow(() -> {
            TeamMakerResult nullResult = new TeamMakerResult(null, false, 0.0, 0.0);
            assertNull(nullResult.getTeams());
        });
    }

    @Test
    @DisplayName("Should handle negative strength values")
    void shouldHandleNegativeStrengthValues() {
        // Given/When
        TeamMakerResult negativeResult = new TeamMakerResult(sampleTeams, false, -1.0, -0.5);
        
        // Then
        assertEquals(-1.0, negativeResult.getMinimumStrength(), 0.001);
        assertEquals(-0.5, negativeResult.getMaximumStrength(), 0.001);
    }

    @Test
    @DisplayName("Should handle equal min and max strength")
    void shouldHandleEqualMinAndMaxStrength() {
        // Given
        double equalStrength = 5.5;
        
        // When
        TeamMakerResult equalResult = new TeamMakerResult(sampleTeams, true, equalStrength, equalStrength);
        
        // Then
        assertEquals(equalStrength, equalResult.getMinimumStrength(), 0.001);
        assertEquals(equalStrength, equalResult.getMaximumStrength(), 0.001);
        assertTrue(equalResult.isBalanced());
    }

    @Test
    @DisplayName("Should handle very large teams list")
    void shouldHandleVeryLargeTeamsList() {
        // Given
        List<Team> largeTeamsList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Team team = new Team("Team " + (char)('A' + i % 26) + (i / 26), 2);
            team.add(new Player("Player " + (i * 2 + 1), 3.0));
            team.add(new Player("Player " + (i * 2 + 2), 3.0));
            largeTeamsList.add(team);
        }
        
        // When
        TeamMakerResult largeResult = new TeamMakerResult(largeTeamsList, true, 6.0, 6.0);
        
        // Then
        assertEquals(100, largeResult.getTeams().size());
        assertTrue(largeResult.isBalanced());
        assertEquals(6.0, largeResult.getMinimumStrength(), 0.001);
        assertEquals(6.0, largeResult.getMaximumStrength(), 0.001);
    }
}