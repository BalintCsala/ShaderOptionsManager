package me.balintcsala.ui.components;

import me.balintcsala.ui.editor.Content;

import javax.swing.*;
import java.awt.*;

public class Text extends JLabel {

    private String text;
    private int width, height;

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
        int x = (width - metrics.stringWidth(text)) / 2;
        int y = (height - metrics.getHeight()) / 2 + metrics.getAscent();

        graphics.setColor(Color.DARK_GRAY);
        graphics.drawString(text, x + 2, y + 2);
        graphics.setColor(Color.WHITE);
        graphics.drawString(text, x, y);

        graphics.dispose();
    }
}
