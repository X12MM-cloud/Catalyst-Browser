package com.zetax.zetabrowser;

public class TabInfo {
    private String name;
    private String url;

    // Default constructor for Jackson
    public TabInfo() {}

    public TabInfo(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
} 