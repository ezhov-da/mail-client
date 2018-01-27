package ru.ezhov.mailclient;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.FromStringTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Окно работы клиента
 * <p>
 *
 * @author ezhov_da
 */
class MailClientForm extends JDialog implements ListenerMailClient, Closeable {
    private static final Logger LOG = Logger.getLogger(MailClientForm.class.getName());
    private String textButtonExecuteSave;

    private JTextField textFieldFind;
    private JLabel labelInfo;
    private JToggleButton buttonFrom;
    private JToggleButton buttonTo;
    private JToggleButton buttonSubject;
    private JTable tableResult;
    private TreatmentMailboxModel treatmentMailboxModel;
    private JTextPane textPane;
    private JButton buttonSaveMails;
    private JButton selectAll;
    private JButton clearAll;
    private InitMailClientObject initMailClientObject;
    private AddMailActionListener addMailActionListener;
    private PanelFinder panelFinder;
    private Icon icon;

    public MailClientForm(InitMailClientObject initMailClientObject) throws MessagingException {
        this.initMailClientObject = initMailClientObject;
        this.icon = new ImageIcon(MailClientForm.class.getResource("/src/main/resources/res/loader.gif"));
        init();
        setTitle(initMailClientObject.getTitleForm());
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(constructPanelMails());
        splitPane.setBottomComponent(createBottomPanel());
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(textPane), BorderLayout.CENTER);
        List<JLabel> labelsInfo = initMailClientObject.getLabelsInfo();
        //смотрим есть ли информационные JLabel
        if (Objects.nonNull(labelsInfo) && !labelsInfo.isEmpty()) {
            JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            labelsInfo.forEach(jl -> panelInfo.add(jl));
            panel.add(panelInfo, BorderLayout.SOUTH);
        }
        return panel;
    }

    private void init() throws MessagingException {
        labelInfo = new JLabel();
        labelInfo.setText(MailResourceBundle.getString("title.label.search"));
        labelInfo.setIcon(icon);
        labelInfo.setVisible(false);
        buttonFrom = new JToggleButton(MailResourceBundle.getString("title.column.2"));
        buttonFrom.addActionListener(new ListenerToggleButtonFind());
        buttonSubject = new JToggleButton(MailResourceBundle.getString("title.column.3"));
        buttonSubject.addActionListener(new ListenerToggleButtonFind());
        buttonTo = new JToggleButton(MailResourceBundle.getString("title.column.4"));
        buttonTo.addActionListener(new ListenerToggleButtonFind());
        textButtonExecuteSave = initMailClientObject.getTitleButtonExecuteSave();
        treatmentMailboxModel =
                new TreatmentMailboxModel(
                        initMailClientObject.getPropertiesMailConnection(),
                        initMailClientObject.getAuth());
        textFieldFind = new JETextFieldHint(MailResourceBundle.getString("title.text.field"));
        tableResult = new JTable();
        tableResult.setModel(treatmentMailboxModel);
        initTable();
        textFieldFind.addKeyListener(new ListenerFindMail());
        tableResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableResult.getSelectionModel().addListSelectionListener(new TableListSelectionEvent());
        textPane = new JTextPane();
        buttonSaveMails = new JButton(getTextButton());
        addMailActionListener =
                new AddMailActionListener(
                        treatmentMailboxModel,
                        MailClientForm.this, initMailClientObject.getFileMaxSize());
        buttonSaveMails.addActionListener(addMailActionListener);
        treatmentMailboxModel.addTableModelListener((TableModelEvent e) ->
        {
            SwingUtilities.invokeLater(() -> buttonSaveMails.setText(getTextButton()));

        });
        selectAll = new JButton(initMailClientObject.getTitleButtonSelectAll());
        selectAll.addActionListener(new SelectedMails(true));
        clearAll = new JButton(initMailClientObject.getTitleButtonDeSelectAll());
        clearAll.addActionListener(new SelectedMails(false));
        panelFinder = new PanelFinder();
    }

    private void initTable() {
        tableResult
                .getColumnModel()
                .getColumn(0)
                .setMaxWidth(MailResourceBundle.getInt("size.first.column"));
        int intLastColumn = MailResourceBundle.getInt("size.last.column");
        tableResult.getColumnModel().getColumn(4).setMaxWidth(intLastColumn);
        tableResult.getColumnModel().getColumn(4).setMinWidth(intLastColumn);
        tableResult.setRowSorter(new TableRowSorter(treatmentMailboxModel) {
            private final SorterStringTable stringConverterPpzTask = new SorterStringTable();

            @Override
            public SorterStringTable getStringConverter() {
                return stringConverterPpzTask;
            }
        });
    }

    private JPanel constructPanelMails() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(panelFinder, BorderLayout.NORTH);
        panel.add(new JScrollPane(tableResult), BorderLayout.CENTER);
        //
        JPanel paneButtonl = new JPanel();
        paneButtonl.add(selectAll);
        paneButtonl.add(clearAll);
        paneButtonl.add(buttonSaveMails);
        //
        panel.add(paneButtonl, BorderLayout.SOUTH);
        return panel;
    }

    private String getTextButton() {
        return String.format(textButtonExecuteSave, treatmentMailboxModel.getCountSelectedMail());
    }

    @Override
    public void addListenerAttachment(ListenerAttachment listenerAttachment) {
        addMailActionListener.addListenerAttachment(listenerAttachment);
    }

    @Override
    public void removeListenerAttachment(ListenerAttachment listenerAttachment) {
        addMailActionListener.removeListenerAttachment(listenerAttachment);
    }

    @Override
    public void close() throws IOException {
        treatmentMailboxModel.close();
    }

    private void executeFind() {
        String val = textFieldFind.getText();
        if (val != null && !"".equals(val)) {
            Thread thread = new Thread(new FindMailWorker());
            thread.start();
        }
    }

    private class PanelFinder extends JPanel {

        public PanelFinder() {
            super(new GridBagLayout());
            int counter = 0;
            Insets insets = new Insets(5, 5, 5, 5);
            add(textFieldFind, new GridBagConstraints(0, counter, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
            add(labelInfo, new GridBagConstraints(1, counter, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
            add(buttonFrom, new GridBagConstraints(2, counter, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
            add(buttonTo, new GridBagConstraints(3, counter, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
            add(buttonSubject, new GridBagConstraints(4, counter, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
            //
        }

        public SearchTerm[] getSearchtem() {
            List<SearchTerm> searchTerms = new ArrayList<>();
            String textFind = textFieldFind.getText();
            //на всякий случай, если ничего не выбрано, ищем во всех доступных полях
            if (!buttonFrom.isSelected() && !buttonTo.isSelected() && !buttonSubject.isSelected()) {
                searchTerms.add(new FromStringTerm(textFind));
                searchTerms.add(new RecipientStringTerm(Message.RecipientType.TO, textFind));
                searchTerms.add(new RecipientStringTerm(Message.RecipientType.CC, textFind));
                searchTerms.add(new RecipientStringTerm(Message.RecipientType.BCC, textFind));
                searchTerms.add(new SubjectTerm(textFind));
            }
            if (buttonFrom.isSelected()) {
                searchTerms.add(new FromStringTerm(textFind));
            }
            if (buttonTo.isSelected()) {
                searchTerms.add(new RecipientStringTerm(Message.RecipientType.TO, textFind));
                searchTerms.add(new RecipientStringTerm(Message.RecipientType.CC, textFind));
                searchTerms.add(new RecipientStringTerm(Message.RecipientType.BCC, textFind));
            }
            if (buttonSubject.isSelected()) {
                searchTerms.add(new SubjectTerm(textFind));
            }
            SearchTerm[] terms = searchTerms.toArray(new SearchTerm[searchTerms.size()]);
            return terms;
        }

    }

    //слушатель на поиск писем
    private class ListenerFindMail extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                executeFind();
            }
        }
    }

    private class FindMailWorker extends SwingWorker<Object, Object> {

        public FindMailWorker() {
            labelInfo.setVisible(true);
            panelFinder.revalidate();
        }


        @Override
        protected Object doInBackground() throws Exception {
            treatmentMailboxModel.find(panelFinder.getSearchtem());
            return null;
        }

        @Override
        protected void done() {
            treatmentMailboxModel.commitFinder();
            labelInfo.setVisible(false);
            panelFinder.revalidate();
        }
    }

    //слушатель на выбор в таблице
    private class TableListSelectionEvent implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            SwingUtilities.invokeLater(() ->
            {
                if (!e.getValueIsAdjusting()) {
                    int i = tableResult.getSelectedRow();
                    MessageFind messageFind =
                            treatmentMailboxModel
                                    .getValueMessageFind(
                                            tableResult.convertRowIndexToModel(i));
                    String text = messageFind.getBody();
                    if (text.contains("<html>") || text.contains("<htm>")) {
                        String[] whiteListArray =
                                initMailClientObject
                                        .getWhiteListTagHolder()
                                        .getWhiteListTags();
                        if (whiteListArray != null) {
                            Whitelist whitelist = new Whitelist();
                            whitelist.addTags(whiteListArray);
                            text = Jsoup.clean(text, whitelist);
                        }
                        textPane.setContentType("text/html");
                        textPane.setText("<html>" + text);
                    } else {
                        textPane.setContentType("text/plane");
                        textPane.setText(text);
                    }
                    textPane.setCaretPosition(0);
                }
            });
        }

    }

    //слушатель на кнопке выбрать все или сбросить
    private class SelectedMails implements ActionListener {
        private boolean select;

        public SelectedMails(boolean select) {
            this.select = select;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(() ->
            {

                try {
                    treatmentMailboxModel.selectMessageFind(select);
                } catch (IndexOutOfBoundsException ex) {
                    //Exception in thread "AWT-EventQueue-0" java.lang.IndexOutOfBoundsException: Invalid index
                    //	at javax.swing.DefaultRowSorter.convertRowIndexToModel(DefaultRowSorter.java:514)
                    //	at javax.swing.JTable.convertRowIndexToModel(JTable.java:2644)
                    //	at ru.ezhov.mailclient.MailClientForm$TableListSelectionEvent.lambda$valueChanged$3(MailClientForm.java:298)
                    //	at ru.ezhov.mailclient.MailClientForm$TableListSelectionEvent$$Lambda$23/1449011547.run(Unknown Source)
                    //	at java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:311)
                }
            });
        }
    }

    private class ListenerToggleButtonFind implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(() -> executeFind());
        }

    }

}
