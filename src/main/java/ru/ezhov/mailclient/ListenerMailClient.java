package ru.ezhov.mailclient;

/**
 * @author ezhov_da
 */
public interface ListenerMailClient {
    /**
     * Добавляем слушателя сохранения вложений
     * <p>
     *
     * @param listenerAttachment
     */
    void addListenerAttachment(ListenerAttachment listenerAttachment);

    void removeListenerAttachment(ListenerAttachment listenerAttachment);

}
