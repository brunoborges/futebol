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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for loading TeamMaker configuration from JSON files.
 */
public class JsonConfigLoader {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Load configuration from a JSON file.
     * 
     * @param filePath path to the JSON configuration file
     * @return TeamMakerConfig loaded from the file
     * @throws IOException if the file cannot be read or parsed
     */
    public static TeamMakerConfig loadFromFile(String filePath) throws IOException {
        return loadFromFile(Path.of(filePath));
    }
    
    /**
     * Load configuration from a JSON file.
     * 
     * @param filePath path to the JSON configuration file
     * @return TeamMakerConfig loaded from the file
     * @throws IOException if the file cannot be read or parsed
     */
    public static TeamMakerConfig loadFromFile(Path filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            return loadFromInputStream(inputStream);
        }
    }
    
    /**
     * Load configuration from a classpath resource.
     * 
     * @param resourceName name of the resource file
     * @return TeamMakerConfig loaded from the resource
     * @throws IOException if the resource cannot be read or parsed
     */
    public static TeamMakerConfig loadFromResource(String resourceName) throws IOException {
        try (InputStream inputStream = JsonConfigLoader.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourceName);
            }
            return loadFromInputStream(inputStream);
        }
    }
    
    /**
     * Load configuration from an InputStream.
     * 
     * @param inputStream the input stream containing JSON data
     * @return TeamMakerConfig loaded from the stream
     * @throws IOException if the stream cannot be read or parsed
     */
    public static TeamMakerConfig loadFromInputStream(InputStream inputStream) throws IOException {
        TeamMakerConfig config = objectMapper.readValue(inputStream, TeamMakerConfig.class);
        validateConfig(config);
        return config;
    }
    
    /**
     * Save configuration to a JSON file.
     * 
     * @param config the configuration to save
     * @param filePath path where to save the JSON file
     * @throws IOException if the file cannot be written
     */
    public static void saveToFile(TeamMakerConfig config, String filePath) throws IOException {
        saveToFile(config, Path.of(filePath));
    }
    
    /**
     * Save configuration to a JSON file.
     * 
     * @param config the configuration to save
     * @param filePath path where to save the JSON file
     * @throws IOException if the file cannot be written
     */
    public static void saveToFile(TeamMakerConfig config, Path filePath) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), config);
    }
    
    /**
     * Validate the loaded configuration.
     * 
     * @param config the configuration to validate
     * @throws IllegalArgumentException if the configuration is invalid
     */
    private static void validateConfig(TeamMakerConfig config) throws IllegalArgumentException {
        // Use the comprehensive validation method from TeamMakerConfig
        config.validate();
    }
}