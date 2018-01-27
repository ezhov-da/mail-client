package ru.ezhov.mailclient;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;

/**
 * Текстовое поле с подсказкой
 * <p>
 *
 * @author ezhov_da
 */
public class JETextFieldHint extends JTextField {
    private static final Logger LOG = Logger.getLogger(JETextFieldHint.class.getName());
    protected KeyListener keyListener;
    protected String textForPaint;
    protected int defaultLenght = 0;
    protected int maxLenght = defaultLenght;
    protected String textCondition = ". Максимум: %s символов.";

    public JETextFieldHint(String textForPaint) {
        this.textForPaint = textForPaint;
    }

    public JETextFieldHint(String textForPaint, int maxLenght) {
        this.textForPaint = textForPaint;
        if (maxLenght < 1) {
            throw new IllegalArgumentException("Значение maxLenght должны быть больше нуля");
        } else {
            this.maxLenght = maxLenght;
            keyListener = new KeyListenerLenghtTextField();
            addKeyListener(keyListener);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        if ("".equals(getText())) {
            graphics2D.setPaint(Color.GRAY);
            Font font = getFont();
            int caretPosition;
            try {
                caretPosition = modelToView(getCaretPosition()).x;
            } catch (BadLocationException ex) {
                caretPosition = 5;
            }
            graphics2D.drawString(getTextForHint(), caretPosition, getHeight() - font.getSize() / 2);
        }
    }

    private String getTextForHint() {
        if (textForPaint != null && !"".equals(textForPaint) && maxLenght != defaultLenght) {
            return textForPaint + String.format(textCondition, maxLenght);
        }
        return textForPaint;
    }

    private class KeyListenerLenghtTextField extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            String text = getText();
            if (maxLenght != defaultLenght) {
                if (text.length() > maxLenght) {
                    int caretPosition = getCaretPosition();
                    text = text.substring(0, maxLenght);
                    setText(text);
                    if (caretPosition > text.length()) {
                        setCaretPosition(maxLenght);
                    } else {
                        setCaretPosition(caretPosition);
                    }
                }
            }
        }

    }

}
