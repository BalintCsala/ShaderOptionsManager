package me.balintcsala.ui;

import javax.swing.*;
import java.awt.*;

public class ProgressBarDialog extends JDialog {

    private final JLabel descriptionLabel;
    private final JProgressBar progressBar;

    public ProgressBarDialog(String description) {
        setLayout(new BorderLayout());
        descriptionLabel = new JLabel(description);
        progressBar = new JProgressBar();
        progressBar.setMaximum(1000);

        add(descriptionLabel, BorderLayout.NORTH);
        add(progressBar, BorderLayout.CENTER);

        setSize(300, 50);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    public void setDescription(String description) {
        descriptionLabel.setText(description);
    }

    public void updateProgress(double progress) {
        progressBar.setValue((int) (1000 * progress));
    }

}
