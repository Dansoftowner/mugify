package com.dansoftware.mugify.config;

import com.pixelduke.transit.Style;

import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class Preferences {
    private volatile Style theme = Style.DARK;
    private volatile boolean syncTheme;
    private volatile Locale locale = Locale.of("hu", "HU");
    private volatile List<String> recentFiles = new Vector<>();

    public Style getTheme() {
        return theme;
    }

    public void setTheme(Style theme) {
        this.theme = theme;
    }

    public boolean isSyncTheme() {
        return syncTheme;
    }

    public void setSyncTheme(boolean syncTheme) {
        this.syncTheme = syncTheme;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public List<String> getRecentFiles() {
        return recentFiles;
    }

    public void setRecentFiles(List<String> recentFiles) {
        this.recentFiles = recentFiles;
    }
}