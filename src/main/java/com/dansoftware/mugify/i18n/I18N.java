package com.dansoftware.mugify.i18n;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.*;

public class I18N {
    private final String baseName;
    private Locale currentLocale;
    private ResourceBundle currentBundle;
    private final Map<String, StringProperty> properties = new HashMap<>();

    public I18N(String baseName, Locale initialLocale) {
        this.baseName = baseName;
        this.currentLocale = initialLocale;
        this.currentBundle = ResourceBundle.getBundle(baseName, initialLocale);

        // Populate the properties map with StringProperty objects for each key
        Enumeration<String> keys = currentBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            properties.put(key, new SimpleStringProperty(currentBundle.getString(key)));
        }
    }

    public StringProperty getProperty(String key) {
        if (!properties.containsKey(key))
            properties.put(key, new SimpleStringProperty());
        return properties.get(key);
    }

    public void setLocale(Locale newLocale) {
        if (!newLocale.equals(currentLocale)) {
            this.currentLocale = newLocale;
            this.currentBundle = ResourceBundle.getBundle(baseName, newLocale);

            // Update all StringProperty values with the new bundle's values
            for (String key : properties.keySet()) {
                properties.get(key).set(currentBundle.getString(key));
            }
        }
    }
}