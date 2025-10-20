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

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import io.github.brunoborges.teammaker.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JavaFX GUI application for TeamMaker.
 * Provides a user-friendly interface for configuring teams and members,
 * and running team draws.
 */
public class TeamMakerFXApp extends Application {

    private ObservableList<Player> players = FXCollections.observableArrayList();
    private ObservableList<String> teamNames = FXCollections.observableArrayList();
    private ListView<Player> playerListView;
    private ListView<String> teamListView;
    private Slider minScoreSlider;
    private Slider maxScoreSlider;
    private Label minScoreLabel;
    private Label maxScoreLabel;
    private TextArea resultsArea;
    private TabPane tabPane;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("👥 TeamMaker - Balanced Team Generator");
        
        // Create main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // Create tabs
        tabPane = new TabPane();
        tabPane.setId("tabPane");
        
        // Configuration tab
        Tab configTab = new Tab("Configuration");
        configTab.setClosable(false);
        configTab.setContent(createConfigurationPane());
        
        // Results tab
        Tab resultsTab = new Tab("Results");
        resultsTab.setClosable(false);
        resultsTab.setContent(createResultsPane());
        
        tabPane.getTabs().addAll(configTab, resultsTab);
        
        root.setCenter(tabPane);
        
        // Create menu bar
        MenuBar menuBar = createMenuBar(primaryStage);
        root.setTop(menuBar);
        
        // Create status bar
        HBox statusBar = createStatusBar();
        root.setBottom(statusBar);
        
        // Load default configuration
        loadDefaultPlayers();
        loadDefaultTeams();
        
        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/io/github/brunoborges/teammaker/gui/styles.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates the configuration pane with members and teams management.
     */
    private VBox createConfigurationPane() {
        VBox configPane = new VBox(10);
        configPane.setPadding(new Insets(10));
        
        // Split configuration into two columns
        HBox mainConfigBox = new HBox(20);
        
        // Left side - Members
        VBox playersBox = createPlayersSection();
        
        // Right side - Teams and Settings
        VBox teamsBox = createTeamsSection();
        
        mainConfigBox.getChildren().addAll(playersBox, teamsBox);
        HBox.setHgrow(playersBox, Priority.ALWAYS);
        HBox.setHgrow(teamsBox, Priority.ALWAYS);
        
        // Rating scale section
        VBox scoreScaleBox = createScoreScaleSection();
        
        // Action buttons
        HBox actionButtonsBox = createActionButtons();
        
        configPane.getChildren().addAll(mainConfigBox, scoreScaleBox, actionButtonsBox);
        
        return configPane;
    }

    /**
     * Creates the members management section.
     */
    private VBox createPlayersSection() {
        VBox playersBox = new VBox(10);
        
        Label playersLabel = new Label("👥 Members");
        playersLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        // Members list
        playerListView = new ListView<>(players);
        playerListView.setId("playerListView");
        playerListView.setPrefHeight(200);
        playerListView.setCellFactory(listView -> new PlayerListCell());
        
        // Member input controls
        HBox playerInputBox = new HBox(10);
        TextField playerNameField = new TextField();
        playerNameField.setPromptText("Member Name");
        Spinner<Double> playerScoreSpinner = new Spinner<>(1.0, 5.0, 3.0, 0.1);
        playerScoreSpinner.setEditable(true);
        Button addPlayerButton = new Button("Add Member");
        addPlayerButton.setId("addPlayerButton");
        
        addPlayerButton.setOnAction(e -> {
            String name = playerNameField.getText().trim();
            double score = playerScoreSpinner.getValue();
            
            if (!name.isEmpty()) {
                Player newPlayer = new Player(name, score);
                players.add(newPlayer);
                playerNameField.clear();
                playerScoreSpinner.getValueFactory().setValue(3.0);
            }
        });
        
        // Add member on Enter key
        playerNameField.setOnAction(e -> addPlayerButton.fire());
        
        playerInputBox.getChildren().addAll(playerNameField, playerScoreSpinner, addPlayerButton);
        HBox.setHgrow(playerNameField, Priority.ALWAYS);
        
        // Player management buttons
        HBox playerButtonsBox = new HBox(10);
        Button removePlayerButton = new Button("Remove Selected");
        removePlayerButton.setId("removePlayerButton");
        Button editPlayerButton = new Button("Edit Selected");
        Button clearPlayersButton = new Button("Clear All");
        
        removePlayerButton.setOnAction(e -> {
            Player selected = playerListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                players.remove(selected);
            }
        });
        
        editPlayerButton.setOnAction(e -> editSelectedPlayer());
        
        clearPlayersButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Clear Players");
            alert.setHeaderText("Remove all players?");
            alert.setContentText("This action cannot be undone.");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                players.clear();
            }
        });
        
        playerButtonsBox.getChildren().addAll(removePlayerButton, editPlayerButton, clearPlayersButton);
        
        playersBox.getChildren().addAll(playersLabel, playerListView, playerInputBox, playerButtonsBox);
        
        return playersBox;
    }

    /**
     * Creates the teams management section.
     */
    private VBox createTeamsSection() {
        VBox teamsBox = new VBox(10);
        
        Label teamsLabel = new Label("📋 Teams");
        teamsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        // Teams list
        teamListView = new ListView<>(teamNames);
        teamListView.setId("teamListView");
        teamListView.setPrefHeight(200);
        
        // Team input controls
        HBox teamInputBox = new HBox(10);
        TextField teamNameField = new TextField();
        teamNameField.setPromptText("Team Name");
        Button addTeamButton = new Button("Add Team");
        addTeamButton.setId("addTeamButton");
        
        addTeamButton.setOnAction(e -> {
            String name = teamNameField.getText().trim();
            if (!name.isEmpty() && !teamNames.contains(name)) {
                teamNames.add(name);
                teamNameField.clear();
            }
        });
        
        teamNameField.setOnAction(e -> addTeamButton.fire());
        
        teamInputBox.getChildren().addAll(teamNameField, addTeamButton);
        HBox.setHgrow(teamNameField, Priority.ALWAYS);
        
        // Team management buttons
        HBox teamButtonsBox = new HBox(10);
        Button removeTeamButton = new Button("Remove Selected");
        removeTeamButton.setId("removeTeamButton");
        Button clearTeamsButton = new Button("Clear All");
        
        removeTeamButton.setOnAction(e -> {
            String selected = teamListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                teamNames.remove(selected);
            }
        });
        
        clearTeamsButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Clear Teams");
            alert.setHeaderText("Remove all teams?");
            alert.setContentText("This action cannot be undone.");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                teamNames.clear();
            }
        });
        
        teamButtonsBox.getChildren().addAll(removeTeamButton, clearTeamsButton);
        
        teamsBox.getChildren().addAll(teamsLabel, teamListView, teamInputBox, teamButtonsBox);
        
        return teamsBox;
    }

    /**
     * Creates the rating scale configuration section.
     */
    private VBox createScoreScaleSection() {
        VBox scoreScaleBox = new VBox(10);
        
        Label scoreScaleLabel = new Label("⚖️ Member Rating Scale");
        scoreScaleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        HBox sliderBox = new HBox(20);
        
        // Min score
        VBox minBox = new VBox(5);
        minScoreLabel = new Label("Min Score: 1.0");
        minScoreSlider = new Slider(1.0, 5.0, 1.0);
        minScoreSlider.setId("minScoreSlider");
        minScoreSlider.setMajorTickUnit(1.0);
        minScoreSlider.setMinorTickCount(0);
        minScoreSlider.setSnapToTicks(true);
        minScoreSlider.setShowTickLabels(true);
        minScoreSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            minScoreLabel.setText(String.format("Min Score: %.1f", newVal.doubleValue()));
            if (newVal.doubleValue() >= maxScoreSlider.getValue()) {
                maxScoreSlider.setValue(newVal.doubleValue() + 1.0);
            }
        });
        minBox.getChildren().addAll(minScoreLabel, minScoreSlider);
        
        // Max score
        VBox maxBox = new VBox(5);
        maxScoreLabel = new Label("Max Score: 5.0");
        maxScoreSlider = new Slider(1.0, 5.0, 5.0);
        maxScoreSlider.setId("maxScoreSlider");
        maxScoreSlider.setMajorTickUnit(1.0);
        maxScoreSlider.setMinorTickCount(0);
        maxScoreSlider.setSnapToTicks(true);
        maxScoreSlider.setShowTickLabels(true);
        maxScoreSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            maxScoreLabel.setText(String.format("Max Score: %.1f", newVal.doubleValue()));
            if (newVal.doubleValue() <= minScoreSlider.getValue()) {
                minScoreSlider.setValue(newVal.doubleValue() - 1.0);
            }
        });
        maxBox.getChildren().addAll(maxScoreLabel, maxScoreSlider);
        
        sliderBox.getChildren().addAll(minBox, maxBox);
        HBox.setHgrow(minBox, Priority.ALWAYS);
        HBox.setHgrow(maxBox, Priority.ALWAYS);
        
        scoreScaleBox.getChildren().addAll(scoreScaleLabel, sliderBox);
        
        return scoreScaleBox;
    }

    /**
     * Creates the action buttons section.
     */
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button generateButton = new Button("🎲 Generate Teams");
        generateButton.setId("generateButton");
        generateButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px;");
        generateButton.setOnAction(e -> generateTeams());
        
        Button loadDefaultButton = new Button("📋 Load Sample Members");
        loadDefaultButton.setOnAction(e -> {
            loadDefaultPlayers();
            loadDefaultTeams();
        });
        
        buttonBox.getChildren().addAll(generateButton, loadDefaultButton);
        
        return buttonBox;
    }

    /**
     * Creates the results pane.
     */
    private VBox createResultsPane() {
        VBox resultsPane = new VBox(10);
        resultsPane.setPadding(new Insets(10));
        
        Label resultsLabel = new Label("📊 Team Formation Results");
        resultsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        
        resultsArea = new TextArea();
        resultsArea.setId("resultsArea");
        resultsArea.setEditable(false);
        resultsArea.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 12px;");
        resultsArea.setText("Click 'Generate Teams' to see results here...");
        
        VBox.setVgrow(resultsArea, Priority.ALWAYS);
        
        resultsPane.getChildren().addAll(resultsLabel, resultsArea);
        
        return resultsPane;
    }

    /**
     * Creates the menu bar.
     */
    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();
        
        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem loadConfigItem = new MenuItem("Load Configuration...");
        MenuItem saveConfigItem = new MenuItem("Save Configuration...");
        MenuItem exitItem = new MenuItem("Exit");
        
        loadConfigItem.setOnAction(e -> loadConfiguration(stage));
        saveConfigItem.setOnAction(e -> saveConfiguration(stage));
        exitItem.setOnAction(e -> stage.close());
        
        fileMenu.getItems().addAll(loadConfigItem, saveConfigItem, new SeparatorMenuItem(), exitItem);
        
        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        
        return menuBar;
    }

    /**
     * Creates the status bar.
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5));
        statusBar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");
        
        Label statusLabel = new Label("Ready");
        statusBar.getChildren().add(statusLabel);
        
        return statusBar;
    }

    /**
     * Custom list cell for displaying players.
     */
    private class PlayerListCell extends ListCell<Player> {
        @Override
        protected void updateItem(Player player, boolean empty) {
            super.updateItem(player, empty);
            
            if (empty || player == null) {
                setText(null);
            } else {
                setText(String.format("👤 %s (%.1f)", player.name(), player.score()));
            }
        }
    }

    /**
     * Loads default players.
     */
    private void loadDefaultPlayers() {
        players.clear();
        // Use some default players since DefaultPlayers enum is not public
        players.addAll(List.of(
            new Player("Alex", 3.0),
            new Player("Andre", 2.0),
            new Player("Augusto", 3.0),
            new Player("Bruno", 4.0),
            new Player("Diego", 3.0),
            new Player("Diogo", 4.0),
            new Player("Duda", 4.0),
            new Player("Felipe", 4.0),
            new Player("Guilhermo", 3.0),
            new Player("Jean", 3.0),
            new Player("Juan", 2.0),
            new Player("Leo", 4.0),
            new Player("Leonardo", 3.0),
            new Player("Lucio", 3.0),
            new Player("Marcelo", 3.0),
            new Player("Pedro", 3.0),
            new Player("Rafael", 3.0),
            new Player("Rodrigo", 4.0),
            new Player("Tiago", 3.0),
            new Player("Thiago", 2.0)
        ));
    }

    /**
     * Loads default team names.
     */
    private void loadDefaultTeams() {
        teamNames.clear();
        for (int i = 1; i <= 10; i++) {
            teamNames.add("Team " + (char)('A' + i - 1));
        }
    }

    /**
     * Generates teams and displays results.
     */
    private void generateTeams() {
        if (players.isEmpty()) {
            showAlert("No Players", "Please add some players before generating teams.");
            return;
        }
        
        if (teamNames.isEmpty()) {
            showAlert("No Teams", "Please add some teams before generating.");
            return;
        }
        
        if (players.size() % teamNames.size() != 0) {
            showAlert("Invalid Configuration", 
                String.format("Number of players (%d) must be divisible by number of teams (%d).", 
                    players.size(), teamNames.size()));
            return;
        }
        
        try {
            // Create configuration
            TeamMakerConfig config = new TeamMakerConfig(
                new ArrayList<>(players),
                new ArrayList<>(teamNames),
                new TeamMakerConfig.ScoreScale(minScoreSlider.getValue(), maxScoreSlider.getValue())
            );
            
            // Generate teams
            TeamMakerResult result = TeamMaker.createBalancedTeamsFromConfig(config);
            
            // Display results
            displayResults(result);
            
        } catch (Exception e) {
            showAlert("Error", "Failed to generate teams: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays the team generation results.
     */
    private void displayResults(TeamMakerResult result) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════════════════════════════════\n");
        sb.append("                     📊 TEAM FORMATION RESULTS 📊                     \n");
        sb.append("═══════════════════════════════════════════════════════════════════\n\n");
        
        List<Team> teams = result.getTeams();
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            sb.append(String.format("┌─── Team #%d - %s\n", i + 1, team.getName()));
            sb.append(String.format("│ Rating: %.1f\n", team.getScore()));
            sb.append("├─ Players:\n");
            
            for (Player player : team.getPlayers()) {
                sb.append(String.format("│   • %s (%.1f)\n", player.name(), player.score()));
            }
            
            sb.append("└").append("─".repeat(63)).append("\n\n");
        }
        
        // Add summary
        sb.append("📊 SUMMARY\n");
        sb.append("─".repeat(60)).append("\n");
        sb.append(String.format("Total Teams: %d\n", teams.size()));
        
        double avgStrength = teams.stream().mapToDouble(Team::getScore).average().orElse(0.0);
        double minStrength = teams.stream().mapToDouble(Team::getScore).min().orElse(0.0);
        double maxStrength = teams.stream().mapToDouble(Team::getScore).max().orElse(0.0);
        double difference = maxStrength - minStrength;
        
        sb.append(String.format("Average Team Rating: %.1f\n", avgStrength));
        sb.append(String.format("Rating Range: %.1f - %.1f\n", minStrength, maxStrength));
        sb.append(String.format("Max Difference: %.1f", difference));
        
        if (difference <= 1.0) {
            sb.append(" ✅ WELL BALANCED!");
        } else if (difference <= 2.0) {
            sb.append(" ⚠️ MODERATELY BALANCED");
        } else {
            sb.append(" ❌ UNBALANCED");
        }
        
        sb.append("\n\n");
        sb.append("═══════════════════════════════════════════════════════════════════\n");
        sb.append("               � GOOD LUCK WITH YOUR ACTIVITIES! �                \n");
        sb.append("═══════════════════════════════════════════════════════════════════\n");
        
        resultsArea.setText(sb.toString());
        
        // Switch to results tab
        tabPane.getSelectionModel().select(1);
    }

    /**
     * Edits the selected player.
     */
    private void editSelectedPlayer() {
        Player selected = playerListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Dialog<Player> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");
        dialog.setHeaderText("Edit member information");
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(selected.name());
        Spinner<Double> scoreSpinner = new Spinner<>(1.0, 5.0, selected.score(), 0.1);
        scoreSpinner.setEditable(true);
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Score:"), 0, 1);
        grid.add(scoreSpinner, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Player(nameField.getText().trim(), scoreSpinner.getValue());
            }
            return null;
        });
        
        Optional<Player> result = dialog.showAndWait();
        result.ifPresent(newPlayer -> {
            int index = players.indexOf(selected);
            players.set(index, newPlayer);
        });
    }

    /**
     * Loads configuration from a JSON file.
     */
    private void loadConfiguration(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Configuration");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                TeamMakerConfig config = objectMapper.readValue(file, TeamMakerConfig.class);
                
                // Load players
                players.clear();
                players.addAll(config.getPlayers());
                
                // Load teams
                teamNames.clear();
                teamNames.addAll(config.getTeamNames());
                
                // Load score scale
                TeamMakerConfig.ScoreScale scale = config.getScoreScale();
                if (scale != null) {
                    minScoreSlider.setValue(scale.getMin());
                    maxScoreSlider.setValue(scale.getMax());
                }
                
                showInfo("Configuration Loaded", "Configuration loaded successfully from " + file.getName());
                
            } catch (IOException e) {
                showAlert("Error", "Failed to load configuration: " + e.getMessage());
            }
        }
    }

    /**
     * Saves configuration to a JSON file.
     */
    private void saveConfiguration(Stage stage) {
        if (players.isEmpty() || teamNames.isEmpty()) {
            showAlert("Nothing to Save", "Please add members and teams before saving.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Configuration");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialFileName("team-config.json");
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                TeamMakerConfig config = new TeamMakerConfig(
                    new ArrayList<>(players),
                    new ArrayList<>(teamNames),
                    new TeamMakerConfig.ScoreScale(minScoreSlider.getValue(), maxScoreSlider.getValue())
                );
                
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, config);
                showInfo("Configuration Saved", "Configuration saved successfully to " + file.getName());
                
            } catch (IOException e) {
                showAlert("Error", "Failed to save configuration: " + e.getMessage());
            }
        }
    }

    /**
     * Shows an information dialog.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows the about dialog.
     */
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About TeamMaker");
        alert.setHeaderText("👥 TeamMaker GUI v1.0");
        alert.setContentText("A JavaFX application for generating balanced teams.\n\n" +
                           "Features:\n" +
                           "• Configure players and teams\n" +
                           "• Smart team balancing algorithm\n" +
                           "• Save/load configurations\n" +
                           "• Beautiful formatted results\n\n" +
                           "Built with Java 21 & JavaFX");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}