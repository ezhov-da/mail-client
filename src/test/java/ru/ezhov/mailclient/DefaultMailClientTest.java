package ru.ezhov.mailclient;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.swing.*;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ezhov_da
 */
public class DefaultMailClientTest {

    public static void main(String[] args) {
        DefaultMailClientTest defaultMailClientTest = new DefaultMailClientTest();
        //defaultMailClientTest.thAuth();
        //defaultMailClientTest.ouAuth();
    }

    private void thAuth() {
        Properties props = System.getProperties();
        props.put("mail.host", "1");
        props.put("mail.auth", "1");
        props.put("mail.mime.charset", "UTF-8");
        props.put("mail.store.protocol", "imap");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.mime.ignoreunknownencoding", "true");
        Authenticator auth = new DefaultAuthenticator("1", "1");
        InitMailClientObject initMailClientObject = new InitMailClientObject(props, auth);
        buildDialog(initMailClientObject);
    }

    private void ouAuth() {
        try {
            Properties props = System.getProperties();
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            props.put("mail.imap.starttls.enable", "true");
            props.put("mail.imap.ssl.socketFactory", sf);
            //
            props.put("mail.host", "1");
            props.put("mail.imap.starttls.enable", "true");
            props.put("mail.auth", "true");
            props.put("mail.mime.charset", "UTF-8");
            props.put("mail.store.protocol", "imap");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.mime.ignoreunknownencoding", "true");
            Authenticator auth = new DefaultAuthenticator("1", "1");
            InitMailClientObject initMailClientObject = new InitMailClientObject(props, auth);
            buildDialog(initMailClientObject);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(DefaultMailClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void buildDialog(InitMailClientObject initMailClientObject) {
        try {
            JLabel labelInstructions;
            JLabel labelWhy;
            labelInstructions = new JLabel("<html><a href=\"http://office6887:8080/web_otz/\">Инструкция</a>");
            labelWhy = new JLabel("<html><a href=\"http://office6887:8080/web_otz/\">Почему я не вижу писем?</a>");
            initMailClientObject.add(labelInstructions);
            initMailClientObject.add(labelWhy);
            MailClient mailClientImpl = new DefaultMailClient(initMailClientObject);
            mailClientImpl.addListenerAttachment((List<FileMessageSave> fileMessageSaves) -> System.out.println(fileMessageSaves));
            mailClientImpl.getDialog().setLocationRelativeTo(null);
            mailClientImpl.getDialog().setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            mailClientImpl.getDialog().setModal(true);
            mailClientImpl.showClient();
        } catch (MessagingException ex) {
            Logger.getLogger(DefaultMailClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
