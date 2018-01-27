package ru.ezhov.mailclient;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import javax.swing.table.AbstractTableModel;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Модель таблицы
 * <p>
 *
 * @author ezhov_da
 */
class TreatmentMailboxModel extends AbstractTableModel implements Closeable {
    private static final Logger LOG = Logger.getLogger(TreatmentMailboxModel.class.getName());
    private static final String[] NAME_COLUMN = new String[]
            {
                    MailResourceBundle.getString("title.column.1"),
                    MailResourceBundle.getString("title.column.2"),
                    MailResourceBundle.getString("title.column.3"),
                    MailResourceBundle.getString("title.column.4"),
                    MailResourceBundle.getString("title.column.5")
            };
    private Properties props;
    private Authenticator auth;
    private List<MessageFind> messages = new ArrayList<>();
    private List<MessageFind> finds = new ArrayList<>();
    private int countSelectedMail;
    private Session session;
    private Store store;
    private Folder inbox;

    public TreatmentMailboxModel(Properties properties, Authenticator authenticator)
            throws NoSuchProviderException, MessagingException {
        this.props = properties;
        this.auth = authenticator;
        initSession();
        initStore();
        openFolder();
    }

    private void initSession() {
        session = Session.getDefaultInstance(props, auth);

    }

    private void initStore() throws NoSuchProviderException, MessagingException {
        store = session.getStore();
        store.connect();
    }

    private boolean isConnectedStrore() {
        return store.isConnected();
    }


    private void openFolder() throws MessagingException {
        inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);
    }

    private boolean isOpenInboxFolder() {
        return inbox.isOpen();
    }

    public void find(SearchTerm[] find) {
        try {
            preFindActions();
            readFolder(inbox, find);
            countSelectedMail = 0;
            finds.sort((MessageFind m1, MessageFind m2) ->
            {
                return m2.getDate().compareTo(m1.getDate());
            });
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(TreatmentMailboxModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException | IOException ex) {
            Logger.getLogger(TreatmentMailboxModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Столкнулись с такой проблемой:
    //при долгом открытии пропадает подключение, по-этому,
    //нужно перед поиском перепроверять подключения и пересоздавать
    private void preFindActions() throws MessagingException {
        if (!isConnectedStrore()) {
            initStore();
        }
        if (!isOpenInboxFolder()) {
            openFolder();
        }
    }

    public void commitFinder() {
        messages = finds;
        fireTableDataChanged();
    }

    public int getCountSelectedMail() {
        return countSelectedMail;
    }

    @Override
    public int getRowCount() {
        return messages.size();
    }

    @Override
    public int getColumnCount() {
        return NAME_COLUMN.length;
    }

    @Override
    public String getColumnName(int column) {
        return NAME_COLUMN[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String returnText = "";
        MessageFind messageFind = messages.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return messageFind.isSelected();
            case 1:
                returnText = messageFind.getTextListFrom();
                break;
            case 2:
                returnText = messageFind.getSubject();
                break;
            case 3:
                returnText = messageFind.getTextListTo();
                break;
            case 4:
                returnText = messageFind.getDateText();
                break;
        }
        return returnText;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            boolean b = Boolean.valueOf(aValue.toString());
            MessageFind messageFind = messages.get(rowIndex);
            messageFind.setSelected(b);
            counter(b);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }

    public List<MessageFind> getSelectedMessages() {
        List<MessageFind> messageFinds = messages.stream().filter(ms -> ms.isSelected()).collect(Collectors.toList());
        return messageFinds;
    }


    private void counter(boolean select) {
        if (select) {
            countSelectedMail++;
        } else {
            countSelectedMail--;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    public MessageFind getValueMessageFind(int i) {
        if (i == -1) {
            return messages.get(0);
        }
        return messages.get(i);
    }

    private void readFolder(Folder inbox, SearchTerm[] find)
            throws MessagingException, FileNotFoundException, IOException {
        Message[] arrMsgs;
        finds = new ArrayList<>();
        if (find == null || find.length == 0) {
            arrMsgs = inbox.getMessages();
        } else {
            arrMsgs = inbox.search(new OrTerm(find));
        }
        finds.clear();
        MessageFind messageFind;
        for (Message message : arrMsgs) {
            messageFind = new MessageFind();
            messageFind.setSubject(message.getSubject());
            messageFind.setDate(message.getSentDate());
            messageFind.setMessage(message);
            collectFrom(message.getFrom(), messageFind);
            collectTo(message.getRecipients(Message.RecipientType.TO), messageFind);
            collectCc(message.getRecipients(Message.RecipientType.CC), messageFind);
            try {
                Object object = message.getContent();
                StringBuilder stringBuilder = new StringBuilder();
                executeContent(object, stringBuilder);
                messageFind.setBody(stringBuilder.toString());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            finds.add(messageFind);
        }
    }

    private void collectFrom(Address[] addresses, MessageFind messageFind)
            throws AddressException {
        for (Address address : addresses) {
            InternetAddress internetAddress = new InternetAddress(address.toString());
            messageFind.addFrom(internetAddress);
        }
    }

    private void collectTo(Address[] addresses, MessageFind messageFind) throws AddressException {
        for (Address address : addresses) {
            InternetAddress internetAddress = new InternetAddress(address.toString());
            messageFind.addTo(internetAddress);
        }
    }

    private void collectCc(Address[] addresses, MessageFind messageFind) throws AddressException {
        if (addresses != null) {
            for (Address address : addresses) {
                InternetAddress internetAddress = new InternetAddress(address.toString());
                messageFind.addCc(internetAddress);
            }
        }
    }

    private void executeContent(Object content, StringBuilder stringBuilder)
            throws IOException, MessagingException {
        if (content instanceof String) {
            stringTreatmentContent((String) content, stringBuilder);
        } else if (content instanceof MimeMultipart) {
            mimeMultipartTreatmentOnlyStringContent((MimeMultipart) content, stringBuilder);
        }
    }

    private StringBuilder stringTreatmentContent(String message, StringBuilder stringBuilder)
            throws IOException, MessagingException {
        stringBuilder.append(message);
        return stringBuilder;
    }

    private void mimeMultipartTreatmentOnlyStringContent(MimeMultipart mimeMultipart, StringBuilder stringBuilder)
            throws IOException, MessagingException {
        for (int mp = 0; mp < mimeMultipart.getCount(); mp++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(mp);
            executeContent(bodyPart.getContent(), stringBuilder);
        }
    }

    public List<MessageFind> getMessages() {
        return messages;
    }

    public void selectMessageFind(final boolean select) {

        List<MessageFind> listFilteredMsg =
                messages
                        .stream()
                        .filter(messageFind -> messageFind.isSelected() != select)
                        .collect(Collectors.toList());

        listFilteredMsg.forEach(messageFind ->
        {
            messageFind.setSelected(select);
            counter(select);
        });
        fireTableDataChanged();
    }

    @Override
    public void close() throws IOException {
        if (inbox != null) {
            try {
                inbox.close(false);
            } catch (MessagingException ex) {
                Logger.getLogger(TreatmentMailboxModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (store != null) {
            try {
                store.close();
            } catch (MessagingException ex) {
                Logger.getLogger(TreatmentMailboxModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
