package ru.ezhov.mailclient;

import java.util.List;

/**
 * Интерфейс слушателя который уведомляет всех заинтересованных клиентов о подтверждении вложения
 * <p>
 *
 * @author ezhov_da
 */
public interface ListenerAttachment {
    public void attachmentsSelect(List<FileMessageSave> fileMessageSaves);
}
