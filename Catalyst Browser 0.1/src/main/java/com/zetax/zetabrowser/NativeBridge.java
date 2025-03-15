package com.zetax.zetabrowser;

import java.awt.Dimension;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Bridge class for communicating with the native CEF implementation.
 * This class provides methods to control the embedded Chromium browser.
 */
public class NativeBridge {
    
    static {
        try {
            // Load the native library
            System.loadLibrary("zetabrowser_cef");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load native library: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize the CEF framework.
     * Must be called before any other methods.
     * 
     * @return true if initialization was successful
     */
    public static native boolean initialize();
    
    /**
     * Shutdown the CEF framework.
     * Should be called when the application is closing.
     */
    public static native void shutdown();
    
    /**
     * Create a browser instance with the specified URL.
     * 
     * @param url The initial URL to load
     * @return true if browser creation was successful
     */
    public static native boolean createBrowser(String url);
    
    /**
     * Load a URL in the browser.
     * 
     * @param url The URL to load
     */
    public static native void loadURL(String url);
    
    /**
     * Execute JavaScript code in the browser.
     * 
     * @param code The JavaScript code to execute
     */
    public static native void executeJavaScript(String code);
    
    /**
     * Get the latest rendered frame buffer from the browser.
     * 
     * @param dimensions Output parameter that will be filled with the width and height
     * @return Byte array containing the BGRA pixel data
     */
    public static native byte[] getLatestBuffer(Dimension dimensions);
    
    /**
     * Convert the native BGRA buffer to a JavaFX WritableImage.
     * 
     * @param buffer The BGRA pixel data
     * @param width The width of the image
     * @param height The height of the image
     * @return A JavaFX WritableImage containing the browser content
     */
    public static WritableImage createWritableImage(byte[] buffer, int width, int height) {
        if (buffer == null || width <= 0 || height <= 0) {
            return null;
        }
        
        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();
        
        // Convert BGRA to RGBA for JavaFX
        byte[] rgbaBuffer = new byte[buffer.length];
        for (int i = 0; i < buffer.length; i += 4) {
            // BGRA to RGBA conversion
            rgbaBuffer[i] = buffer[i + 2];     // R = B
            rgbaBuffer[i + 1] = buffer[i + 1]; // G = G
            rgbaBuffer[i + 2] = buffer[i];     // B = R
            rgbaBuffer[i + 3] = buffer[i + 3]; // A = A
        }
        
        pixelWriter.setPixels(0, 0, width, height, 
                PixelFormat.getByteBgraInstance(), 
                rgbaBuffer, 0, width * 4);
        
        return image;
    }
}