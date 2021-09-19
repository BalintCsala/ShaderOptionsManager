package me.balintcsala.ui.components;

import me.balintcsala.ui.editor.Content;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Button extends JButton {

    public enum MouseButton {
        LEFT,
        RIGHT,
    }

    private static ImageIcon background;
    private static ImageIcon backgroundHover;

    private final ButtonClickListener clickListener;
    private final Text label;

    public Button(String text, ButtonClickListener clickListener) {
        this.clickListener = clickListener;

        if (background == null) {
            background = new ImageIcon(getClass().getResource("/button.png"));
            backgroundHover = new ImageIcon(getClass().getResource("/button_highlight.png"));
        }

        setIcon(background);
        setRolloverIcon(backgroundHover);
        setPressedIcon(backgroundHover);
        setFont(Content.font);
        setForeground(Color.WHITE);
        setPreferredSize(new Dimension(380, 40));
        setBorderPainted(false);
        setVerticalTextPosition(CENTER);
        setHorizontalTextPosition(CENTER);

        setLayout(new GridBagLayout());
        label = new Text(text, 340, 20);
        add(label);

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickListener.onClick(label, e.getButton() == MouseEvent.BUTTON1 ? MouseButton.LEFT : MouseButton.RIGHT);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    public interface ButtonClickListener {

        void onClick(Text label, MouseButton button);

    }

}
