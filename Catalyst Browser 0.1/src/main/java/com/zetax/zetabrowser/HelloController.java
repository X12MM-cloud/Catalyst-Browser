package com.zetax.zetabrowser;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

// This class serves as the controller for the main browser UI, managing user interactions and browser state.
public class HelloController {
    // URL input field at the top of the browser
    @FXML
    private TextField urlFieldTop;
    // URL input field at the side of the browser
    @FXML
    private TextField urlFieldSide;
    // List of open tabs
    @FXML
    private ListView<String> tabsList;
    // Container for the web content
    @FXML
    private StackPane browserContent;
    // WebView for rendering web pages
    @FXML
    private WebView webView;
    // Sidebar container
    @FXML
    private VBox sidebar;
    // Handle for resizing the sidebar
    @FXML
    private Rectangle resizeHandle;
    // Navigation buttons
    @FXML
    private Button navButtonBack;
    @FXML
    private Button navButtonForward;
    @FXML
    private Button navButtonRefresh;
    // Button to navigate to the entered URL
    @FXML
    private Button goButton;
    // Button to control tab actions
    @FXML
    private Button tabControl;
    // Button to hide the UI
    @FXML
    private Button hideUIButton;
    // Top bar of the browser
    @FXML
    private HBox topBar;
    // Application logo
    @FXML
    private ImageView appLogo;
    // Button to open settings
    @FXML
    private Button settingsButton;
    // Button to minimize the window
    @FXML
    private Button minimizeButton;
    // Button to maximize the window
    @FXML
    private Button maximizeButton;
    // Button to close the window
    @FXML
    private Button closeButton;

    // WebEngine for managing web page loading and execution
    private WebEngine webEngine;
    // Initial width of the sidebar for resizing
    private double sidebarInitialWidth;
    // Initial X position of the mouse for resizing
    private double mouseInitialX;
    // Map of tab IDs to their corresponding WebViews
    private Map<String, WebView> tabWebViews = new HashMap<>();
    // Map of tab IDs to their titles
    private Map<String, String> tabTitles = new HashMap<>();
    // ID of the currently active tab
    private String currentTabId;
    // Offsets for window dragging
    private double xOffset = 0;
    private double yOffset = 0;
    // Margin for detecting resize actions
    private static final int RESIZE_MARGIN = 5;

    // Manager for user profiles
    private ProfileManager profileManager;
    // Dropdown for selecting profiles
    private ComboBox<String> profileSelector;

    // Search field for tabs
    @FXML
    private TextField tabSearchField;
    // List of tab groups
    @FXML
    private ListView<String> groupsList;
    // Button to create a new group
    @FXML
    private Button createGroupButton;
    // Button to add a tab to a group
    @FXML
    private Button addToGroupButton;

    // Manager for handling tab operations
    private TabManager tabManager;
    // Container for pinned tabs
    @FXML
    private FlowPane pinnedTabsContainer;

    // Map of pinned tab IDs to their pinned status
    private Map<String, Boolean> pinnedTabs = new HashMap<>();

    // Button to trigger summarization
    @FXML
    private Button summarizeButton;
    // Toggle for search prediction feature
    @FXML
    private CheckBox searchPredictionToggle;
    // Label to display AI status
    @FXML
    private Label aiStatusLabel;

    // API key for external services (replace with actual key)
    private static final String YOUR_API_KEY = "sk-mnopqrstijkl5678mnopqrstijkl5678mnopqrst";

