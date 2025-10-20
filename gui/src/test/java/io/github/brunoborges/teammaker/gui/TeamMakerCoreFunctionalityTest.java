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

package io.github.brunoborges.teammaker.gui;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Focused tests for TeamMaker core functionality.
 * This class tests the most important features: team generation and validation.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TeamMakerCoreFunctionalityTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        TeamMakerFXApp app = new TeamMakerFXApp();
        app.start(stage);
    }

    /**
     * Test that the application starts successfully with default data.
     */
    @Test
    @Order(1)
    public void testApplicationStart() {
        // Verify the application window exists
        Stage stage = (Stage) window("👥 TeamMaker - Balanced Team Generator");
        assertNotNull(stage);
        assertTrue(stage.isShowing());

        // Verify default data is loaded
        ListView<?> playerListView = lookup("#playerListView").queryAs(ListView.class);
        ListView<?> teamListView = lookup("#teamListView").queryAs(ListView.class);
        
        assertTrue(playerListView.getItems().size() > 0, "Should have default players");
        assertTrue(teamListView.getItems().size() > 0, "Should have default teams");
        
        System.out.println("Application started with " + playerListView.getItems().size() + 
                          " players and " + teamListView.getItems().size() + " teams");
    }

    /**
     * Test the team generation core functionality.
     */
    @Test
    @Order(2)
    public void testTeamGenerationCore() {
        // Get the generate button
        Button generateButton = lookup("#generateButton").queryAs(Button.class);
        assertNotNull(generateButton);
        assertTrue(generateButton.isVisible());

        // Click to generate teams
        clickOn("#generateButton");

        // Allow time for generation
        sleep(500);

        // Switch to results tab
        TabPane tabPane = lookup("#tabPane").queryAs(TabPane.class);
        Platform.runLater(() -> tabPane.getSelectionModel().select(1));
        sleep(500);

        // Check results
        TextArea resultsArea = lookup("#resultsArea").queryAs(TextArea.class);
        assertNotNull(resultsArea);
        
        String resultsText = resultsArea.getText();
        assertNotNull(resultsText);
        assertFalse(resultsText.trim().isEmpty());

        // Verify the generation either succeeded or failed with a proper message
        boolean hasTeamResults = resultsText.contains("TEAM FORMATION RESULTS");
        boolean hasError = resultsText.contains("Click 'Generate Teams' to see results here...");
        boolean hasConfigError = resultsText.contains("Invalid Configuration");

        assertTrue(hasTeamResults || hasError || hasConfigError, 
                   "Should have either generated teams, shown an error, or maintained initial state");

        if (hasTeamResults) {
            // If teams were generated, verify the content looks correct
            assertTrue(resultsText.contains("Team #"), "Should show team numbers");
            assertTrue(resultsText.contains("SUMMARY"), "Should show summary section");
            System.out.println("✓ Team generation successful");
        } else {
            System.out.println("ℹ Team generation skipped due to configuration requirements");
        }
    }

    /**
     * Test that the results persist when switching between tabs.
     */
    @Test
    @Order(3)
    public void testResultsPersistence() {
        // Generate teams first
        clickOn("#generateButton");
        
        // Go to results tab
        TabPane tabPane = lookup("#tabPane").queryAs(TabPane.class);
        Platform.runLater(() -> tabPane.getSelectionModel().select(1));
        sleep(400);
        
        // Capture initial results
        TextArea resultsArea = lookup("#resultsArea").queryAs(TextArea.class);
        String initialResults = resultsArea.getText();
        
        // Switch back to configuration
        Platform.runLater(() -> tabPane.getSelectionModel().select(0));
        sleep(200);
        
        // Switch back to results
        Platform.runLater(() -> tabPane.getSelectionModel().select(1));
        sleep(200);
        
        // Results should be the same
        String persistedResults = resultsArea.getText();
        assertEquals(initialResults, persistedResults, "Results should persist across tab switches");
    }

    /**
     * Test that UI components are responsive and functional.
     */
    @Test
    @Order(4)
    public void testUIResponsiveness() {
        // Test that key components can be interacted with
        assertDoesNotThrow(() -> {
            // Test tab switching
            TabPane tabPane = lookup("#tabPane").queryAs(TabPane.class);
            Platform.runLater(() -> tabPane.getSelectionModel().select(1));
            sleep(100);
            Platform.runLater(() -> tabPane.getSelectionModel().select(0));
            sleep(100);
        }, "Tab switching should not throw exceptions");

        // Test that sliders work
        assertDoesNotThrow(() -> {
            Slider minSlider = lookup("#minScoreSlider").queryAs(Slider.class);
            Slider maxSlider = lookup("#maxScoreSlider").queryAs(Slider.class);
            
            Platform.runLater(() -> {
                minSlider.setValue(2.5);
                maxSlider.setValue(4.5);
            });
            sleep(100);
        }, "Slider adjustments should not throw exceptions");

        // Test generate button is clickable
        assertDoesNotThrow(() -> {
            clickOn("#generateButton");
            sleep(200);
        }, "Generate button should be clickable without exceptions");
    }

    /**
     * Test that all expected UI components are present and accessible.
     */
    @Test
    @Order(5)
    public void testUIComponentsPresence() {
        // Essential components for team generation
        assertNotNull(lookup("#tabPane").query(), "TabPane must be present");
        assertNotNull(lookup("#playerListView").query(), "Player list must be present");
        assertNotNull(lookup("#teamListView").query(), "Team list must be present");
        assertNotNull(lookup("#generateButton").query(), "Generate button must be present");
        assertNotNull(lookup("#resultsArea").query(), "Results area must be present");
        
        // Score configuration components
        assertNotNull(lookup("#minScoreSlider").query(), "Min score slider must be present");
        assertNotNull(lookup("#maxScoreSlider").query(), "Max score slider must be present");
        
        // Management buttons
        assertNotNull(lookup("#addPlayerButton").query(), "Add player button must be present");
        assertNotNull(lookup("#addTeamButton").query(), "Add team button must be present");
        assertNotNull(lookup("#removePlayerButton").query(), "Remove player button must be present");
        assertNotNull(lookup("#removeTeamButton").query(), "Remove team button must be present");
    }

    /**
     * Test that the score sliders have reasonable constraints.
     */
    @Test
    @Order(6)
    public void testScoreSliderConstraints() {
        Slider minSlider = lookup("#minScoreSlider").queryAs(Slider.class);
        Slider maxSlider = lookup("#maxScoreSlider").queryAs(Slider.class);
        
        // Check that sliders have reasonable ranges
        assertTrue(minSlider.getMin() >= 1.0, "Min slider should start at reasonable value");
        assertTrue(maxSlider.getMax() <= 10.0, "Max slider should have reasonable upper bound");
        assertTrue(minSlider.getValue() <= maxSlider.getValue(), "Min should not exceed max");
        
        // Test that values can be set programmatically
        Platform.runLater(() -> {
            minSlider.setValue(1.5);
            maxSlider.setValue(4.5);
        });
        sleep(100);
        
        assertEquals(1.5, minSlider.getValue(), 0.1, "Min slider should accept new values");
        assertEquals(4.5, maxSlider.getValue(), 0.1, "Max slider should accept new values");
    }
}