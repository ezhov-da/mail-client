package ru.ezhov.mailclient;

/**
 * Интерфейс, который предоставляет список разрешенных тегов для отображения
 * <p>
 *
 * @author ezhov_da
 */
public interface WhiteListTagHolder {
    /**
     * Список разрешенных тегоы, может быть null
     * <p>
     *
     * @return
     */
    default String[] getWhiteListTags() {
        return null;
    }

    ;

}
