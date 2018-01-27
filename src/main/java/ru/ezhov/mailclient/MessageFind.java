package ru.ezhov.mailclient;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Класс обертка, в который мы помещаем найденное письмо
 * <p>
 *
 * @author ezhov_da
 */
public class MessageFind {
    private static final Logger LOG = Logger.getLogger(MessageFind.class.getName());
    private boolean selected;
    private String patternFullDate = "dd.MM.yyyy HH:mm";
    private String patternNowDate = "HH:mm";
    private List<InternetAddress> fromList;
    private List<InternetAddress> toList;
    private List<InternetAddress> ccList;
    private String subject;
    private String body;
    private String dateText;
    private Date date;
    private Message message;

    public MessageFind() {
        fromList = new ArrayList<>();
        toList = new ArrayList<>();
        ccList = new ArrayList<>();
    }

    public static Logger getLOG() {
        return LOG;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        checkAndSetDate(date);
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void addFrom(InternetAddress from) {
        fromList.add(from);
    }

    public void addTo(InternetAddress to) {
        toList.add(to);
    }

    public void addCc(InternetAddress cc) {
        ccList.add(cc);
    }

    public String getDateText() {
        return dateText;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private void checkAndSetDate(Date date) {
        Calendar calendarSet = Calendar.getInstance();
        calendarSet.setTime(date);
        calendarSet.set(Calendar.HOUR_OF_DAY, 0);
        calendarSet.set(Calendar.MINUTE, 0);
        calendarSet.set(Calendar.SECOND, 0);
        calendarSet.set(Calendar.MILLISECOND, 0);
        Calendar calendarNow = Calendar.getInstance();
        calendarNow.setTime(new Date());
        calendarNow.set(Calendar.HOUR_OF_DAY, 0);
        calendarNow.set(Calendar.MINUTE, 0);
        calendarNow.set(Calendar.SECOND, 0);
        calendarNow.set(Calendar.MILLISECOND, 0);
        DateFormat dateFormat;
        int compare = calendarSet.compareTo(calendarNow);
        if (compare == 0) {
            dateFormat = new SimpleDateFormat(patternNowDate);
        } else {
            dateFormat = new SimpleDateFormat(patternFullDate);
        }
        dateText = dateFormat.format(date);
    }

    public String getTextListFrom() {
        return getStringList(fromList);
    }

    public String getTextListTo() {
        return getStringList(toList);
    }

    public String getTextListCc() {
        return getStringList(ccList);
    }

    private String getStringList(List<InternetAddress> internetAddresses) {
        StringBuilder stringBuilder = new StringBuilder();
        internetAddresses.forEach(ia ->
        {
            String namePersonal = ia.getPersonal();
            if (namePersonal == null || "".equals(ia.getPersonal())) {
                namePersonal = ia.getAddress();
            }
            stringBuilder.append(namePersonal);
            stringBuilder.append(", ");
        });
        String text = stringBuilder.toString();
        return text.substring(0, text.length() - 2);
    }

}
