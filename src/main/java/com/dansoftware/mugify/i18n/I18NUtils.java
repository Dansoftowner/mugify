package com.dansoftware.mugify.i18n;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Locale;

public class I18NUtils {

    private static final I18N i18n = new I18N("com.dansoftware.mugify.i18n.Values", Locale.of("hu", "HU"));

    static {
        i18n.currentLocaleProperty().addListener(
                (_, _, locale) -> Locale.setDefault(locale));
    }

    public static List<Locale> getSupportedLocales() {
        return List.of(Locale.ENGLISH, Locale.of("hu", "HU"));
    }

    public static StringProperty val(String property) {
        return i18n.getProperty(property);
    }

    public static void setLocale(Locale locale) {
        i18n.setLocale(locale);
    }

    public static ObjectProperty<Locale> localeProperty() {
        return i18n.currentLocaleProperty();
    }

    private I18NUtils() {
    }
}
