package com.zetax.zetabrowser;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class ProfileViewController {
    @FXML
    private ListView<String> profileListView;
    private ProfileManager profileManager;
    private Stage mainStage;
    
    @FXML
    private CheckBox darkModeCheckBox;
    
    @FXML
    public void initialize() {
        // Use the shared ProfileManager instance
        profileManager = HelloApplication.getProfileManager();
        updateProfileList();
        
        // Add listener to profile selection to update theme checkbox
        profileListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Profile profile = profileManager.getProfile(newVal);
                if (profile != null) {
                    darkModeCheckBox.setSelected(profile.isDarkMode());
                }
            }
        });
    }
    
    @FXML
    private void onThemeChange() {
        String selectedProfile = profileListView.getSelectionModel().getSelectedItem();
        if (selectedProfile != null) {
            Profile profile = profileManager.getProfile(selectedProfile);
            if (profile != null) {
                profile.setDarkMode(darkModeCheckBox.isSelected());
                profileManager.saveProfiles();
                applyTheme(darkModeCheckBox.isSelected());
            }
        }
    }
    
    private void applyTheme(boolean darkMode) {
        Scene scene = profileListView.getScene();
        if (scene != null) {
            if (darkMode) {
                scene.getStylesheets().remove(getClass().getResource("light-style.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            } else {
                scene.getStylesheets().remove(getClass().getResource("style.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource("light-style.css").toExternalForm());
            }
        }
    }
    
    @FXML
    private void continueWithProfile() {
        String selectedProfile = profileListView.getSelectionModel().getSelectedItem();
        if (selectedProfile == null) {
            showAlert("Please select a profile or create a new one.");
            return;
        }
    
        Profile profile = profileManager.getProfile(selectedProfile);
        if (profile != null) {
            // Save theme preference
            profile.setDarkMode(darkModeCheckBox.isSelected());
            profileManager.saveProfiles();
        }
        
        profileManager.switchProfile(selectedProfile);
        launchMainApplication();
    }

    private void updateProfileList() {
        profileListView.getItems().clear();
        profileListView.getItems().addAll(profileManager.getProfileNames());
    }

    @FXML
    private void createNewProfile() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Profile");
        dialog.setHeaderText("Enter profile name:");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                profileManager.createProfile(name);
                updateProfileList();
                profileListView.getSelectionModel().select(name);
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void launchMainApplication() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            
            // Get the controller after loading the FXML
            HelloController controller = loader.getController();
            
            // Get the current stage
            Stage stage = (Stage) profileListView.getScene().getWindow();
            
            // Set up the new scene
            stage.setScene(scene);
            stage.setMaximized(true);
            
            // Initialize the controller with the profile manager after the scene is set
            Platform.runLater(() -> {
                try {
                    // Use reflection to set the profile manager
                    java.lang.reflect.Method setProfileManager = HelloController.class.getDeclaredMethod("setProfileManager", ProfileManager.class);
                    setProfileManager.setAccessible(true);
                    setProfileManager.invoke(controller, profileManager);
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error initializing application settings");
                }
            });
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error launching application");
        }
    }
}