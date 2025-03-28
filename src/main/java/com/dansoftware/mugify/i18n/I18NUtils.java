package com.dansoftware.mugify.i18n;

import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Locale;

public class I18NUtils {

    private static final I18N i18n = new I18N("com.dansoftware.mugify.i18n.Values", Locale.getDefault());

    public static List<Locale> getSupportedLocales() {
        return List.of(Locale.ENGLISH, Locale.of("hu", "HU"));
    }

    public static StringProperty val(String property) {
        return i18n.getProperty(property);
    }

    public static void setLocale(Locale locale) {
        Locale.setDefault(locale);
        i18n.setLocale(locale);
    }

    private I18NUtils() {
    }
}
