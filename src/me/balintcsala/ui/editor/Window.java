package me.balintcsala.ui.editor;

import me.balintcsala.data.lang.Language;
import me.balintcsala.data.options.ShaderProperties;

import javax.swing.*;
import java.util.HashMap;

public class Window extends JFrame {

    public Window(ShaderProperties shaderProperties, HashMap<String, Language> languages, String shaderpackName) {
        setTitle("Shader Options Manager");
        setSize(800, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage());

        setContentPane(new Content(shaderProperties, languages, shaderpackName));

        setVisible(true);
        repaint();
    }

}
