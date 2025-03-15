package com.zetax.zetabrowser;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of tabs
 */
public class TabGroup {
    private String name;
    private String description;
    private Color color;
    private List<String> tabIds = new ArrayList<>();

    /**
     * Creates a new tab group
     * 
     * @param name Group name
     * @param description Group description
     * @param color Group color
     */
    public TabGroup(String name, String description, Color color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }

    /**
     * Gets the group name
     * 
     * @return Group name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the group name
     * 
     * @param name New group name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the group description
     * 
     * @return Group description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the group description
     * 
     * @param description New group description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the group color
     * 
     * @return Group color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the group color
     * 
     * @param color New group color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Adds a tab to the group
     * 
     * @param tabId Identifier of the tab to add
     */
    public void addTab(String tabId) {
        if (!tabIds.contains(tabId)) {
            tabIds.add(tabId);
        }
    }

    /**
     * Removes a tab from the group
     * 
     * @param tabId Identifier of the tab to remove
     */
    public void removeTab(String tabId) {
        tabIds.remove(tabId);
    }

    /**
     * Gets all tab IDs in this group
     * 
     * @return List of tab IDs
     */
    public List<String> getTabIds() {
        return new ArrayList<>(tabIds);
    }
}