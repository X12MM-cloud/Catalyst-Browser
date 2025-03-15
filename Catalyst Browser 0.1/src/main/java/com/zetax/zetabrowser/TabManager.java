package com.zetax.zetabrowser;

import javafx.scene.web.WebView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.scene.paint.Color;

/**
 * Manages browser tabs and tab groups
 */
public class TabManager {
    private Map<String, WebView> tabWebViews = new HashMap<>();
    private Map<String, String> tabTitles = new HashMap<>();
    private Map<String, String> tabUrls = new HashMap<>();
    private List<TabGroup> tabGroups = new ArrayList<>();
    private Map<String, TabGroup> tabToGroupMap = new HashMap<>();

    /**
     * Adds a new tab
     * 
     * @param tabId Unique identifier for the tab
     * @param webView WebView associated with the tab
     * @param title Initial title for the tab
     * @param url Initial URL for the tab
     */
    public void addTab(String tabId, WebView webView, String title, String url) {
        tabWebViews.put(tabId, webView);
        tabTitles.put(tabId, title);
        tabUrls.put(tabId, url);
    }

    /**
     * Removes a tab
     * 
     * @param tabId Identifier of the tab to remove
     */
    public void removeTab(String tabId) {
        tabWebViews.remove(tabId);
        tabTitles.remove(tabId);
        tabUrls.remove(tabId);
        
        // Remove from group if it belongs to one
        TabGroup group = tabToGroupMap.get(tabId);
        if (group != null) {
            group.removeTab(tabId);
            tabToGroupMap.remove(tabId);
        }
    }

    /**
     * Updates a tab's title
     * 
     * @param tabId Identifier of the tab
     * @param title New title
     */
    public void updateTabTitle(String tabId, String title) {
        tabTitles.put(tabId, title);
    }

    /**
     * Updates a tab's URL
     * 
     * @param tabId Identifier of the tab
     * @param url New URL
     */
    public void updateTabUrl(String tabId, String url) {
        tabUrls.put(tabId, url);
    }

    /**
     * Gets the WebView for a tab
     * 
     * @param tabId Identifier of the tab
     * @return WebView associated with the tab
     */
    public WebView getTabWebView(String tabId) {
        return tabWebViews.get(tabId);
    }

    /**
     * Gets the title for a tab
     * 
     * @param tabId Identifier of the tab
     * @return Title of the tab
     */
    public String getTabTitle(String tabId) {
        return tabTitles.get(tabId);
    }

    /**
     * Gets the URL for a tab
     * 
     * @param tabId Identifier of the tab
     * @return URL of the tab
     */
    public String getTabUrl(String tabId) {
        return tabUrls.get(tabId);
    }

    /**
     * Searches tabs based on a query string
     * 
     * @param query Search query
     * @return List of tab IDs matching the query
     */
    public List<String> searchTabs(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(tabTitles.keySet());
        }
        
        String lowerQuery = query.toLowerCase();
        return tabTitles.entrySet().stream()
                .filter(entry -> entry.getValue().toLowerCase().contains(lowerQuery) || 
                                 tabUrls.getOrDefault(entry.getKey(), "").toLowerCase().contains(lowerQuery))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new tab group
     * 
     * @param name Group name
     * @param description Group description
     * @param color Group color
     * @return The created TabGroup
     */
    public TabGroup createGroup(String name, String description, Color color) {
        TabGroup group = new TabGroup(name, description, color);
        tabGroups.add(group);
        return group;
    }

    /**
     * Moves a tab to a group
     * 
     * @param tabId Identifier of the tab
     * @param group Target group
     */
    public void moveTabToGroup(String tabId, TabGroup group) {
        // Remove from current group if any
        TabGroup currentGroup = tabToGroupMap.get(tabId);
        if (currentGroup != null) {
            currentGroup.removeTab(tabId);
        }
        
        // Add to new group
        group.addTab(tabId);
        tabToGroupMap.put(tabId, group);
    }

    /**
     * Gets all tab groups
     * 
     * @return List of all tab groups
     */
    public List<TabGroup> getAllGroups() {
        return new ArrayList<>(tabGroups);
    }

    /**
     * Gets all tabs in a group
     * 
     * @param group The group to get tabs from
     * @return List of tab IDs in the group
     */
    public List<String> getTabsInGroup(TabGroup group) {
        return group.getTabIds();
    }
}