package com.zetax.zetabrowser;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HelloApplication extends Application {
    private static ProfileManager profileManager;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialize the profile manager
        profileManager = new ProfileManager();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("profile-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 500);
        
        // Apply theme based on current profile if available
        if (profileManager.getCurrentProfile() != null) {
            applyTheme(scene, profileManager.getCurrentProfile().isDarkMode());
        } else {
            // Default to dark theme if no profile is selected
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        }
        
        primaryStage.setTitle("Zeta Browser - Profile Selection");
        primaryStage.setScene(scene);
        
        // Set the application icon
        Image icon = new Image(getClass().getResourceAsStream("logo.png"));
        primaryStage.getIcons().add(icon);
        
        primaryStage.show();
    }
    
    // Helper method to apply theme to a scene
    public static void applyTheme(Scene scene, boolean darkMode) {
        scene.getStylesheets().clear();
        if (darkMode) {
            scene.getStylesheets().add(HelloApplication.class.getResource("style.css").toExternalForm());
        } else {
            scene.getStylesheets().add(HelloApplication.class.getResource("light-style.css").toExternalForm());
        }
    }
    
    // Getter for profile manager to be used by other classes
    public static ProfileManager getProfileManager() {
        return profileManager;
    }
    
    public static void main(String[] args) {
        launch();
    }
}