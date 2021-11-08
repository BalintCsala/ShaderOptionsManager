package me.balintcsala.ui.components;

import me.balintcsala.data.lang.Language;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class Slider extends JPanel {

    public interface ValueCallback {

        public String getValueName(String id, String value);

    }

    private static final int HANDLE_WIDTH = 16;

    private static Image background;
    private static Image handle;
    private static Image handleHighlight;

    private final String name;
    private final String valueId;
    private final ArrayList<String> values;
    private int selected;
    private final SliderChangeListener changeListener;
    private final ValueCallback valueCallback;

    private final Text label;
    private boolean hover = false;
    private boolean dragging = false;

    public Slider(String name, String valueId, ArrayList<String> values, int selected, SliderChangeListener changeListener, ValueCallback valueCallback) {
        this.name = name;
        this.valueId = valueId;
        this.values = values;
        this.selected = selected;
        this.changeListener = changeListener;
        this.valueCallback = valueCallback;

        if (background == null) {
            background = new ImageIcon(getClass().getResource("/slider_background.png")).getImage();
            handle = new ImageIcon(getClass().getResource("/slider_handle.png")).getImage();
            handleHighlight = new ImageIcon(getClass().getResource("/slider_handle_highlight.png")).getImage();
        }

        setSize(380, 40);
        setPreferredSize(new Dimension(380, 40));
        setLayout(new GridBagLayout());
        label = new Text(name + ": " + valueCallback.getValueName(valueId, values.get(selected)), 340, 20);
        add(label);

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 1) {
                    dragging = true;
                    handleMouse(e.getX());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 1) {
                    dragging = false;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                dragging = false;
                hover = false;
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    handleMouse(e.getX());
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
    }

    private void handleMouse(int x) {
        selected = (int) Math.min(Math.max(Math.round(x / (380.0 / (values.size() - 1))), 0), values.size() - 1);
        label.updateText(name + ": " + valueCallback.getValueName(valueId, values.get(selected)));
        changeListener.onClick(values.get(selected));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(background, 0, 0, this);
        g.drawImage(hover ? handleHighlight : handle, (int) ((380 - HANDLE_WIDTH) * ((double) selected / (values.size() - 1))), 0, this);
    }

    public interface SliderChangeListener {

        void onClick(String newValue);

    }
}
