package ru.ezhov.mailclient;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Класс помощник для авторизации пользователя в почте
 * <p>
 *
 * @author ezhov_da
 */
public class DefaultAuthenticator extends Authenticator {
    private String user;
    private String password;

    public DefaultAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.user, this.password);
    }
}
