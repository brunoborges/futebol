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

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Command-line application for team generation using picocli.
 * This class handles user interaction and output formatting,
 * delegating the core logic to TeamMaker.
 */
@Command(
    name = "teammaker",
    mixinStandardHelpOptions = true,
    version = "TeamMaker 1.0",
    description = "Generate balanced teams from a list of players"
)
public class TeamMakerApp implements Callable<Integer> {

    @Parameters(
        index = "0", 
        arity = "0..1",
        description = "JSON configuration file path (optional)"
    )
    private String configFile;

    @Option(
        names = {"-r", "--resource"}, 
        description = "Use resource file instead of external file (default: team-config.json)"
    )
    private String resourceName = "team-config.json";

    @Option(
        names = {"-d", "--default"}, 
        description = "Use default hard-coded players instead of JSON configuration"
    )
    private boolean useDefault = false;

    @Option(
        names = {"-v", "--verbose"}, 
        description = "Enable verbose output"
    )
    private boolean verbose = false;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new TeamMakerApp()).execute(args);
        // Only call System.exit if we're not in a test environment
        if (!isInTest()) {
            System.exit(exitCode);
        }
    }

    /**
     * Check if we're running in a test environment by looking at the stack trace.
     */
    private static boolean isInTest() {
        return StackWalker.getInstance()
                .walk(frames -> frames
                        .anyMatch(frame -> frame.getClassName().contains("junit") || 
                                         frame.getClassName().contains("Test") ||
                                         frame.getClassName().contains("surefire")));
    }

    @Override
    public Integer call() throws Exception {
        try {
            TeamResultFormatter formatter = new TeamResultFormatter(verbose);
            
            if (useDefault) {
                if (verbose) System.out.println("Using default hard-coded players...");
                run(formatter);
            } else if (configFile != null) {
                if (verbose) System.out.println("Loading configuration from: " + configFile);
                runWithConfig(configFile, formatter);
            } else {
                if (verbose) System.out.println("Loading configuration from resource: " + resourceName);
                try {
                    runWithResourceConfig(resourceName, formatter);
                } catch (IOException e) {
                    if (verbose) System.out.println("Resource not found, falling back to default players...");
                    run(formatter);
                }
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            return 1;
        }
    }

    /**
     * Run the application with default hard-coded players.
     */
    private void run(TeamResultFormatter formatter) {
        TeamMaker teamMaker = new TeamMaker();

        // Keep trying until we get balanced teams
        TeamMakerResult result;
        do {
            result = teamMaker.createBalancedTeams();
        } while (!result.isBalanced());

        // Print the results using the formatter
        formatter.printResults(result);
    }

    /**
     * Run the application with JSON configuration from a file.
     * 
     * @param configPath path to the JSON configuration file
     * @param formatter the result formatter
     * @throws IOException if the configuration cannot be loaded
     */
    private void runWithConfig(String configPath, TeamResultFormatter formatter) throws IOException {
        TeamMakerResult result;
        do {
            result = TeamMaker.createBalancedTeamsFromConfig(configPath);
        } while (!result.isBalanced());

        // Print the results using the formatter
        formatter.printResults(result);
    }

    /**
     * Run the application with JSON configuration from resources.
     * 
     * @param resourceName name of the JSON configuration resource
     * @param formatter the result formatter
     * @throws IOException if the configuration cannot be loaded
     */
    private void runWithResourceConfig(String resourceName, TeamResultFormatter formatter) throws IOException {
        TeamMakerResult result;
        do {
            result = TeamMaker.createBalancedTeamsFromResource(resourceName);
        } while (!result.isBalanced());

        // Print the results using the formatter
        formatter.printResults(result);
    }
}