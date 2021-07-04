package view;

import util.Constants;

import javax.swing.*;
import javax.swing.plaf.multi.MultiLabelUI;
import java.io.IOException;

public class Main extends JFrame {

    private static final String windowName = Constants.titleSoftware;
    private static final int windowWidth = 700;
    private static final int windowHeight = 600;
    private JPanel mainJPanel;
    private JButton startCalibrationProcessButton;
    private JButton getConcentrationByTheButton;
    private JTextArea buttonDescription;

    public Main() {
        bindFrame();
        setUpComponents();
        setActionListeners();
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

    private void setUpComponents() {
        buttonDescription.setWrapStyleWord(true);
        buttonDescription.setLineWrap(true);
        buttonDescription.setEditable(false);
        buttonDescription.setBackground(mainJPanel.getBackground());
        buttonDescription.setText(Constants.textButtonDescription);
    }

    private void setActionListeners() {
        startCalibrationProcessButton.addActionListener(e -> new DataSetup().setVisible(true));
        getConcentrationByTheButton.addActionListener(e ->
        {
            showDialog(Constants.titleGetConcentrationDialog, Constants.textGetConcentrationDialog, JOptionPane.WARNING_MESSAGE);
            runPythonScript();
        });
    }

    private void runPythonScript() {
        try {
            Runtime run = Runtime.getRuntime();
            run.exec(Constants.SAMPLE_SCRIPT);
        } catch (IOException e) {
            showDialog(Constants.titleErrorDialog, e.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showDialog(String title, String text, int messageType) {
        JOptionPane.showMessageDialog(this, text, title, messageType);
    }

}