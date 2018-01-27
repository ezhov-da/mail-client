package ru.ezhov.mailclient;

import java.util.logging.Logger;

/**
 * Это класс, который хранит результат сохранения писем
 * <p>
 *
 * @author ezhov_da
 */
public class FileMessageSave {
    private static final Logger LOG = Logger.getLogger(FileMessageSave.class.getName());
    private Ext nowExt;
    private String fullPathToFileSaveWithoutExtension;
    private boolean error;
    private Exception exception;
    private MessageFind messageFind;

    public FileMessageSave() {
        this.nowExt = Ext.EML;
    }

    public String getFullPathToFileSaveWithoutExtension() {
        return fullPathToFileSaveWithoutExtension;
    }

    public void setFullPathToFileSaveWithoutExtension(String fullPathToFileSaveWithoutExtension) {
        this.fullPathToFileSaveWithoutExtension = fullPathToFileSaveWithoutExtension;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public MessageFind getMessageFind() {
        return messageFind;
    }

    public void setMessageFind(MessageFind messageFind) {
        this.messageFind = messageFind;
    }

    public String getFullFilePathWithExte(Ext ext) {
        return fullPathToFileSaveWithoutExtension + ext.getExt();
    }

    public String getFullFilePathWithNowExt() {
        return fullPathToFileSaveWithoutExtension + nowExt.getExt();
    }

    public Ext getNowExt() {
        return nowExt;
    }

    public void setNowExt(Ext nowExt) {
        this.nowExt = nowExt;
    }

    public enum Ext {
        EML(".eml"), ZIP("zip");

        private String ext;

        Ext(String ext) {
            this.ext = ext;
        }

        public String getExt() {
            return ext;
        }
    }
}
