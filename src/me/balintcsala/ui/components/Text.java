package me.balintcsala.ui.components;

import me.balintcsala.ui.editor.Content;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Text extends JLabel {

    private static final HashMap<Character, Color> colorCodes = new HashMap<>();
    static {
        colorCodes.put('0', Color.BLACK);
        colorCodes.put('1', new Color(0, 0, 170));
        colorCodes.put('2', new Color(0, 170, 0));
        colorCodes.put('3', new Color(0, 170, 170));
        colorCodes.put('4', new Color(170, 0, 0));
        colorCodes.put('5', new Color(170, 0, 170));
        colorCodes.put('6', new Color(255, 170, 0));
        colorCodes.put('7', new Color(170, 170, 170));
        colorCodes.put('8', new Color(85, 85, 85));
        colorCodes.put('9', new Color(85, 85, 255));
        colorCodes.put('a', new Color(85, 255, 85));
        colorCodes.put('b', new Color(85, 255, 255));
        colorCodes.put('c', new Color(255, 85, 85));
        colorCodes.put('d', new Color(255, 85, 255));
        colorCodes.put('e', new Color(255, 255, 85));
        colorCodes.put('f', Color.WHITE);
    }

    private String text;
    private int width, height;
    private boolean timerOn = false;
    private Timer timer;

    public Text(String text, int width, int height) {
        this.text = text;
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
    }

    public void updateText(String text) {
        this.text = text;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics = (Graphics2D) g;
        graphics.setFont(Content.font);
        FontMetrics metrics = graphics.getFontMetrics();

        String temp = text.replaceAll("§.", "");
        int stringWidth = metrics.stringWidth(temp);
        int x;
        if (stringWidth < width) {
            if (timerOn) {
                timerOn = false;
                timer.stop();
            }
            x = (width - stringWidth) / 2;
        } else {
            if (!timerOn) {
                timerOn = true;
                timer = new Timer(50, e -> repaint());
                timer.start();
            }
            int maxOffset = stringWidth - width;
            x = (int) -Math.round((System.currentTimeMillis() / 62.5) % (maxOffset + 40) - 20);
            x = Math.max(Math.min(x, 0), -maxOffset);
        }

        int y = (height - metrics.getHeight()) / 2 + metrics.getAscent();

        graphics.setColor(Color.DARK_GRAY);
        graphics.drawString(temp, x + 2, y + 2);
        graphics.setColor(Color.WHITE);

        int lastStop = 0;
        int xOffset = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '§') {
                graphics.drawString(text.substring(lastStop, i), x + xOffset, y);
                xOffset += metrics.stringWidth(text.substring(lastStop, i));
                graphics.setColor(colorCodes.get(text.charAt(i + 1)));
                lastStop = i + 2;
                i++;
            }
        }
        graphics.drawString(text.substring(lastStop), x + xOffset, y);

        graphics.dispose();
    }
}
