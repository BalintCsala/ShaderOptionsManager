package me.balintcsala.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Text extends JLabel {

    private static Font font;

    public Text(String text) {
        super(text);
        if (font == null) {
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, new File("res/font.otf"))
                        .deriveFont(Font.PLAIN, 40);
            } catch (FontFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //setFont(font);
        setForeground(Color.WHITE);
    }

}
