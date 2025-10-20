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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;

@DisplayName("TeamResultFormatter Tests")
class TeamResultFormatterTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should format results with attractive headers and footers")
    void shouldFormatResultsWithAttractiveHeadersAndFooters() {
        // Given
        TeamResultFormatter formatter = new TeamResultFormatter(false);
        TeamMakerResult result = createSimpleTestResult();
        
        // When
        formatter.printResults(result);
        
        // Then
        String output = outContent.toString();
        assertTrue(output.contains("⚽ TEAM DRAW RESULTS ⚽"), "Should contain attractive header");
        assertTrue(output.contains("🏆 GOOD LUCK WITH YOUR MATCHES! 🏆"), "Should contain attractive footer");
        assertTrue(output.contains("╔══════════════════════════════════════════════════════════════════╗"), "Should contain border decorations");
    }

    @Test
    @DisplayName("Should display team information in formatted boxes")
    void shouldDisplayTeamInformationInFormattedBoxes() {
        // Given
        TeamResultFormatter formatter = new TeamResultFormatter(false);
        TeamMakerResult result = createSimpleTestResult();
        
        // When
        formatter.printResults(result);
        
        // Then
        String output = outContent.toString();
        assertTrue(output.contains("┌─── Team #1"), "Should contain formatted team headers");
        assertTrue(output.contains("│ Strength:"), "Should contain strength information");
        assertTrue(output.contains("├─ Players:"), "Should contain players section");
        assertTrue(output.contains("│   •"), "Should contain player bullet points");
        assertTrue(output.contains("└─"), "Should contain box closers");
    }

    @Test
    @DisplayName("Should display summary statistics")
    void shouldDisplaySummaryStatistics() {
        // Given
        TeamResultFormatter formatter = new TeamResultFormatter(false);
        TeamMakerResult result = createSimpleTestResult();
        
        // When
        formatter.printResults(result);
        
        // Then
        String output = outContent.toString();
        assertTrue(output.contains("📊 SUMMARY"), "Should contain summary section");
        assertTrue(output.contains("Total Teams:"), "Should show total teams count");
        assertTrue(output.contains("Average Team Strength:"), "Should show average strength");
        assertTrue(output.contains("Strength Range:"), "Should show strength range");
        assertTrue(output.contains("Max Difference:"), "Should show max difference");
    }

    @Test
    @DisplayName("Should show balance indicators")
    void shouldShowBalanceIndicators() {
        // Given
        TeamResultFormatter formatter = new TeamResultFormatter(false);
        TeamMakerResult result = createBalancedTestResult();
        
        // When
        formatter.printResults(result);
        
        // Then
        String output = outContent.toString();
        assertTrue(output.contains("✅ WELL BALANCED!"), "Should show balanced indicator");
    }

    @Test
    @DisplayName("Should show excellent balance message in verbose mode")
    void shouldShowExcellentBalanceMessageInVerboseMode() {
        // Given
        TeamResultFormatter formatter = new TeamResultFormatter(true);
        TeamMakerResult result = createVeryBalancedTestResult();
        
        // When
        formatter.printResults(result);
        
        // Then
        String output = outContent.toString();
        assertTrue(output.contains("🎯 Excellent balance achieved"), "Should show excellent balance message");
    }

    private TeamMakerResult createSimpleTestResult() {
        List<Team> teams = new ArrayList<>();
        
        Team team1 = new Team("Team A", 2);
        team1.add(new Player("Alice", 4.0));
        team1.add(new Player("Bob", 3.0));
        teams.add(team1);
        
        Team team2 = new Team("Team B", 2);
        team2.add(new Player("Charlie", 3.5));
        team2.add(new Player("David", 3.5));
        teams.add(team2);
        
        return new TeamMakerResult(teams, false, 7.0, 7.0);
    }

    private TeamMakerResult createBalancedTestResult() {
        List<Team> teams = new ArrayList<>();
        
        Team team1 = new Team("Team A", 2);
        team1.add(new Player("Alice", 3.5));
        team1.add(new Player("Bob", 3.5));
        teams.add(team1);
        
        Team team2 = new Team("Team B", 2);
        team2.add(new Player("Charlie", 3.5));
        team2.add(new Player("David", 3.5));
        teams.add(team2);
        
        return new TeamMakerResult(teams, true, 7.0, 7.0);
    }

    private TeamMakerResult createVeryBalancedTestResult() {
        List<Team> teams = new ArrayList<>();
        
        Team team1 = new Team("Team A", 2);
        team1.add(new Player("Alice", 3.5));
        team1.add(new Player("Bob", 3.5));
        teams.add(team1);
        
        Team team2 = new Team("Team B", 2);
        team2.add(new Player("Charlie", 3.6));
        team2.add(new Player("David", 3.4));
        teams.add(team2);
        
        return new TeamMakerResult(teams, true, 7.0, 7.0);
    }
}