package com.zetax.zetabrowser;

import java.awt.Dimension;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;

/**
 * A JavaFX component that displays the CEF browser content.
 * This class handles the rendering of the browser and user interaction.
 */
public class BrowserView extends Region {
    private Canvas canvas;
    private AnimationTimer renderTimer;
    private Dimension dimensions;
    private boolean initialized;
    private int fontSize = 16; // Default font size
    
    public BrowserView() {
        canvas = new Canvas();
        dimensions = new Dimension();
        getChildren().add(canvas);
        
        // Bind canvas size to parent size
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        
        // Handle size changes
        widthProperty().addListener((obs, oldVal, newVal) -> {
            if (initialized) {
                updateCanvasSize();
            }
        });
        heightProperty().addListener((obs, oldVal, newVal) -> {
            if (initialized) {
                updateCanvasSize();
            }
        });
        
        // Start the render loop
        renderTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateBrowserContent();
            }
        };
    }
    
    /**
     * Initialize the browser with the specified URL.
     * 
     * @param url The initial URL to load
     * @return true if initialization was successful
     */
    public boolean initialize(String url) {
        if (!NativeBridge.initialize()) {
            return false;
        }
        
        if (!NativeBridge.createBrowser(url)) {
            return false;
        }
        
        initialized = true;
        renderTimer.start();
        applyFontSize(); // Apply default font size
        return true;
    }
    
    /**
     * Load a new URL in the browser.
     * 
     * @param url The URL to load
     */
    public void loadURL(String url) {
        if (initialized) {
            NativeBridge.loadURL(url);
            // Re-apply font size when loading a new page
            applyFontSize();
        }
    }
    
    /**
     * Execute JavaScript code in the browser.
     * 
     * @param code The JavaScript code to execute
     */
    public void executeJavaScript(String code) {
        if (initialized) {
            NativeBridge.executeJavaScript(code);
        }
    }
    
    /**
     * Set the font size for the browser content.
     * 
     * @param size The font size in pixels
     */
    public void setFontSize(int size) {
        if (size >= 8 && size <= 24) { // Limit font size to reasonable range
            this.fontSize = size;
            applyFontSize();
        }
    }
    
    /**
     * Get the current font size.
     * 
     * @return The current font size in pixels
     */
    public int getFontSize() {
        return fontSize;
    }
    
    /**
     * Apply the current font size to the browser content.
     */
    private void applyFontSize() {
        if (initialized) {
            // Apply font size using JavaScript
            String js = "document.body.style.fontSize = '" + fontSize + "px'; " +
                       "document.querySelectorAll('*').forEach(function(el) { " +
                       "  if (el.tagName !== 'SCRIPT' && el.tagName !== 'STYLE') { " +
                       "    el.style.fontSize = '" + fontSize + "px'; " +
                       "  } " +
                       "});";
            executeJavaScript(js);
        }
    }
    
    /**
     * Clean up resources when the view is being destroyed.
     */
    public void dispose() {
        renderTimer.stop();
        if (initialized) {
            NativeBridge.shutdown();
            initialized = false;
        }
    }
    
    private void updateCanvasSize() {
        // Update the browser view size if needed
        // This would be handled by the native layer
    }
    
    private void updateBrowserContent() {
        if (!initialized) {
            return;
        }
        
        // Get the latest buffer from the native layer
        byte[] buffer = NativeBridge.getLatestBuffer(dimensions);
        if (buffer == null) {
            return;
        }
        
        // Convert the buffer to a JavaFX image
        WritableImage image = NativeBridge.createWritableImage(buffer, dimensions.width, dimensions.height);
        if (image == null) {
            return;
        }
        
        // Draw the image to the canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
    }
}