package ru.ezhov.mailclient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Основной класс, который обрабатывает сохранение файлов
 * <p>
 *
 * @author ezhov_da
 */
class AddMailActionListener implements ActionListener, ListenerMailClient {
    private static final Logger LOG = Logger.getLogger(AddMailActionListener.class.getName());
    private TreatmentMailboxModel treatmentMailboxModel;
    private Component parentComponent;
    private Set<ListenerAttachment> listenerAttachments;
    private int fileMaxSize;
    private JButton buttonExecute;
    private Icon icon;

    public AddMailActionListener(TreatmentMailboxModel treatmentMailboxModel, Component parentComponent, int fileMaxSize) {
        this.treatmentMailboxModel = treatmentMailboxModel;
        this.parentComponent = parentComponent;
        this.fileMaxSize = fileMaxSize;
        this.icon = new ImageIcon(MailClientForm.class.getResource("/res/loader.gif"));
        listenerAttachments = new HashSet<>();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        buttonExecute = (JButton) e.getSource();
        //если список выбранных писем не 0
        if (treatmentMailboxModel.getCountSelectedMail() == 0) {
            return;
        }
        Thread thread = new Thread(new ExecuteSave());
        thread.start();
    }

    @Override
    public void addListenerAttachment(ListenerAttachment listenerAttachment) {
        listenerAttachments.add(listenerAttachment);
    }

    @Override
    public void removeListenerAttachment(ListenerAttachment listenerAttachment) {
        listenerAttachments.remove(listenerAttachment);
    }

    private void fireSaveMails(List<FileMessageSave> fileMessageSaves) {
        listenerAttachments.forEach((ListenerAttachment la) -> la.attachmentsSelect(fileMessageSaves));
    }


    private class ExecuteSave extends SwingWorker<Object, Object> {
        private MessageSaver messageSaver;
        private List<FileMessageSave> fileMessageSaves;
        private List<FileMessageSave> fileMessageSavesErrorSave;
        private List<FileMessageSave> fileMessageSavesBigSize;

        public ExecuteSave() {
            buttonExecute.setIcon(icon);
            buttonExecute.setEnabled(false);
        }

        @Override
        protected Object doInBackground() throws Exception {
            //сохраняем выбранный список писем
            messageSaver = new MessageSaver(treatmentMailboxModel.getSelectedMessages());
            fileMessageSaves = messageSaver.save();
            //получаем список писем, которые не сохранились (то есть по ним возникла ошибка)
            fileMessageSavesErrorSave = fileMessageSaves.stream().filter(fns -> fns.isError()).collect(Collectors.toList());
            //получаем список писем, размер которых больше установленного
            fileMessageSavesBigSize = fileMessageSaves.stream().filter(fns -> !fns.isError()).filter((FileMessageSave fns) ->
            {
                File file = new File(fns.getFullFilePathWithExte(FileMessageSave.Ext.EML));
                return fileMaxSize > file.getTotalSpace();
            }).collect(Collectors.toList());
            //если есть большие письма
            executeMailBigSize();
            createResultMsgAndSaveMail();
            return null;
        }


        private void executeMailBigSize() {
            if (!fileMessageSavesBigSize.isEmpty()) {
                //спрашиваем об архивации
                int ans = JOptionPane.showConfirmDialog(parentComponent,
                        MailResourceBundle.getString("msg.question.compress.body"),
                        MailResourceBundle.getString("msg.question.compress.title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                //пользователь подтвердил архивирование
                if (ans == JOptionPane.YES_OPTION) {
                    MessageCompress messageCompress = new MessageCompress(fileMessageSaves);
                    //архивируем список писем
                    //пока ни как не используем результирующий список архивированных писем
                    List<FileMessageSave> fileMessageSavesCompressResult = messageCompress.compress();
                }
            }
        }

        private void createResultMsgAndSaveMail() {
            //выводим результат сохранения, ошибочне письма и итоговые
            StringBuilder stringBuilderGood = new StringBuilder();
            StringBuilder stringBuilderError = new StringBuilder();
            fileMessageSaves.forEach(fms ->
            {
                stringBuilderGood.append("- ");
                String subject = fms.getMessageFind().getSubject();
                if (Objects.isNull(subject)) {
                    subject = MailResourceBundle.getString("empty.subject");
                }
                if (fms.isError()) {
                    stringBuilderError.append(subject);
                } else {
                    stringBuilderGood.append(subject);
                }
                stringBuilderGood.append("\n");
            });
            String resultText = "";
            String goodText = stringBuilderGood.toString();
            String errorText = stringBuilderError.toString();
            if ("".equals(goodText)) {
                resultText = resultText + MailResourceBundle.getString("msg.not.good.save.mail");
            } else {
                resultText = resultText + MailResourceBundle.getString("msg.good.save.mail") + goodText;
            }
            if (!"".equals(errorText)) {
                resultText = resultText + MailResourceBundle.getString("msg.error.save.mail") + errorText;
            }
            int resultAnswear = JOptionPane.showConfirmDialog(parentComponent, resultText, MailResourceBundle.getString("msg.result.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (resultAnswear == JOptionPane.YES_OPTION) {
                fireSaveMails(fileMessageSaves);
            }
        }

        @Override
        protected void done() {
            buttonExecute.setIcon(null);
            buttonExecute.setEnabled(true);
        }
    }

}
