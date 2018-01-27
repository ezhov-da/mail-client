package ru.ezhov.mailclient;

import javax.mail.MessagingException;
import javax.swing.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Реализация клиента по умолчанию, именно через нее происходит взаимодействие
 * <p>
 *
 * @author ezhov_da
 */
public class DefaultMailClient implements MailClient {
    private static final Logger LOG = Logger.getLogger(DefaultMailClient.class.getName());
    private InitMailClientObject initMailClientObject;
    private MailClientForm clientForm;

    public DefaultMailClient(InitMailClientObject initMailClientObject) throws MessagingException {
        this.initMailClientObject = initMailClientObject;
        clientForm = new MailClientForm(initMailClientObject);
        clientForm.setSize(
                MailResourceBundle.getInt("size.form.width"),
                MailResourceBundle.getInt("size.form.height"));
    }

    @Override
    public void showClient() {
        SwingUtilities.invokeLater(() ->
        {
            clientForm.setVisible(true);
        });
    }

    @Override
    public void addListenerAttachment(ListenerAttachment listenerAttachment) {
        clientForm.addListenerAttachment(listenerAttachment);
    }

    @Override
    public void removeListenerAttachment(ListenerAttachment listenerAttachment) {
        clientForm.removeListenerAttachment(listenerAttachment);
    }

    @Override
    public JDialog getDialog() {
        return clientForm;
    }

    @Override
    public void close() throws IOException {
        clientForm.close();
    }
}
