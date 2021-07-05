package view;

import util.Constants;

import javax.swing.*;
import java.io.IOException;

public class Main extends JFrame {

    private static final String windowName = Constants.titleSoftware;
    private static final int windowWidth = 700;
    private static final int windowHeight = 600;
    private JPanel mainJPanel;
    private JButton startCalibrationProcessButton;
    private JButton getConcentrationByTheButton;
    private JTextArea buttonCalibrationDescription;
    private JTextArea buttonConcentrationDescription;

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
        setCalibrationJTextArea();
        setConcentrationJTextArea();
    }

    private void setCalibrationJTextArea() {
        buttonCalibrationDescription.setWrapStyleWord(true);
        buttonCalibrationDescription.setLineWrap(true);
        buttonCalibrationDescription.setEditable(false);
        buttonCalibrationDescription.setBackground(mainJPanel.getBackground());
        buttonCalibrationDescription.setText(Constants.textCalibrationButtonDescription);
    }

    private void setConcentrationJTextArea() {
        buttonConcentrationDescription.setWrapStyleWord(true);
        buttonConcentrationDescription.setLineWrap(true);
        buttonConcentrationDescription.setEditable(false);
        buttonConcentrationDescription.setBackground(mainJPanel.getBackground());
        buttonConcentrationDescription.setText(Constants.textConcentrationButtonDescription);
    }

    private void setActionListeners() {
        startCalibrationProcessButton.addActionListener(e -> new DataSetup().setVisible(true));
        getConcentrationByTheButton.addActionListener(e ->
        {
            int dialogButton = JOptionPane.showConfirmDialog(this, Constants.textGetConcentrationDialog, Constants.titleGetConcentrationDialog, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (dialogButton == JOptionPane.NO_OPTION)
                return;
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