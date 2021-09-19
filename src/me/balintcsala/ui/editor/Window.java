package me.balintcsala.ui;

import javax.swing.*;

public class Window extends JFrame {

    public Window() {
        setTitle("Shader options manager");
        setSize(800, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setContentPane(new Content());

        setVisible(true);
        repaint();
    }

}
