module com.zetax.zetabrowser {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;  // Add this line to access java.awt package
    
    // Jackson dependencies
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    
    // Add any other required modules here
    
    exports com.zetax.zetabrowser;
    opens com.zetax.zetabrowser to javafx.fxml;
}