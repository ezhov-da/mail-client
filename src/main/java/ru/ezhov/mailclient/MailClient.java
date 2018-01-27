package ru.ezhov.mailclient;

import javax.swing.*;
import java.io.Closeable;

/**
 * Интерфейс для взаимодействия с внешним миром почтового клиента
 * <p>
 *
 * @author ezhov_da
 */
public interface MailClient extends ListenerMailClient, Closeable {
    /**
     * показываем клиент
     */
    void showClient();

    JDialog getDialog();
}
