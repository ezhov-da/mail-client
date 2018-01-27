package ru.ezhov.mailclient;

import javax.swing.table.TableModel;
import javax.swing.table.TableStringConverter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

/**
 * @author ezhov_da
 */
public class SorterStringTable extends TableStringConverter {
    private static final Logger LOG = Logger.getLogger(SorterStringTable.class.getName());
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString(TableModel model, int row, int column) {
        int i = 5;
        MessageFind messageFind = ((TreatmentMailboxModel) model).getValueMessageFind(row);
        switch (column) {
            case 0:
                return "";
            case 1:
                return messageFind.getTextListFrom();
            case 2:
                return messageFind.getSubject();
            case 3:
                return messageFind.getTextListTo();
            case 4:
                return dateFormat.format(messageFind.getDate());
            default:
                return "";
        }
    }
}
