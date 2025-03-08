package com.zetax.zetabrowser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
// Remove these duplicate imports
// import javafx.scene.Scene;
// import javafx.stage.Stage;
// import javafx.stage.Window;

public class ProfileManager {
    private static final String PROFILES_FILE = "profiles.json";
    private Map<String, Profile> profiles;
    private Profile currentProfile;
    private final ObjectMapper objectMapper;

    public ProfileManager() {
        this.objectMapper = new ObjectMapper();
        this.profiles = new HashMap<>();
        loadProfiles();
    }

    public void createProfile(String username) {
        Profile profile = new Profile(username);
        profiles.put(username, profile);
        saveProfiles();
    }

    public void switchProfile(String username) {
        currentProfile = profiles.get(username);
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public List<String> getProfileNames() {
        return new ArrayList<>(profiles.keySet());
    }

    private void loadProfiles() {
        File file = new File(PROFILES_FILE);
        if (file.exists()) {
            try {
                profiles = objectMapper.readValue(file, new TypeReference<Map<String, Profile>>() {});
            } catch (IOException e) {
                e.printStackTrace();
                profiles = new HashMap<>();
            }
        } else {
            profiles = new HashMap<>();
        }
    }

    public void saveProfiles() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File("profiles.json"), profiles);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving profiles: " + e.getMessage());
        }
    }

    public void updateCurrentProfile() {
        if (currentProfile != null) {
            profiles.put(currentProfile.getUsername(), currentProfile);
            saveProfiles();
        }
    }
    // Add this method to your ProfileManager class
    // Fix the getProfile method
    public Profile getProfile(String profileName) {
        // Simply return the profile from the map using the profileName as key
        return profiles.get(profileName);
    }
    // Add a method to apply theme to any scene
        public void applyThemeToScene(Scene scene) {
            if (scene != null && currentProfile != null) {
                scene.getStylesheets().clear();
                if (currentProfile.isDarkMode()) {
                    scene.getStylesheets().add(getClass().getResource("/com/zetax/zetabrowser/style.css").toExternalForm());
                } else {
                    scene.getStylesheets().add(getClass().getResource("/com/zetax/zetabrowser/light-style.css").toExternalForm());
                }
            }
        }
        // Add a method to apply theme to all open windows
        public void applyThemeToAllWindows() {
            for (Window window : Window.getWindows()) {
                if (window instanceof Stage) {
                    Scene scene = ((Stage) window).getScene();
                    applyThemeToScene(scene);
                }
            }
        }
}  // Add this closing brace for the class