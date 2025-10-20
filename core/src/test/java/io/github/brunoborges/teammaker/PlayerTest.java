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
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Player Tests")
class PlayerTest {

    @Test
    @DisplayName("Should create player with name and score")
    void shouldCreatePlayerWithNameAndScore() {
        // Given
        String name = "John Doe";
        double score = 4.5;
        
        // When
        Player player = new Player(name, score);
        
        // Then
        assertEquals(name, player.name());
        assertEquals(score, player.score());
    }

    @Test
    @DisplayName("Should handle integer score values")
    void shouldHandleIntegerScoreValues() {
        // Given
        String name = "Jane Smith";
        double score = 3.0;
        
        // When
        Player player = new Player(name, score);
        
        // Then
        assertEquals(name, player.name());
        assertEquals(score, player.score());
    }

    @Test
    @DisplayName("Should handle decimal score values")
    void shouldHandleDecimalScoreValues() {
        // Given
        String name = "Mike Johnson";
        double score = 2.75;
        
        // When
        Player player = new Player(name, score);
        
        // Then
        assertEquals(name, player.name());
        assertEquals(score, player.score(), 0.001);
    }

    @Test
    @DisplayName("Should handle zero score")
    void shouldHandleZeroScore() {
        // Given
        String name = "Zero Player";
        double score = 0.0;
        
        // When
        Player player = new Player(name, score);
        
        // Then
        assertEquals(name, player.name());
        assertEquals(score, player.score());
    }

    @Test
    @DisplayName("Should handle negative score")
    void shouldHandleNegativeScore() {
        // Given
        String name = "Negative Player";
        double score = -1.5;
        
        // When
        Player player = new Player(name, score);
        
        // Then
        assertEquals(name, player.name());
        assertEquals(score, player.score());
    }

    @Test
    @DisplayName("Should handle empty name")
    void shouldHandleEmptyName() {
        // Given
        String name = "";
        double score = 3.0;
        
        // When
        Player player = new Player(name, score);
        
        // Then
        assertEquals(name, player.name());
        assertEquals(score, player.score());
    }

    @Test
    @DisplayName("Should handle null name")
    void shouldHandleNullName() {
        // Given
        String name = null;
        double score = 3.0;
        
        // When
        Player player = new Player(name, score);
        
        // Then
        assertNull(player.name());
        assertEquals(score, player.score());
    }

}