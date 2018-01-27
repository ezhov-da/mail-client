package ru.ezhov.mailclient;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс, который отвечает за сохранение писем во временной директории
 * <p>
 *
 * @author ezhov_da
 */
class MessageSaver {
    private static final Logger LOG = Logger.getLogger(MessageSaver.class.getName());
    private List<MessageFind> messageFindsForSave;
    private int counter;
    private String tempDir;

    MessageSaver(List<MessageFind> messageFindsForSave) {
        this.messageFindsForSave = messageFindsForSave;
        tempDir = System.getProperty("java.io.tmpdir");
    }

    /**
     * Сохраняем выбранные письма во временную директорию
     * <p>
     *
     * @return список с результатами сохранения писем
     */
    public List<FileMessageSave> save() {
        List<FileMessageSave> fileMessageSaves = new ArrayList<>();
        messageFindsForSave.forEach(mf ->
        {
            counter = 0;
            FileMessageSave fileMessageSave = new FileMessageSave();
            fileMessageSave.setMessageFind(mf);
            //
            String fileName = getUniqueNameFileMsg(fileMessageSave);
            fileMessageSave.setFullPathToFileSaveWithoutExtension(fileName);
            File fileTest = new File(fileMessageSave.getFullFilePathWithNowExt());
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileTest)) {
                mf.getMessage().writeTo(fileOutputStream);
            } catch (IOException | MessagingException ex) {
                fileMessageSave.setError(true);
                fileMessageSave.setException(ex);
                LOG.log(Level.WARNING, "Ошибка сохоранения письма: " + fileName, ex);
            }
            fileMessageSaves.add(fileMessageSave);
        });
        return fileMessageSaves;
    }

    //получаем путь и название файла
    private String getUniqueNameFileMsg(FileMessageSave messageFind) {
        String pathAndNameFile;
        String subject = messageFind.getMessageFind().getSubject();
        String clearPathWithoutExtension;
        if (counter == 0) {
            clearPathWithoutExtension = tempDir + replaceWrongSymbol(subject);
        } else {
            clearPathWithoutExtension = tempDir + replaceWrongSymbol(subject) + " (" + counter + ")";
        }
        pathAndNameFile = clearPathWithoutExtension + messageFind.getNowExt().getExt();
        File f = new File(pathAndNameFile);
        if (f.exists()) {
            counter++;
            clearPathWithoutExtension = getUniqueNameFileMsg(messageFind);
        }
        return clearPathWithoutExtension;
    }

    //меняем запрещенные символы в названии письма
    private String replaceWrongSymbol(String nameFile) {
        Character[] character = new Character[]
                {
                        '*', '|', '\\', ':', '\"', '<', '>', '?', '/'
                };
        if (Objects.isNull(nameFile)) {
            return MailResourceBundle.getString("empty.subject");
        } else {
            for (char c : character) {
                nameFile = nameFile.replace(c, ' ');
            }
            return nameFile;
        }
    }
}
