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
 * Result object containing the generated teams and balance information.
 */
public class TeamMakerResult {
    private final List<Team> teams;
    private final boolean balanced;
    private final double minimumStrength;
    private final double maximumStrength;

    public TeamMakerResult(List<Team> teams, boolean balanced, double minimumStrength, double maximumStrength) {
        this.teams = teams;
        this.balanced = balanced;
        this.minimumStrength = minimumStrength;
        this.maximumStrength = maximumStrength;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public boolean isBalanced() {
        return balanced;
    }

    public double getMinimumStrength() {
        return minimumStrength;
    }

    public double getMaximumStrength() {
        return maximumStrength;
    }
}