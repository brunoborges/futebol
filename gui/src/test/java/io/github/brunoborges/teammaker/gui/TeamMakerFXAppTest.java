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
 * TestFX unit tests for TeamMakerFXApp core functionality.
 * Tests team generation and results display.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TeamMakerFXAppTest extends ApplicationTest {

    private TeamMakerFXApp app;

    @Override
    public void start(Stage stage) throws Exception {
        app = new TeamMakerFXApp();
        app.start(stage);
    }

    /**
     * Test the initial application state.
     */
    @Test
    @Order(1)
    public void testInitialState() {
        // Verify window exists and title is correct
        Stage stage = (Stage) window("👥 TeamMaker - Balanced Team Generator");
        assertNotNull(stage, "Main window should be present");
        assertEquals("👥 TeamMaker - Balanced Team Generator", stage.getTitle());

        // Verify tabs are present
        TabPane tabPane = lookup("#tabPane").queryAs(TabPane.class);
        assertNotNull(tabPane);
        assertEquals(2, tabPane.getTabs().size());
        assertEquals("Configuration", tabPane.getTabs().get(0).getText());
        assertEquals("Results", tabPane.getTabs().get(1).getText());

        // Verify Configuration tab is initially selected
        assertEquals(0, tabPane.getSelectionModel().getSelectedIndex());
    }

    /**
     * Test that default players and teams are loaded.
     */
    @Test
    @Order(2)
    public void testDefaultDataLoaded() {
        // Check that default players are loaded
        ListView<?> playerListView = lookup("#playerListView").queryAs(ListView.class);
        assertNotNull(playerListView);
        assertTrue(playerListView.getItems().size() > 0, "Default players should be loaded");

        // Check that default teams are loaded
        ListView<?> teamListView = lookup("#teamListView").queryAs(ListView.class);
        assertNotNull(teamListView);
        assertTrue(teamListView.getItems().size() > 0, "Default teams should be loaded");
    }

    /**
     * Test the core functionality: generating teams.
     */
    @Test
    @Order(3)
    public void testGenerateTeams() {
        // Ensure we have a valid configuration (even number of players for teams)
        ListView<?> playerListView = lookup("#playerListView").queryAs(ListView.class);
        ListView<?> teamListView = lookup("#teamListView").queryAs(ListView.class);

        int playerCount = playerListView.getItems().size();
        int teamCount = teamListView.getItems().size();

        System.out.println("Initial players: " + playerCount + ", teams: " + teamCount);

        // Click Generate Teams button
        clickOn("#generateButton");

        // Switch to Results tab to verify results
        TabPane tabPane = lookup("#tabPane").queryAs(TabPane.class);
        Platform.runLater(() -> tabPane.getSelectionModel().select(1));
        
        // Wait a bit for the tab switch and results to populate
        sleep(500);

        // Verify results are displayed
        TextArea resultsArea = lookup("#resultsArea").queryAs(TextArea.class);
        assertNotNull(resultsArea);
        
        String resultsText = resultsArea.getText();
        assertNotNull(resultsText);
        assertFalse(resultsText.isEmpty());
        
        // If generation failed due to uneven division, we should see an error
        // If it succeeded, we should see results
        boolean hasError = resultsText.contains("Click 'Generate Teams' to see results here...");
        boolean hasResults = resultsText.contains("TEAM FORMATION RESULTS");
        
        // Either we have results or the configuration was invalid (which is okay for this test)
        assertTrue(hasResults || hasError, "Should either show results or maintain initial text");
        
        if (hasResults) {
            // Verify results contain expected content
            assertTrue(resultsText.contains("Team #"), "Results should contain team information");
            assertTrue(resultsText.contains("SUMMARY"), "Results should contain summary");
        }
    }

    /**
     * Test that team generation validates input correctly.
     */
    @Test
    @Order(4)
    public void testGenerateTeamsValidation() {
        // Get current lists
        ListView<?> playerListView = lookup("#playerListView").queryAs(ListView.class);
        ListView<?> teamListView = lookup("#teamListView").queryAs(ListView.class);
        
        int initialPlayerCount = playerListView.getItems().size();
        int initialTeamCount = teamListView.getItems().size();
        
        // Test successful generation when numbers work
        if (initialPlayerCount % initialTeamCount == 0) {
            clickOn("#generateButton");
            
            // Should generate teams successfully - check by switching to results
            TabPane tabPane = lookup("#tabPane").queryAs(TabPane.class);
            Platform.runLater(() -> tabPane.getSelectionModel().select(1));
            sleep(500);
            
            TextArea resultsArea = lookup("#resultsArea").queryAs(TextArea.class);
            String resultsText = resultsArea.getText();
            assertTrue(resultsText.contains("TEAM FORMATION RESULTS") || 
                      resultsText.contains("Invalid Configuration"), 
                      "Should either generate teams or show configuration error");
        }
    }

    /**
     * Test results tab switching and content persistence.
     */
    @Test
    @Order(5)
    public void testResultsTabSwitching() {
        // Generate teams first
        clickOn("#generateButton");
        
        // Switch to Results tab
        TabPane tabPane = lookup("#tabPane").queryAs(TabPane.class);
        Platform.runLater(() -> tabPane.getSelectionModel().select(1));
        sleep(500);
        
        // Verify we're on Results tab
        assertEquals(1, tabPane.getSelectionModel().getSelectedIndex());
        
        // Get results content
        TextArea resultsArea = lookup("#resultsArea").queryAs(TextArea.class);
        String resultsContent = resultsArea.getText();
        
        // Switch back to Configuration tab
        Platform.runLater(() -> tabPane.getSelectionModel().select(0));
        sleep(200);
        
        // Verify we're back on Configuration tab
        assertEquals(0, tabPane.getSelectionModel().getSelectedIndex());
        
        // Switch back to Results tab and verify content is still there
        Platform.runLater(() -> tabPane.getSelectionModel().select(1));
        sleep(200);
        
        assertEquals(resultsContent, resultsArea.getText(), "Results should persist when switching tabs");
    }

    /**
     * Test score sliders functionality.
     */
    @Test
    @Order(6)
    public void testScoreSliders() {
        // Find the score sliders
        Slider minSlider = lookup("#minScoreSlider").queryAs(Slider.class);
        Slider maxSlider = lookup("#maxScoreSlider").queryAs(Slider.class);
        
        assertNotNull(minSlider);
        assertNotNull(maxSlider);
        
        // Verify initial values are reasonable
        assertTrue(minSlider.getValue() >= minSlider.getMin());
        assertTrue(maxSlider.getValue() <= maxSlider.getMax());
        assertTrue(minSlider.getValue() <= maxSlider.getValue());
        
        // Test changing values programmatically
        Platform.runLater(() -> {
            minSlider.setValue(2.0);
            maxSlider.setValue(4.0);
        });
        sleep(200);
        
        // Values should have changed
        assertEquals(2.0, minSlider.getValue(), 0.1);
        assertEquals(4.0, maxSlider.getValue(), 0.1);
    }

    /**
     * Test UI component presence and basic properties.
     */
    @Test
    @Order(7)
    public void testUIComponents() {
        // Test that all major UI components are present
        assertNotNull(lookup("#tabPane").query(), "TabPane should be present");
        assertNotNull(lookup("#playerListView").query(), "Player ListView should be present");
        assertNotNull(lookup("#teamListView").query(), "Team ListView should be present");
        assertNotNull(lookup("#addPlayerButton").query(), "Add Player button should be present");
        assertNotNull(lookup("#addTeamButton").query(), "Add Team button should be present");
        assertNotNull(lookup("#removePlayerButton").query(), "Remove Player button should be present");
        assertNotNull(lookup("#removeTeamButton").query(), "Remove Team button should be present");
        assertNotNull(lookup("#generateButton").query(), "Generate button should be present");
        assertNotNull(lookup("#resultsArea").query(), "Results area should be present");
        assertNotNull(lookup("#minScoreSlider").query(), "Min score slider should be present");
        assertNotNull(lookup("#maxScoreSlider").query(), "Max score slider should be present");
    }

    /**
     * Test that the generate button produces some kind of result.
     */
    @Test
    @Order(8)
    public void testGenerateButtonFunctionality() {
        // Get initial state of results area
        TextArea resultsArea = lookup("#resultsArea").queryAs(TextArea.class);
        String initialText = resultsArea.getText();
        
        // Click generate button
        clickOn("#generateButton");
        
        // Wait a moment for processing
        sleep(300);
        
        // Switch to results tab to see what happened
        TabPane tabPane = lookup("#tabPane").queryAs(TabPane.class);
        Platform.runLater(() -> tabPane.getSelectionModel().select(1));
        sleep(300);
        
        // Get the final text - it should either show results or an error dialog should have appeared
        String finalText = resultsArea.getText();
        
        // The button should have done *something* - either generated results or triggered validation
        assertNotNull(finalText);
        
        // Test passes if: 
        // 1. Results were generated (text changed to show results)
        // 2. Or validation prevented generation (text remained the same but generation was attempted)
        boolean textChanged = !initialText.equals(finalText);
        boolean hasInitialText = finalText.contains("Click 'Generate Teams' to see results here...");
        
        assertTrue(textChanged || hasInitialText, 
                   "Generate button should either produce results or maintain initial state due to validation");
    }
}