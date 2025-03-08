package com.zetax.zetabrowser;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ListView;
import javafx.util.Duration;
import java.util.function.Consumer;

/**
 * Utility class for browser UI components and animations
 */
public class BrowserUIUtils {
    
    /**
     * Sets up hover animations for buttons
     * 
     * @param buttons The buttons to apply animations to
     */
    public static void setupButtonAnimations(Button... buttons) {
        for (Button button : buttons) {
            if (button == null) continue;
            
            button.setOnMouseEntered(event -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
                scaleTransition.setToX(1.1);
                scaleTransition.setToY(1.1);
                scaleTransition.play();
            });
            
            button.setOnMouseExited(event -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
                scaleTransition.setToX(1.0);
                scaleTransition.setToY(1.0);
                scaleTransition.play();
            });
        }
    }
    
    /**
     * Creates a tab icon with the given emoji
     * 
     * @param emoji The emoji to use as the icon
     * @return A Label containing the emoji icon
     */
    public static Label createTabIcon(String emoji) {
        Label icon = new Label(emoji);
        icon.setStyle("-fx-font-size: 16px;");
        return icon;
    }
    
    /**
     * Creates a context menu for a ListView with pin and close options
     * 
     * @param listView The ListView to attach the context menu to
     * @param pinAction Action to perform when pin/unpin is selected
     * @param closeAction Action to perform when close is selected
     * @param isPinnedCheck Function to check if an item is pinned
     */
    public static void setupTabContextMenu(
            ListView<String> listView, 
            Consumer<String> pinAction, 
            Consumer<String> closeAction,
            java.util.function.Function<String, Boolean> isPinnedCheck) {
        
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem pinItem = new MenuItem("Pin Tab");
        pinItem.setOnAction(event -> {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                pinAction.accept(selectedItem);
            }
        });
        
        MenuItem closeItem = new MenuItem("Close Tab");
        closeItem.setOnAction(event -> {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                closeAction.accept(selectedItem);
            }
        });
        
        contextMenu.getItems().addAll(pinItem, closeItem);
        
        // Set the context menu on the ListView
        listView.setContextMenu(contextMenu);
        
        // Update pin/unpin menu item text based on selection
        listView.setOnContextMenuRequested(event -> {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                boolean isPinned = isPinnedCheck.apply(selectedItem);
                pinItem.setText(isPinned ? "Unpin Tab" : "Pin Tab");
            }
        });
    }
}