    // Initialize the controller and set up event listeners
    public void initialize() {
        tabManager = new TabManager();

        webEngine = webView.getEngine();
        webEngine.locationProperty().addListener((obs, oldVal, newVal) -> {
            urlFieldTop.setText(newVal);
            urlFieldSide.setText(newVal);

            if (currentTabId != null) {
                tabManager.updateTabUrl(currentTabId, newVal);
            }
        });

        onNewTabClick();

        // Handle tab selection
        tabsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                switchToTab(newVal);
            }
        });

        // Initialize pinned tabs container if it exists
        if (pinnedTabsContainer != null) {
            pinnedTabsContainer.setHgap(10);
            pinnedTabsContainer.setVgap(10);
            pinnedTabsContainer.setPadding(new Insets(10));
        }

        // Add context menu to tabs list
        setupTabsContextMenu();

        if (tabSearchField != null) {
            tabSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
                updateTabSearchResults(newVal);
            });
        }

        if (groupsList != null) {
            updateGroupsList();

            groupsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    showTabsInSelectedGroup();
                }
            });
        }

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), browserContent);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        setupButtonAnimations();

        Scene scene = browserContent.getScene();
        if (scene != null) {
            setupKeyHandlers(scene);
        }

        topBar.setOnMousePressed(event -> {
            Stage stage = (Stage) topBar.getScene().getWindow();
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        topBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) topBar.getScene().getWindow();
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

        if (scene != null) {
            scene.setOnMouseMoved(event -> {
                Stage stage = (Stage) browserContent.getScene().getWindow();
                double mouseX = event.getSceneX();
                double mouseY = event.getSceneY();
                double width = stage.getWidth();
                double height = stage.getHeight();

                if (mouseX < RESIZE_MARGIN || mouseX > width - RESIZE_MARGIN ||
                        mouseY < RESIZE_MARGIN || mouseY > height - RESIZE_MARGIN) {
                    scene.setCursor(javafx.scene.Cursor.SE_RESIZE);
                } else {
                    scene.setCursor(javafx.scene.Cursor.DEFAULT);
                }
            });

            scene.setOnMouseDragged(event -> {
                Stage stage = (Stage) browserContent.getScene().getWindow();
                double mouseX = event.getSceneX();
                double mouseY = event.getSceneY();

                if (scene.getCursor() == javafx.scene.Cursor.SE_RESIZE) {
                    stage.setWidth(mouseX);
                    stage.setHeight(mouseY);
                }
            });
        }

        urlFieldTop.setOnAction(event -> onGoButtonClick());

        webView.getStyleClass().add("web-view");
    }

    private void setupButtonAnimations() {
        // Apply hover animations to all buttons using the utility class
        BrowserUIUtils.setupButtonAnimations(
                navButtonBack, navButtonForward, navButtonRefresh,
                goButton, tabControl, settingsButton, hideUIButton
        );
    }

    private TextField getCurrentUrlField() {
        if (topBar.isVisible()) {
            return urlFieldTop;
        } else if (sidebar.isVisible()) {
            return urlFieldSide;
        }
        return null; // Return null if neither is visible
    }

    @FXML
    protected void onGoButtonClick() {
        String query = getCurrentUrlField().getText();
        if (isValidUrl(query)) {
            webEngine.load(query);
        } else {
            // Perform a Google search
            webEngine.load("https://www.google.com/search?q=" + query.replace(" ", "+"));
        }
    }

    @FXML
    protected void onNewTabClick() {
        String tabName = "New Tab";
        tabsList.getItems().add(tabName);

        // Create a new WebView for the tab
        WebView newWebView = new WebView();
        WebEngine newWebEngine = newWebView.getEngine();

        // Update tab name when page title changes
        newWebEngine.titleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                updateTabName(currentTabId, newVal);

                // Also update in TabManager
                tabManager.updateTabTitle(currentTabId, newVal);
            }
        });

        // Add to TabManager
        tabManager.addTab(tabName, newWebView, tabName, "");

        // Add to existing maps for backward compatibility
        tabWebViews.put(tabName, newWebView);
        tabTitles.put(tabName, tabName);

        // Switch to the new tab
        switchToTab(tabName);
    }

    @FXML
    protected void goBack() {
        if (webEngine.getHistory().getCurrentIndex() > 0) {
            webEngine.getHistory().go(-1);
        }
    }

    @FXML
    protected void goForward() {
        if (webEngine.getHistory().getCurrentIndex() < webEngine.getHistory().getEntries().size() - 1) {
            webEngine.getHistory().go(1);
        }
    }

    @FXML
    protected void refreshPage() {
        webEngine.reload();
    }

    @FXML
    protected void onResizeHandlePressed(MouseEvent event) {
        sidebarInitialWidth = sidebar.getWidth();
        mouseInitialX = event.getSceneX();
    }

    @FXML
    protected void onSidebarResize(MouseEvent event) {
        double deltaX = event.getSceneX() - mouseInitialX;
        double newWidth = sidebarInitialWidth + deltaX;
        if (newWidth > 100 && newWidth < 400) { // Min and max width
            sidebar.setPrefWidth(newWidth);
        }
    }

    @FXML
    private void toggleUIVisibility() {
        boolean isVisible = topBar.isVisible();
        topBar.setVisible(!isVisible);
        topBar.setManaged(!isVisible);
        sidebar.setVisible(!isVisible);
        sidebar.setManaged(!isVisible);
    }

    @FXML
    private void onHideUIButtonClick() {
        toggleUIVisibility();
    }

    private void toggleUIVisibilityWithAnimation() {
        boolean isVisible = topBar.isVisible() || sidebar.isVisible();

        TranslateTransition slideOutTopBar = new TranslateTransition(Duration.millis(500), topBar);
        slideOutTopBar.setFromY(0);
        slideOutTopBar.setToY(isVisible ? -topBar.getHeight() : 0);
        slideOutTopBar.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

        TranslateTransition slideOutSidebar = new TranslateTransition(Duration.millis(500), sidebar);
        slideOutSidebar.setFromX(0);
        slideOutSidebar.setToX(isVisible ? -sidebar.getWidth() : 0);
        slideOutSidebar.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

        TranslateTransition slideOutWebView = new TranslateTransition(Duration.millis(500), webView);
        slideOutWebView.setFromX(0);
        slideOutWebView.setFromY(0);
        slideOutWebView.setToX(isVisible ? -sidebar.getWidth() : 0);
        slideOutWebView.setToY(isVisible ? -topBar.getHeight() : 0);
        slideOutWebView.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

        slideOutTopBar.setOnFinished(event -> {
            topBar.setManaged(!isVisible);
            topBar.setVisible(!isVisible);
            sidebar.setManaged(!isVisible);
            sidebar.setVisible(!isVisible);

            TranslateTransition slideInTopBar = new TranslateTransition(Duration.millis(500), topBar);
            slideInTopBar.setFromY(isVisible ? topBar.getHeight() : -topBar.getHeight());
            slideInTopBar.setToY(0);
            slideInTopBar.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

            TranslateTransition slideInSidebar = new TranslateTransition(Duration.millis(500), sidebar);
            slideInSidebar.setFromX(isVisible ? sidebar.getWidth() : -sidebar.getWidth());
            slideInSidebar.setToX(0);
            slideInSidebar.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

            TranslateTransition slideInWebView = new TranslateTransition(Duration.millis(500), webView);
            slideInWebView.setFromX(isVisible ? sidebar.getWidth() : -sidebar.getWidth());
            slideInWebView.setFromY(isVisible ? topBar.getHeight() : -topBar.getHeight());
            slideInWebView.setToX(0);
            slideInWebView.setToY(0);
            slideInWebView.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

            slideInTopBar.play();
            slideInSidebar.play();
            slideInWebView.play();
        });

        slideOutTopBar.play();
        slideOutSidebar.play();
        slideOutWebView.play();

        // Update button text
        if (hideUIButton != null) {
            hideUIButton.setText(isVisible ? "Show UI" : "Hide UI");
        }
    }

    private void switchToTab(String tabId) {
        // If the tab is pinned but not in the list, restore it
        if (pinnedTabs.getOrDefault(tabId, false) && !tabsList.getItems().contains(tabId)) {
            tabsList.getItems().add(tabId);
        }

        currentTabId = tabId;
        WebView selectedWebView = tabWebViews.get(tabId);
        
        // If the WebView doesn't exist (shouldn't happen for pinned tabs, but just in case)
        if (selectedWebView == null) {
            selectedWebView = new WebView();
            WebEngine engine = selectedWebView.getEngine();
            
            // If it's a pinned tab, try to restore its URL
            if (pinnedTabs.getOrDefault(tabId, false) && 
                profileManager != null && 
                profileManager.getCurrentProfile() != null) {
                String savedUrl = profileManager.getCurrentProfile().getPinnedTabUrl(tabId);
                if (savedUrl != null && !savedUrl.isEmpty()) {
                    engine.load(savedUrl);
                }
            }
            
            tabWebViews.put(tabId, selectedWebView);
            tabManager.addTab(tabId, selectedWebView, tabId, engine.getLocation());
        }

        browserContent.getChildren().setAll(selectedWebView);
        webEngine = selectedWebView.getEngine();
        getCurrentUrlField().setText(webEngine.getLocation());

        // Update active state of pinned tab buttons
        if (pinnedTabsContainer != null) {
            for (Node node : pinnedTabsContainer.getChildren()) {
                if (node instanceof Button) {
                    node.getStyleClass().remove("active-pinned-tab");
                    if (("pinned-" + tabId).equals(node.getId())) {
                        node.getStyleClass().add("active-pinned-tab");
                    }
                }
            }
        }
    }

    private void updateTabName(String tabId, String newName) {
        int index = tabsList.getItems().indexOf(tabId);
        if (index != -1) {
            tabsList.getItems().set(index, newName);
            tabWebViews.put(newName, tabWebViews.remove(tabId));
            tabTitles.put(newName, tabTitles.remove(tabId));
            currentTabId = newName;
        }
    }

    private boolean isValidUrl(String input) {
        // Regular expression to validate URLs
        String urlRegex = "^(https?:\\/\\/)?" + // Optional protocol
                          "((([a-zA-Z\\d]([a-zA-Z\\d-]*[a-zA-Z\\d])*)\\.)+[a-zA-Z]{2,}|" + // Domain name
                          "localhost|" + // localhost
                          "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|" + // OR IPv4
                          "\\[([a-fA-F\\d:]+)\\])" + // OR IPv6
                          "(:\\d+)?(\\/[-a-zA-Z\\d%_.~+]*)*" + // Port and path
                          "(\\?[;&a-zA-Z\\d%_.~+=-]*)?" + // Query string
                          "(\\#[-a-zA-Z\\d_]*)?$"; // Fragment locator
        return input.matches(urlRegex);
    }

    public String getCurrentUrl() {
        return getCurrentUrlField().getText();
    }

    @FXML
    protected void openSettingsWindow() {
        Alert settingsAlert = new Alert(AlertType.INFORMATION);
        settingsAlert.setTitle("Catalyst Browser Settings");
        settingsAlert.setHeaderText(null);

        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.TOP);

        // General Tab
        GridPane generalContent = new GridPane();
        generalContent.setPadding(new Insets(20));
        generalContent.setHgap(10);
        generalContent.setVgap(10);
        generalContent.setStyle("-fx-text-fill: white;");

        TextField homepageField = new TextField();
        homepageField.setPromptText("Homepage URL");
        TextField searchEngineField = new TextField();
        searchEngineField.setPromptText("Default Search Engine");

        generalContent.add(new Label("Homepage:"), 0, 0);
        generalContent.add(homepageField, 1, 0);
        generalContent.add(new Label("Search Engine:"), 0, 1);
        generalContent.add(searchEngineField, 1, 1);
        Tab generalTab = new Tab("General", generalContent);

        // Privacy Tab
        GridPane privacyContent = new GridPane();
        privacyContent.setPadding(new Insets(20));
        privacyContent.setHgap(10);
        privacyContent.setVgap(10);
        privacyContent.setStyle("-fx-text-fill: white;");

        Button clearDataButton = new Button("Clear Browsing Data");
        CheckBox cookiesCheckBox = new CheckBox("Enable Cookies");
        CheckBox trackingProtectionCheckBox = new CheckBox("Enable Tracking Protection");

        privacyContent.add(clearDataButton, 0, 0);
        privacyContent.add(cookiesCheckBox, 0, 1);
        privacyContent.add(trackingProtectionCheckBox, 0, 2);
        Tab privacyTab = new Tab("Privacy", privacyContent);

        // Appearance Tab
        GridPane appearanceContent = new GridPane();
        appearanceContent.setPadding(new Insets(20));
        appearanceContent.setHgap(10);
        appearanceContent.setVgap(10);
        appearanceContent.setStyle("-fx-text-fill: white;");

        ComboBox<String> themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll("Light", "Dark");
        themeComboBox.setPromptText("Select Theme");
        themeComboBox.setOnAction(event -> switchTheme(themeComboBox.getValue()));
        TextField fontSizeField = new TextField();
        fontSizeField.setPromptText("Font Size");

        appearanceContent.add(new Label("Theme:"), 0, 0);
        appearanceContent.add(themeComboBox, 1, 0);
        appearanceContent.add(new Label("Font Size:"), 0, 1);
        appearanceContent.add(fontSizeField, 1, 1);
        Tab appearanceTab = new Tab("Appearance", appearanceContent);

        // Add tabs to TabPane
        tabPane.getTabs().addAll(generalTab, privacyTab, appearanceTab);

        settingsAlert.getDialogPane().setContent(tabPane);
        settingsAlert.getDialogPane().getStylesheets().add(getClass().getResource("/com/zetax/zetabrowser/style.css").toExternalForm());
        settingsAlert.getDialogPane().getStyleClass().add("dialog-pane");
        settingsAlert.showAndWait();
    }

    private void switchTheme(String theme) {
        Scene scene = browserContent.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            if ("Dark".equals(theme)) {
                scene.getStylesheets().add(getClass().getResource("/com/zetax/zetabrowser/style.css").toExternalForm());
            } else if ("Light".equals(theme)) {
                scene.getStylesheets().add(getClass().getResource("/com/zetax/zetabrowser/light-style.css").toExternalForm());
            }
        }
    }

    public void toggleSidebarVisibility() {
        boolean isVisible = sidebar.isVisible();
        sidebar.setManaged(!isVisible);
        sidebar.setVisible(!isVisible);
    }

    private void switchToSingleBarMode() {
        topBar.setVisible(false);
        topBar.setManaged(false);
        sidebar.setVisible(true);
        sidebar.setManaged(true);
        getCurrentUrlField().setVisible(true);
        goButton.setVisible(true);
        getCurrentUrlField().setText("");
    }

    private void switchToDualBarMode() {
        topBar.setVisible(true);
        topBar.setManaged(true);
        sidebar.setVisible(true);
        topBar.setManaged(true);
        getCurrentUrlField().setVisible(false);
        goButton.setVisible(false);
        getCurrentUrlField().setText("");
    }

    private void showUrlCopiedPopup() {
        Platform.runLater(() -> {
            Label popup = new Label("URL Copied");
            popup.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5px; -fx-border-radius: 5; -fx-background-radius: 5;");
            Pane root = (Pane) browserContent.getScene().getRoot();
            root.getChildren().add(popup);
            popup.setLayoutX(root.getWidth() - 100 - popup.getWidth());
            popup.setLayoutY(10);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), popup);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), popup);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> root.getChildren().remove(popup));
                fadeOut.play();
            });
            pause.play();
        });
    }

    private void toggleSidebarVisibilityWithAnimation() {
        boolean isVisible = sidebar.isVisible();
        TranslateTransition slideTransition = new TranslateTransition(Duration.millis(500), sidebar);
        slideTransition.setFromX(isVisible ? 0 : -sidebar.getWidth());
        slideTransition.setToX(isVisible ? -sidebar.getWidth() : 0);
        slideTransition.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
        slideTransition.setOnFinished(event -> {
            sidebar.setManaged(!isVisible);
            sidebar.setVisible(!isVisible);
        });
        slideTransition.play();
    }

    private void setupKeyHandlers(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            // Toggle UI with Ctrl+Shift+S
            if (event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.S) {
                toggleUIVisibility();
                event.consume();
            }

            // Copy URL with Ctrl+Shift+C
            if (event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.C) {
                String currentUrl = webEngine.getLocation();
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(currentUrl);
                clipboard.setContent(content);
                showUrlCopiedPopup();
                event.consume();
            }

            // Toggle sidebar with Ctrl+Shift+B
            if (event.isControlDown() && event.getCode() == KeyCode.T) {
                // Create new tab when Ctrl+Shift+B is pressed
                Platform.runLater(() -> {
                    onNewTabClick();
                });
                event.consume();
            }

            if (event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.B) {
                toggleSidebarVisibilityWithAnimation();
                event.consume();
            }
        });
    }

    public void setProfileManager(ProfileManager profileManager) {
        this.profileManager = profileManager;

        // Apply profile settings
        if (profileManager != null && profileManager.getCurrentProfile() != null) {
            Profile profile = profileManager.getCurrentProfile();
            if (profile.getHomepage() != null && !profile.getHomepage().isEmpty()) {
                webEngine.load(profile.getHomepage());
            }

            // Apply theme based on profile preference
            applyTheme(profile.isDarkMode());

            // Restore pinned tabs
            pinnedTabs.clear();
            pinnedTabsContainer.getChildren().clear();
            for (TabInfo tab : profile.getPinnedTabs()) {
                String tabId = tab.getName();
                String url = tab.getUrl();

                // Create a new tab if it doesn't exist
                if (!tabWebViews.containsKey(tabId)) {
                    WebView newWebView = new WebView();
                    WebEngine newWebEngine = newWebView.getEngine();
                    
                    // Load the saved URL
                    if (url != null && !url.isEmpty()) {
                        newWebEngine.load(url);
                    }

                    tabWebViews.put(tabId, newWebView);
                    tabTitles.put(tabId, tabId);
                    tabsList.getItems().add(tabId);
                    tabManager.addTab(tabId, newWebView, tabId, url);
                }
                // Pin the tab
                pinnedTabs.put(tabId, true);
                addPinnedTabToUI(tabId);
            }
        }

        // Re-setup key handlers to ensure they work
        Platform.runLater(() -> {
            Scene scene = browserContent.getScene();
            if (scene != null) {
                setupKeyHandlers(scene);
            }
        });
    }

    // Move these methods inside the class body
    @FXML
    private void showSettingsMenu() {
        Alert settingsAlert = new Alert(AlertType.NONE);
        settingsAlert.setTitle("Catalyst Browser Settings");
        settingsAlert.setHeaderText(null);
        settingsAlert.getButtonTypes().add(ButtonType.CLOSE);

        // Create a stylish dialog
        settingsAlert.getDialogPane().setPrefWidth(500);
        settingsAlert.getDialogPane().setPrefHeight(400);

        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.LEFT);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // General Tab
        GridPane generalContent = new GridPane();
        generalContent.setPadding(new Insets(20));
        generalContent.setHgap(15);
        generalContent.setVgap(15);
        generalContent.setStyle("-fx-text-fill: white;");

        TextField homepageField = new TextField();
        homepageField.setPromptText("Homepage URL");
        if (profileManager != null && profileManager.getCurrentProfile() != null) {
            homepageField.setText(profileManager.getCurrentProfile().getHomepage());
        }

        TextField searchEngineField = new TextField();
        searchEngineField.setPromptText("Default Search Engine");
        searchEngineField.setText("https://www.google.com/search?q=");

        generalContent.add(new Label("Homepage:"), 0, 0);
        generalContent.add(homepageField, 1, 0);
        generalContent.add(new Label("Search Engine:"), 0, 1);
        generalContent.add(searchEngineField, 1, 1);

        Button saveGeneralButton = new Button("Save Changes");
        saveGeneralButton.getStyleClass().add("settings-button");
        saveGeneralButton.setOnAction(e -> {
            if (profileManager != null && profileManager.getCurrentProfile() != null) {
                profileManager.getCurrentProfile().setHomepage(homepageField.getText());
                // Save search engine preference
            }
        });
        generalContent.add(saveGeneralButton, 1, 3);

        Tab generalTab = new Tab("General", generalContent);
        generalTab.setGraphic(createTabIcon("⚙️"));

        // Privacy Tab
        GridPane privacyContent = new GridPane();
        privacyContent.setPadding(new Insets(20));
        privacyContent.setHgap(15);
        privacyContent.setVgap(15);
        privacyContent.setStyle("-fx-text-fill: white;");

        Button clearDataButton = new Button("Clear Browsing Data");
        clearDataButton.getStyleClass().add("settings-button");
        clearDataButton.setOnAction(e -> {
            webEngine.executeScript("localStorage.clear(); sessionStorage.clear();");
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Data Cleared");
            alert.setHeaderText(null);
            alert.setContentText("Browsing data has been cleared successfully.");
            alert.showAndWait();
        });

        CheckBox cookiesCheckBox = new CheckBox("Enable Cookies");
        cookiesCheckBox.setSelected(true);

        CheckBox trackingProtectionCheckBox = new CheckBox("Enable Tracking Protection");
        trackingProtectionCheckBox.setSelected(true);

        privacyContent.add(clearDataButton, 0, 0);
        privacyContent.add(cookiesCheckBox, 0, 1);
        privacyContent.add(trackingProtectionCheckBox, 0, 2);

        Tab privacyTab = new Tab("Privacy", privacyContent);
        privacyTab.setGraphic(createTabIcon("🔒"));

        // Appearance Tab
        GridPane appearanceContent = new GridPane();
        appearanceContent.setPadding(new Insets(20));
        appearanceContent.setHgap(15);
        appearanceContent.setVgap(15);
        appearanceContent.setStyle("-fx-text-fill: white;");

        ComboBox<String> themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll("Light", "Dark");
        themeComboBox.setPromptText("Select Theme");

        // Set current theme
        if (profileManager != null && profileManager.getCurrentProfile() != null) {
            themeComboBox.setValue(profileManager.getCurrentProfile().isDarkMode() ? "Dark" : "Light");
        } else {
            themeComboBox.setValue("Dark"); // Default
        }

        // In the themeComboBox.setOnAction method
        themeComboBox.setOnAction(event -> {
            String selectedTheme = themeComboBox.getValue();
            boolean isDarkMode = "Dark".equals(selectedTheme);

            if (profileManager != null && profileManager.getCurrentProfile() != null) {
                Profile currentProfile = profileManager.getCurrentProfile();
                currentProfile.setDarkMode(isDarkMode);

                // Apply theme to all windows using the static method
                for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                    if (window instanceof javafx.stage.Stage) {
                        Scene scene = ((javafx.stage.Stage) window).getScene();
                        if (scene != null) {
                            HelloApplication.applyTheme(scene, isDarkMode);
                        }
                    }
                }

                // Save the profile changes to JSON file
                profileManager.saveProfiles();
            }
        });

        Slider fontSizeSlider = new Slider(8, 20, 12);
        fontSizeSlider.setShowTickLabels(true);
        fontSizeSlider.setShowTickMarks(true);
        fontSizeSlider.setMajorTickUnit(2);
        fontSizeSlider.setMinorTickCount(1);
        fontSizeSlider.setBlockIncrement(1);

        Label fontSizeLabel = new Label("12px");

        // When initializing the slider, set it to the current profile's font size
        if (profileManager != null && profileManager.getCurrentProfile() != null) {
            int savedFontSize = profileManager.getCurrentProfile().getFontSize();
            if (savedFontSize > 0) {
                fontSizeSlider.setValue(savedFontSize);
                fontSizeLabel.setText(savedFontSize + "px");
            }
        }

        // In the settings dialog setup, add this code for the font size slider:
        fontSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int fontSize = newVal.intValue();
            // Update the label to show current size
            fontSizeLabel.setText(fontSize + "px");

            // Apply font size to the browser
            applyFontSize(fontSize);

            // Save to profile if available
            if (profileManager != null && profileManager.getCurrentProfile() != null) {
                profileManager.getCurrentProfile().setFontSize(fontSize);
                profileManager.saveProfiles();
            }
        });

        appearanceContent.add(new Label("Theme:"), 0, 0);
        appearanceContent.add(themeComboBox, 1, 0);
        appearanceContent.add(new Label("Font Size:"), 0, 1);
        appearanceContent.add(fontSizeSlider, 1, 1);
        appearanceContent.add(fontSizeLabel, 2, 1);

        Tab appearanceTab = new Tab("Appearance", appearanceContent);
        appearanceTab.setGraphic(createTabIcon("🎨"));

        // Add tabs to TabPane
        tabPane.getTabs().addAll(generalTab, privacyTab, appearanceTab);

        settingsAlert.getDialogPane().setContent(tabPane);
        settingsAlert.getDialogPane().getStylesheets().add(getClass().getResource("/com/zetax/zetabrowser/style.css").toExternalForm());
        settingsAlert.getDialogPane().getStyleClass().add("settings-dialog");

        // Center the dialog on the screen
        Stage stage = (Stage) settingsAlert.getDialogPane().getScene().getWindow();
        stage.setOnShown(e -> {
            Stage parentStage = (Stage) browserContent.getScene().getWindow();
            stage.setX(parentStage.getX() + (parentStage.getWidth() - stage.getWidth()) / 2);
            stage.setY(parentStage.getY() + (parentStage.getHeight() - stage.getHeight()) / 2);
        });

        settingsAlert.showAndWait();
    }

    private void applyTheme(boolean darkMode) {
        Scene scene = browserContent.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            if (darkMode) {
                scene.getStylesheets().add(getClass().getResource("/com/zetax/zetabrowser/style.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/com/zetax/zetabrowser/light-style.css").toExternalForm());
            }

            // Update any UI elements that need theme-specific adjustments
            if (profileManager != null && profileManager.getCurrentProfile() != null) {
                profileManager.getCurrentProfile().setDarkMode(darkMode);
            }
        }
    }

    // Helper method to create tab icons
    private Label createTabIcon(String emoji) {
        return BrowserUIUtils.createTabIcon(emoji);
    }

    // Add this method before the updateTabSearchResults method
    private void applyFontSize(int fontSize) {
        // Apply font size to the current WebView
        webEngine.executeScript(
                "document.body.style.fontSize = '" + fontSize + "px';" +
                        "document.querySelectorAll('*').forEach(function(el) {" +
                        "  if (el.style.fontSize) {" +
                        "    el.style.fontSize = '" + fontSize + "px';" +
                        "  }" +
                        "});"
        );

        // Apply to all tabs
        for (WebView view : tabWebViews.values()) {
            WebEngine engine = view.getEngine();
            engine.executeScript(
                    "document.body.style.fontSize = '" + fontSize + "px';" +
                            "document.querySelectorAll('*').forEach(function(el) {" +
                            "  if (el.style.fontSize) {" +
                            "    el.style.fontSize = '" + fontSize + "px';" +
                            "  }" +
                            "});"
            );
        }
    }

    // Add these methods after the applyFontSize method at the end of the class

    private void updateTabSearchResults(String query) {
        if (tabManager != null) {
            List<String> results = tabManager.searchTabs(query);

            // Update the tabs list with search results
            tabsList.getItems().clear();
            tabsList.getItems().addAll(results);

            // If there are results and no tab is selected, select the first one
            if (!results.isEmpty() && tabsList.getSelectionModel().getSelectedItem() == null) {
                tabsList.getSelectionModel().select(0);
            }
        }
    }

    private void updateGroupsList() {
        if (groupsList != null && tabManager != null) {
            groupsList.getItems().clear();

            for (TabGroup group : tabManager.getAllGroups()) {
                groupsList.getItems().add(group.getName() + " (" + group.getTabIds().size() + " tabs)");
            }
        }
    }

    private void showTabsInSelectedGroup() {
        if (groupsList != null && tabManager != null) {
            String selectedGroupName = groupsList.getSelectionModel().getSelectedItem();
            if (selectedGroupName != null) {
                // Extract group name from the toString format "Name (X tabs)"
                String groupName = selectedGroupName.split(" \\(")[0];

                // Find the group
                TabGroup selectedGroup = null;
                for (TabGroup group : tabManager.getAllGroups()) {
                    if (group.getName().equals(groupName)) {
                        selectedGroup = group;
                        break;
                    }
                }

                if (selectedGroup != null) {
                    // Show only tabs in this group
                    List<String> tabsInGroup = tabManager.getTabsInGroup(selectedGroup);
                    tabsList.getItems().clear();
                    tabsList.getItems().addAll(tabsInGroup);
                }
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handles the create group button click event
     */
    @FXML
    private void onCreateGroupClick() {
        // Create a dialog to get group name
        TextInputDialog dialog = new TextInputDialog("New Group");
        dialog.setTitle("Create Tab Group");
        dialog.setHeaderText("Create a new tab group");
        dialog.setContentText("Enter group name:");

        // Apply CSS to the dialog
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/zetax/zetabrowser/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        // Show the dialog and process the result
        dialog.showAndWait().ifPresent(groupName -> {
            if (!groupName.trim().isEmpty() && tabManager != null) {
                // Create a new group with a random color
                javafx.scene.paint.Color randomColor = javafx.scene.paint.Color.rgb(
                        (int) (Math.random() * 255),
                        (int) (Math.random() * 255),
                        (int) (Math.random() * 255)
                );

                TabGroup newGroup = tabManager.createGroup(groupName, "User created group", randomColor);

                // Add current tab to the group if one is selected
                String selectedTab = tabsList.getSelectionModel().getSelectedItem();
                if (selectedTab != null) {
                    tabManager.moveTabToGroup(selectedTab, newGroup);
                }

                // Update the groups list
                updateGroupsList();

                // Show confirmation
                showAlert("Group Created", "Tab group '" + groupName + "' has been created successfully.");
            }
        });
    }

    @FXML
    private void onAddToGroupClick() {
        String selectedTab = tabsList.getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            showAlert("Error", "Please select a tab first.");
            return;
        }

        Dialog<TabGroup> dialog = new Dialog<>();
        dialog.setTitle("Add to Group");
        dialog.setHeaderText("Select a group to add the tab to");

        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

        ListView<String> groupsListView = new ListView<>();
        for (TabGroup group : tabManager.getAllGroups()) {
            groupsListView.getItems().add(group.getName());
        }

        dialog.getDialogPane().setContent(groupsListView);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/zetax/zetabrowser/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == selectButtonType) {
                String selectedGroupName = groupsListView.getSelectionModel().getSelectedItem();
                if (selectedGroupName != null) {
                    for (TabGroup group : tabManager.getAllGroups()) {
                        if (group.getName().equals(selectedGroupName)) {
                            return group;
                        }
                    }
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(group -> {
            tabManager.moveTabToGroup(selectedTab, group);
            updateGroupsList();
            showAlert("Tab Added", "Tab '" + selectedTab + "' has been added to group '" + group.getName() + "'.");
        });
    }

    private void setupTabsContextMenu() {
        // Use the utility method from BrowserUIUtils to set up the context menu
        BrowserUIUtils.setupTabContextMenu(
                tabsList,
                this::togglePinTab,
                this::closeTab,
                tabId -> pinnedTabs.getOrDefault(tabId, false)
        );
    }

    private void togglePinTab(String tabId) {
        boolean isPinned = pinnedTabs.getOrDefault(tabId, false);

        if (isPinned) {
            // Unpin the tab
            pinnedTabs.put(tabId, false);
            removePinnedTabFromUI(tabId);
            if (profileManager != null && profileManager.getCurrentProfile() != null) {
                profileManager.getCurrentProfile().removePinnedTab(tabId);
                profileManager.updateCurrentProfile();
            }
        } else {
            // Pin the tab
            pinnedTabs.put(tabId, true);
            addPinnedTabToUI(tabId);
            if (profileManager != null && profileManager.getCurrentProfile() != null) {
                // Save tab with its URL
                String url = tabWebViews.get(tabId).getEngine().getLocation();
                profileManager.getCurrentProfile().addPinnedTab(tabId, url);
                profileManager.updateCurrentProfile();
            }
        }
    }

    private void addPinnedTabToUI(String tabId) {
        if (pinnedTabsContainer == null) return;

        // Create a button for the pinned tab
        Button pinnedTabButton = new Button();
        pinnedTabButton.getStyleClass().addAll("pinned-tab-button");
        if (tabId.equals(currentTabId)) {
            pinnedTabButton.getStyleClass().add("active-pinned-tab");
        }
        pinnedTabButton.setId("pinned-" + tabId);

        // Set the favicon or a default icon
        String favicon = getFaviconForTab(tabId);
        if (favicon != null && !favicon.isEmpty()) {
            pinnedTabButton.setText(favicon);
        } else {
            // Extract first letter of domain as fallback
            String url = tabManager.getTabUrl(tabId);
            if (url != null && !url.isEmpty()) {
                try {
                    java.net.URL urlObj = new java.net.URL(url);
                    String host = urlObj.getHost();
                    if (host.startsWith("www.")) {
                        host = host.substring(4);
                    }
                    pinnedTabButton.setText(host.substring(0, 1).toUpperCase());
                } catch (Exception e) {
                    pinnedTabButton.setText(tabId.substring(0, 1));
                }
            } else {
                pinnedTabButton.setText(tabId.substring(0, 1));
            }
        }

        // Set tooltip with tab title
        pinnedTabButton.setTooltip(new Tooltip(tabId));

        // Set action to switch to this tab
        pinnedTabButton.setOnAction(event -> {
            // If the tab doesn't exist in the list, add it back
            if (!tabsList.getItems().contains(tabId)) {
                WebView webView = tabWebViews.get(tabId);
                if (webView == null) {
                    // Create new WebView if it doesn't exist
                    webView = new WebView();
                    WebEngine engine = webView.getEngine();
                    String url = profileManager.getCurrentProfile().getPinnedTabUrl(tabId);
                    if (url != null && !url.isEmpty()) {
                        engine.load(url);
                    }
                    tabWebViews.put(tabId, webView);
                    tabManager.addTab(tabId, webView, tabId, url);
                }
                tabsList.getItems().add(tabId);
            }
            switchToTab(tabId);
            
            // Update active state of pinned tab buttons
            for (Node node : pinnedTabsContainer.getChildren()) {
                if (node instanceof Button) {
                    node.getStyleClass().remove("active-pinned-tab");
                }
            }
            pinnedTabButton.getStyleClass().add("active-pinned-tab");
        });

        // Add context menu to pinned tab button
        ContextMenu pinnedTabMenu = new ContextMenu();
        MenuItem unpinItem = new MenuItem("Unpin Tab");
        unpinItem.setOnAction(event -> togglePinTab(tabId));
        MenuItem closeItem = new MenuItem("Close Tab");
        closeItem.setOnAction(event -> closeTab(tabId));
        pinnedTabMenu.getItems().addAll(unpinItem, closeItem);
        pinnedTabButton.setContextMenu(pinnedTabMenu);

        // Add to the container
        pinnedTabsContainer.getChildren().add(pinnedTabButton);
    }

    private void removePinnedTabFromUI(String tabId) {
        if (pinnedTabsContainer == null) return;

        // Find and remove the pinned tab button
        pinnedTabsContainer.getChildren().removeIf(node ->
                node instanceof Button &&
                        ("pinned-" + tabId).equals(node.getId())
        );
    }

    private String getFaviconForTab(String tabId) {
        // Get the URL for this tab
        String url = tabManager.getTabUrl(tabId);
        if (url == null || url.isEmpty()) return null;

        // Map common domains to emoji icons
        try {
            java.net.URL urlObj = new java.net.URL(url);
            String host = urlObj.getHost().toLowerCase();

            if (host.contains("google")) return "🔍";
            if (host.contains("facebook") || host.contains("fb.com")) return "📘";
            if (host.contains("reddit")) return "🔶";
            if (host.contains("youtube")) return "▶️";
            if (host.contains("twitter") || host.contains("x.com")) return "🐦";
            if (host.contains("instagram")) return "📷";
            if (host.contains("linkedin")) return "💼";
            if (host.contains("github")) return "🐱";
            if (host.contains("spotify")) return "🎵";

            // Default: return null to use first letter of domain
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void closeTab(String tabId) {
        boolean isPinned = pinnedTabs.getOrDefault(tabId, false);
        
        // If it's a pinned tab, just remove it from the tab list but keep the WebView
        if (isPinned) {
            tabsList.getItems().remove(tabId);
            // Don't remove the WebView or other data structures for pinned tabs
            if (tabId.equals(currentTabId)) {
                // Switch to another tab if available, otherwise create new tab
                if (!tabsList.getItems().isEmpty()) {
                    switchToTab(tabsList.getItems().get(0));
                } else {
                    onNewTabClick();
                }
            }
        } else {
            // For non-pinned tabs, remove completely
            if (pinnedTabs.getOrDefault(tabId, false)) {
                removePinnedTabFromUI(tabId);
                pinnedTabs.remove(tabId);
                if (profileManager != null && profileManager.getCurrentProfile() != null) {
                    profileManager.getCurrentProfile().removePinnedTab(tabId);
                    profileManager.updateCurrentProfile();
                }
            }

            tabsList.getItems().remove(tabId);
            tabManager.removeTab(tabId);
            tabWebViews.remove(tabId);
            tabTitles.remove(tabId);

            if (tabId.equals(currentTabId)) {
                if (!tabsList.getItems().isEmpty()) {
                    switchToTab(tabsList.getItems().get(0));
                } else {
                    onNewTabClick();
                }
            }
        }
    }

    @FXML
    private void onSummarizeClick(ActionEvent event) {
        // Get current page content
        String pageContent = webView.getEngine().executeScript("document.body.innerText").toString();

        // Create a WebView to execute JavaScript for summarization
        WebView jsWebView = new WebView();
        WebEngine jsEngine = jsWebView.getEngine();

        // Load a blank page
        jsEngine.loadContent("<html><body></body></html>");

        // JavaScript function to summarize text
        String script = "function summarize(text) { return text.substring(0, 100); } summarize('" + pageContent + "');";

        // Execute JavaScript
        Object summary = jsEngine.executeScript(script);

        // Show the summary in a popup
        showSummaryPopup(summary.toString());
    }

    private void showSummaryPopup(String summary) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Page Summary");
        alert.setHeaderText("AI-Generated Summary");
        alert.setContentText(summary);
        alert.showAndWait();
    }

    @FXML
    private void onSearchPredictionToggle(ActionEvent event) {
        if (searchPredictionToggle.isSelected()) {
            // Enable search prediction
            urlFieldTop.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) {
                    // Use the optimized string matching functions
                    String prediction = predictSearch(newVal);
                    if (!prediction.isEmpty()) {
                        urlFieldTop.setPromptText("Search prediction: " + prediction);
                    }
                }
            });
        } else {
            // Disable search prediction
            urlFieldTop.setPromptText("Enter URL or search...");
        }
    }

    private String predictSearch(String input) {
        // Use the optimized string matching functions
        // ... implementation using the assembly optimized functions ...
        return "predicted search term";
    }
}
    
    // ...