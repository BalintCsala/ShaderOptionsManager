package me.balintcsala.ui.editor;

import me.balintcsala.Utils;
import me.balintcsala.data.lang.Language;
import me.balintcsala.data.options.Option;
import me.balintcsala.data.options.Screen;
import me.balintcsala.data.options.ShaderProperties;
import me.balintcsala.ui.components.Button;
import me.balintcsala.ui.components.Slider;
import me.balintcsala.ui.components.Text;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Content extends JPanel {

    public static final int PIXEL_SCALE = 4;
    public static Font font;

    private BufferedImage image;
    private final ShaderProperties shaderProperties;
    private final HashMap<String, Language> languages;

    private Screen currentScreen;
    private final LinkedList<Screen> screenStack = new LinkedList<>();
    private int currentLanguageIndex;
    private Language currentLanguage;

    private final Text title;
    private final JPanel buttons;

    private Button doneButton;

    public Content(ShaderProperties shaderProperties, HashMap<String, Language> languages, String shaderpackName) {
        this.shaderProperties = shaderProperties;
        this.languages = languages;
        this.currentScreen = shaderProperties.getScreen("MAIN");

        if (languages.containsKey("en_US")) {
            currentLanguage = languages.get("en_US");
            currentLanguageIndex = new ArrayList<>(languages.keySet()).indexOf("en_US");
        } else {
            currentLanguage = (Language) languages.values().toArray()[0];
            currentLanguageIndex = 0;
        }

        try {
            image = ImageIO.read(getClass().getResource("/options_background.png"));
            if (font == null) {
                font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/font.otf"))
                        .deriveFont(Font.PLAIN, 20);
            }
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        setBorder(new EmptyBorder(24, 0, 48, 0));
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(800, 40));
        header.setBorder(new EmptyBorder(0, 4, 0, 4));

        title = new Text("", 800, 40);
        header.add(title, BorderLayout.CENTER);

        JButton langButton = new JButton();
        langButton.setSize(40, 40);
        langButton.setBorderPainted(false);
        langButton.setPreferredSize(new Dimension(40, 40));
        langButton.setIcon(new ImageIcon(getClass().getResource("/lang_button.png")));
        langButton.setPressedIcon(new ImageIcon(getClass().getResource("/lang_button_highlight.png")));
        langButton.setRolloverIcon(new ImageIcon(getClass().getResource("/lang_button_highlight.png")));
        langButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // Left click
                    currentLanguageIndex++;
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    // Right click
                    currentLanguageIndex--;
                }
                currentLanguageIndex = (currentLanguageIndex % languages.keySet().size() + languages.keySet().size()) % languages.keySet().size();
                currentLanguage = (Language) languages.values().toArray()[currentLanguageIndex];
                populate();
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
        header.add(langButton, BorderLayout.LINE_END);
        add(header, BorderLayout.PAGE_START);

        JPanel footer = new JPanel();
        footer.setLayout(new GridLayout(1, 2, 16, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 4, 0, 4));
        footer.add(new Button("Reset", (label, button) -> {
            shaderProperties.reset();
            populate();
        }));

        doneButton = new Button("Generate", (label, button) -> {
            if (screenStack.isEmpty()) {
                shaderProperties.save();

                Path target = Paths.get(Utils.getMinecraftPath().toString(), "shaderpacks", "modified_" + shaderpackName);
                if (target.toFile().exists())
                    Utils.deleteDirectory(target.toFile());
                Utils.copyDirectory(Paths.get("tmp"), target);

            } else {
                currentScreen = screenStack.pop();
                doneButton.setLabelText(screenStack.isEmpty() ? "Generate" : "Back");
                populate();
            }
        });
        footer.add(doneButton);
        add(footer, BorderLayout.PAGE_END);

        buttons = new JPanel();
        buttons.setOpaque(false);
        add(buttons, BorderLayout.CENTER);

        populate();
    }

    private void populate() {
        title.updateText("MAIN".equals(currentScreen.getName()) ? "Shader Options" : currentLanguage.getScreenName(currentScreen.getName()));

        buttons.removeAll();
        JPanel row = null;
        for (int i = 0; i < currentScreen.getEntries().size(); i++) {
            if (i % 2 == 0) {
                row = new JPanel();
                row.setLayout(new GridLayout(1, 2, 16, 0));
                row.setAlignmentX(LEFT_ALIGNMENT);
                row.setOpaque(false);
            }
            Screen.Entry entry = currentScreen.getEntries().get(i);
            switch (entry.type) {
                case LINK:
                    row.add(new Button(currentLanguage.getScreenName(entry.name) + "...", (label, button) -> {
                        screenStack.push(currentScreen);
                        doneButton.setLabelText("Back");
                        currentScreen = shaderProperties.getScreen(entry.name);
                        populate();
                    }));
                    break;
                case OPTION:
                    Option option = shaderProperties.getOption(entry.name);
                    if (shaderProperties.isSlider(entry.name)) {
                        row.add(new Slider(currentLanguage.getOptionName(entry.name), entry.name, option.values, option.values.indexOf(option.getCurrentValue()), option::setValue, (id, value) -> currentLanguage.getValueName(id, value)));
                    } else {
                        row.add(new Button(currentLanguage.getOptionName(entry.name) + ": " + currentLanguage.getValueName(entry.name, option.getCurrentValue()), (label, button) -> {
                            if (button == Button.MouseButton.LEFT) {
                                option.nextValue();
                            } else {
                                option.previousValue();
                            }
                            label.updateText(currentLanguage.getOptionName(entry.name) + ": " + currentLanguage.getValueName(entry.name, option.getCurrentValue()));
                        }));
                    }
                    break;
                case EMPTY:
                    JPanel empty = new JPanel();
                    empty.setPreferredSize(new Dimension(380, 40));
                    empty.setOpaque(false);
                    row.add(empty);
                    break;
                case PROFILE:
                    break;
            }

            if (i % 2 == 1) {
                buttons.add(row);
                row = null;
            }
        }
        if (row != null) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            row.add(empty);
            buttons.add(row);
        }

        revalidate();

        repaint();

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
