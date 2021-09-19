package me.balintcsala.ui;

import me.balintcsala.ui.components.Button;
import me.balintcsala.ui.components.Text;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Content extends JPanel {

    public static final int PIXEL_SCALE = 4;
    public static Font font;

    private BufferedImage image;

    public Content() {

        try {
            image = ImageIO.read(new File("res/options_background.png"));
            if (font == null) {
                font = Font.createFont(Font.TRUETYPE_FONT, new File("res/font.otf"))
                        .deriveFont(Font.PLAIN, 20);
            }
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        setBorder(new EmptyBorder(24, 8, 48, 8));

        add(new Text("Shader Options", 800, 20));

        JPanel row = new JPanel();
        row.setLayout(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.add(new Button("Blur...", () -> {}));
        row.add(new Button("Water...", () -> {}));

        add(row);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();

        for (int x = 0; x < size.width; x += image.getWidth() * PIXEL_SCALE) {
            for (int y = 0; y < size.height; y += image.getHeight() * PIXEL_SCALE) {
                g.drawImage(
                        image, x, y,
                        image.getWidth() * PIXEL_SCALE, image.getHeight() * PIXEL_SCALE,
                        this);
            }
        }
    }

}
