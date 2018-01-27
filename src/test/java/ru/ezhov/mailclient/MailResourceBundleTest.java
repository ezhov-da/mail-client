package ru.ezhov.mailclient;

/**
 * @author ezhov_da
 */
public class MailResourceBundleTest {

    public static void main(String[] args) {
        System.out.println(MailResourceBundle.getString("size.first.column"));
        System.out.println(MailResourceBundle.getInt("size.first.column"));
    }

}
