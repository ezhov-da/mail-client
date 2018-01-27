package ru.ezhov.mailclient;

import javax.mail.Authenticator;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Объект, который инициализирует почтового клиента
 * <p>
 *
 * @author ezhov_da
 */
public class InitMailClientObject {
    private static final Logger LOG = Logger.getLogger(InitMailClientObject.class.getName());
    private String titleForm;
    private String titleButtonSelectAll;
    private String titleButtonDeSelectAll;
    private String titleButtonExecuteSave;
    private Properties propertiesMailConnection;
    private Authenticator auth;
    private int fileMaxSize;
    private WhiteListTagHolder whiteListTagHolder;
    private List<JLabel> labelsInfo;

    public InitMailClientObject(Properties propertiesMailConnection, Authenticator auth) {
        this.titleForm = MailResourceBundle.getString("default.title.form");
        this.titleButtonSelectAll = MailResourceBundle.getString("default.button.title.select.all");
        this.titleButtonDeSelectAll = MailResourceBundle.getString("default.button.title.deselect.all");
        this.titleButtonExecuteSave = MailResourceBundle.getString("default.button.title.save");
        this.fileMaxSize = MailResourceBundle.getInt("file.max.size");
        this.propertiesMailConnection = propertiesMailConnection;
        this.auth = auth;
        this.whiteListTagHolder = new WhiteListTagHolder() {
        };
        this.labelsInfo = new ArrayList<>();
    }

    public InitMailClientObject(
            String titleForm,
            String titleButtonSelectAll,
            String titleButtonDeSelectAll,
            String titleButtonExecuteSave,
            Properties propertiesMailConnection,
            Authenticator auth,
            int fileMaxSize,
            WhiteListTagHolder whiteListTagHolder) {
        this.titleForm = titleForm;
        this.titleButtonSelectAll = titleButtonSelectAll;
        this.titleButtonDeSelectAll = titleButtonDeSelectAll;
        this.titleButtonExecuteSave = titleButtonExecuteSave;
        this.propertiesMailConnection = propertiesMailConnection;
        this.auth = auth;
        this.fileMaxSize = fileMaxSize;
        this.whiteListTagHolder = whiteListTagHolder;
    }

    public String getTitleForm() {
        return titleForm;
    }

    public void setTitleForm(String titleForm) {
        this.titleForm = titleForm;
    }

    public String getTitleButtonSelectAll() {
        return titleButtonSelectAll;
    }

    public void setTitleButtonSelectAll(String titleButtonSelectAll) {
        this.titleButtonSelectAll = titleButtonSelectAll;
    }

    public String getTitleButtonDeSelectAll() {
        return titleButtonDeSelectAll;
    }

    public void setTitleButtonDeSelectAll(String titleButtonDeSelectAll) {
        this.titleButtonDeSelectAll = titleButtonDeSelectAll;
    }

    public String getTitleButtonExecuteSave() {
        return titleButtonExecuteSave;
    }

    public void setTitleButtonExecuteSave(String titleButtonExecuteSave) {
        this.titleButtonExecuteSave = titleButtonExecuteSave;
    }

    public Properties getPropertiesMailConnection() {
        return propertiesMailConnection;
    }

    public void setPropertiesMailConnection(Properties propertiesMailConnection) {
        this.propertiesMailConnection = propertiesMailConnection;
    }

    public Authenticator getAuth() {
        return auth;
    }

    public void setAuth(Authenticator auth) {
        this.auth = auth;
    }

    public int getFileMaxSize() {
        return fileMaxSize;
    }

    public void setFileMaxSize(int fileMaxSize) {
        this.fileMaxSize = fileMaxSize;
    }

    public WhiteListTagHolder getWhiteListTagHolder() {
        return whiteListTagHolder;
    }

    public void setWhiteListTagHolder(WhiteListTagHolder whiteListTagHolder) {
        this.whiteListTagHolder = whiteListTagHolder;
    }

    public List<JLabel> getLabelsInfo() {
        return labelsInfo;
    }

    public void setLabelsInfo(List<JLabel> labelsInfo) {
        this.labelsInfo = labelsInfo;
    }

    public void add(JLabel labelInfo) {
        labelsInfo.add(labelInfo);
    }

}
