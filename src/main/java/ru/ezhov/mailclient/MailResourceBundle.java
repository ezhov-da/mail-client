package ru.ezhov.mailclient;

import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Хранитель настроек для приложения
 * <p>
 *
 * @author ezhov_da
 */
public class MailResourceBundle {
    private static final Logger LOG = Logger.getLogger(MailResourceBundle.class.getName());
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("res/config");

    private MailResourceBundle() {
    }

    public static String getString(String key) {
        return resourceBundle.getString(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(resourceBundle.getString(key));
    }

}
