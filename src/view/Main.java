package view;

import javax.swing.*;

public class Main extends JFrame {
    private static final String windowName = "Spectrum Alcohol";
    private static final int windowWidth = 700;
    private static final int windowHeight = 600;
    private JPanel mainJPanel;
    private JButton startCalibrationProcessButton;

    public Main() {
        bindFrame();
        startCalibrationProcessButton.addActionListener(e -> {
            this.dispose();
            new DataSetup().setVisible(true);
        });
    }

    public static void main(String[] args) {
        new Main().setVisible(true);
    }

    private void bindFrame() {
        setTitle(windowName);
        setContentPane(mainJPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);
        setResizable(false);
    }

}
