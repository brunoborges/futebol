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
 * Formats and prints team results in an attractive and readable way.
 */
public class TeamResultFormatter {

    private final boolean verbose;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_PURPLE = "\u001B[35m";

    public TeamResultFormatter(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Print the complete team results with attractive formatting.
     * 
     * @param result the team maker result to display
     */
    public void printResults(TeamMakerResult result) {
        printHeader();
        printTeams(result.getTeams());
        printSummary(result);
        printFooter();
    }

    /**
     * Print an attractive header for the team results.
     */
    private void printHeader() {
        System.out.println();
        System.out.println(colorize("""
                ╔══════════════════════════════════════════════════════════════════╗
                ║                     ⚽ TEAM DRAW RESULTS ⚽                      ║
                ╚══════════════════════════════════════════════════════════════════╝
                """, ANSI_CYAN));
        System.out.println();
    }

    /**
     * Print all teams with enhanced formatting.
     * 
     * @param teams the list of teams to display
     */
    private void printTeams(List<Team> teams) {
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            printTeam(team, i + 1);

            // Add spacing between teams, but not after the last one
            if (i < teams.size() - 1) {
                System.out.println();
            }
        }
    }

    /**
     * Print a single team with attractive formatting.
     * 
     * @param team       the team to display
     * @param teamNumber the team number for display purposes
     */
    private void printTeam(Team team, int teamNumber) {
        String teamHeader = String.format("Team #%d - %s", teamNumber, team.getName());
        String strengthInfo = String.format("Strength: %.1f", team.getScore());

        System.out.println(colorize("┌─── " + teamHeader, ANSI_BLUE));
        System.out.println(colorize("│ " + strengthInfo, ANSI_GREEN));
        System.out.println(colorize("├─ Players:", ANSI_YELLOW));

        // Print each player with attractive formatting
        team.getPlayers().forEach(player -> {
            String playerInfo = String.format("│   • %s (%.1f)", player.name(), player.score());
            System.out.println(colorize(playerInfo, ANSI_PURPLE));
        });

        System.out.println(colorize("└" + "─".repeat(63), ANSI_BLUE));
    }

    /**
     * Print a summary of the team balance and statistics.
     * 
     * @param result the team maker result
     */
    private void printSummary(TeamMakerResult result) {
        System.out.println();
        System.out.println(colorize("📊 SUMMARY", ANSI_BOLD + ANSI_CYAN));
        System.out.println(colorize("─".repeat(60), ANSI_CYAN));

        List<Team> teams = result.getTeams();

        // Calculate statistics
        double totalStrength = teams.stream().mapToDouble(Team::getScore).sum();
        double avgStrength = totalStrength / teams.size();
        double minStrength = teams.stream().mapToDouble(Team::getScore).min().orElse(0);
        double maxStrength = teams.stream().mapToDouble(Team::getScore).max().orElse(0);
        double strengthDifference = maxStrength - minStrength;

        System.out.printf("Total Teams: %s%d%s%n", ANSI_BOLD, teams.size(), ANSI_RESET);
        System.out.printf("Average Team Strength: %s%.1f%s%n", ANSI_BOLD, avgStrength, ANSI_RESET);
        System.out.printf("Strength Range: %s%.1f - %.1f%s%n", ANSI_BOLD, minStrength, maxStrength, ANSI_RESET);
        System.out.printf("Max Difference: %s%.1f%s", ANSI_BOLD, strengthDifference, ANSI_RESET);

        // Balance indicator
        if (result.isBalanced()) {
            System.out.println(colorize(" ✅ WELL BALANCED!", ANSI_GREEN));
        } else {
            System.out.println(colorize(" ⚠️  Needs rebalancing", ANSI_YELLOW));
        }

        if (verbose && strengthDifference < 1.0) {
            System.out.println(colorize("🎯 Excellent balance achieved - difference under 1.0!", ANSI_GREEN));
        }
    }

    /**
     * Print an attractive footer to conclude the results.
     */
    private void printFooter() {
        System.out.println();
        System.out.println(colorize("""
                ╔══════════════════════════════════════════════════════════════════╗
                ║                🏆 GOOD LUCK WITH YOUR MATCHES! 🏆                ║
                ╚══════════════════════════════════════════════════════════════════╝
                """, ANSI_CYAN));
        System.out.println();
    }

    /**
     * Apply color formatting to text if colors are supported.
     * 
     * @param text      the text to colorize
     * @param colorCode the ANSI color code
     * @return the colorized text or plain text if colors aren't supported
     */
    private String colorize(String text, String colorCode) {
        // Check if we're in a terminal that supports colors
        if (System.console() != null || System.getenv("TERM") != null) {
            return colorCode + text + ANSI_RESET;
        }
        return text;
    }

    /**
     * Center text within a specified width.
     * 
     * @param text  the text to center
     * @param width the total width
     * @return the centered text
     */
    private String center(String text, int width) {
        int padding = Math.max(0, width - text.length());
        int leftPadding = padding / 2;
        int rightPadding = padding - leftPadding;
        return " ".repeat(leftPadding) + text + " ".repeat(rightPadding);
    }
}