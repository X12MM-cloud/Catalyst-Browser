package com.zetax.zetabrowser;

import java.util.ArrayList;
import java.util.List;

public class Profile {
    private String username;
    private String theme;
    private String homepage;
    private String searchEngine;
    private boolean cookiesEnabled;
    private boolean trackingProtection;
    private int fontSize;
    private List<String> bookmarks;
    private List<TabInfo> pinnedTabs;

    public Profile() {
        this.bookmarks = new ArrayList<>();
        this.pinnedTabs = new ArrayList<>();
    }

    public Profile(String username) {
        this();
        this.username = username;
        this.theme = "Dark";
        this.homepage = "https://www.google.com";
        this.searchEngine = "Google";
        this.cookiesEnabled = true;
        this.trackingProtection = true;
        this.fontSize = 13;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    
    // Add these methods for dark mode functionality
    public boolean isDarkMode() { return "Dark".equals(theme); }
    public void setDarkMode(boolean darkMode) { this.theme = darkMode ? "Dark" : "Light"; }
    
    public String getHomepage() { return homepage; }
    public void setHomepage(String homepage) { this.homepage = homepage; }
    
    public String getSearchEngine() { return searchEngine; }
    public void setSearchEngine(String searchEngine) { this.searchEngine = searchEngine; }
    
    public boolean isCookiesEnabled() { return cookiesEnabled; }
    public void setCookiesEnabled(boolean cookiesEnabled) { this.cookiesEnabled = cookiesEnabled; }
    
    public boolean isTrackingProtection() { return trackingProtection; }
    public void setTrackingProtection(boolean trackingProtection) { this.trackingProtection = trackingProtection; }
    
    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }
    
    public List<String> getBookmarks() { return bookmarks; }
    public void setBookmarks(List<String> bookmarks) { this.bookmarks = bookmarks; }

    public List<TabInfo> getPinnedTabs() { return pinnedTabs; }
    public void setPinnedTabs(List<TabInfo> pinnedTabs) { this.pinnedTabs = pinnedTabs; }
    
    public void addPinnedTab(String name, String url) {
        TabInfo tab = new TabInfo(name, url);
        if (!isPinnedTab(name)) {
            pinnedTabs.add(tab);
        }
    }
    
    public void removePinnedTab(String name) {
        pinnedTabs.removeIf(tab -> tab.getName().equals(name));
    }
    
    public boolean isPinnedTab(String name) {
        return pinnedTabs.stream().anyMatch(tab -> tab.getName().equals(name));
    }
    
    public String getPinnedTabUrl(String name) {
        return pinnedTabs.stream()
            .filter(tab -> tab.getName().equals(name))
            .map(TabInfo::getUrl)
            .findFirst()
            .orElse(null);
    }
}