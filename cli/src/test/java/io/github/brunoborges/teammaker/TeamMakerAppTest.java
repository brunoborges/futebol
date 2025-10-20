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

@DisplayName("TeamMakerApp Tests")
class TeamMakerAppTest {

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
    @DisplayName("Should create app instance")
    void shouldCreateAppInstance() {
        // Given/When
        TeamMakerApp newApp = new TeamMakerApp();
        
        // Then
        assertNotNull(newApp);
    }

    @Test
    @DisplayName("Should run without exceptions using default config")
    void shouldRunWithoutExceptionsUsingDefaultConfig() {
        // When/Then
        assertDoesNotThrow(() -> TeamMakerApp.main(new String[]{}));
    }

    @Test
    @DisplayName("Should produce output when running with default config")
    void shouldProduceOutputWhenRunningWithDefaultConfig() {
        // When
        TeamMakerApp.main(new String[]{});
        
        // Then
        String output = outContent.toString();
        assertFalse(output.isEmpty(), "App should produce output");
        assertTrue(output.contains("Team"), "Output should contain team information");
        assertTrue(output.contains("Strength:"), "Output should contain strength information");
        assertTrue(output.contains("┌─"), "Output should contain formatted team boxes");
    }

    @Test
    @DisplayName("Should display team names in output")
    void shouldDisplayTeamNamesInOutput() {
        // When
        TeamMakerApp.main(new String[]{});
        
        // Then
        String output = outContent.toString();
        // Should contain team names from the default resource config
        assertTrue(output.contains("Team"), "Output should contain teams");
    }

    @Test
    @DisplayName("Should display balance information")
    void shouldDisplayStrengthInformation() {
        // When
        TeamMakerApp.main(new String[]{});
        
        // Then
        String output = outContent.toString();
        assertTrue(output.contains("Strength:"), "Output should show strength values");
        assertTrue(output.contains("Players:"), "Output should show players information");
    }

    @Test
    @DisplayName("Should display player information")
    void shouldDisplayPlayerInformation() {
        // When
        TeamMakerApp.main(new String[]{});
        
        // Then
        String output = outContent.toString();
        // Check for some known default players from the resource config
        assertTrue(output.contains("Bruno") || output.contains("Alex") || output.contains("Leo"),
                   "Output should contain player names");
    }

    @Test
    @DisplayName("Main method should run with --default option")
    void mainMethodShouldRunWithDefaultOption() {
        // When/Then
        assertDoesNotThrow(() -> TeamMakerApp.main(new String[]{"--default"}));
        
        // Check output was produced
        String output = outContent.toString();
        assertFalse(output.isEmpty(), "Main method should produce output");
    }

    @Test
    @DisplayName("Main method should handle --help argument")
    void mainMethodShouldHandleHelpArgument() {
        // When/Then - should not throw with help argument
        assertDoesNotThrow(() -> TeamMakerApp.main(new String[]{"--help"}));
        
        // Check that help was displayed
        String output = outContent.toString();
        assertTrue(output.contains("Usage:"), "Help output should contain usage information");
    }

    @Test
    @DisplayName("Should create multiple teams")
    void shouldCreateMultipleTeams() {
        // When
        TeamMakerApp.main(new String[]{});
        
        // Then
        String output = outContent.toString();
        
        // Count occurrences of team headers in the new format: "┌─ Team #N - TeamName"
        long teamCount = output.lines()
                .filter(line -> line.contains("┌─ Team #") || line.contains("Team #"))
                .count();
        
        assertTrue(teamCount >= 2, "Should create multiple teams, found: " + teamCount);
    }

    @Test
    @DisplayName("Should handle --version argument")
    void shouldHandleVersionArgument() {
        // When/Then
        assertDoesNotThrow(() -> TeamMakerApp.main(new String[]{"--version"}));
        
        // Check that version was displayed
        String output = outContent.toString();
        assertTrue(output.contains("TeamMaker"), "Version output should contain application name");
    }
}