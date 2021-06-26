package view;

import com.fazecast.jSerialComm.SerialPort;
import util.Constants;
import util.SerialPortExtensionKt;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class DataRead extends JFrame {

    private static final String windowName = Constants.titleDataRead;
    private static final int windowWidth = 800;
    private static final int windowHeight = 800;

    private int dataRead = 1;
    private int maxDataRead = 1;
    private boolean referenceData;
    private SerialPort serialPort;

    private JPanel mainJPanel;
    private JTextArea dataTextArea;
    private JButton startRecordButton;
    private JButton stopAndSaveButton;
    private JButton clearButton;
    private JLabel numberOfReads;
    private JLabel pathName;
    private JLabel serialPortName;
    private JCheckBox serialMonitorCheckBox;
    private JLabel statusLabel;
    private JLabel statusOfSaveFile;

    public DataRead(String reads, String path, SerialPort port) {
        bindFrame();
        setSerialPort(port);
        setUpDetails(reads, path, port.getSystemPortName());
        setActionListeners();
    }

    private void setActionListeners() {
        clearButton.addActionListener(e -> {
            dataTextArea.selectAll();
            dataTextArea.replaceSelection(Constants.emptyString);
        });

        serialMonitorCheckBox.addItemListener(e -> {
            if (serialMonitorCheckBox.isSelected()) {
                serialMonitorCheckBox.setSelected(true);
                showDataFromSerialPort(getSerialPort());
            } else {
                serialMonitorCheckBox.setSelected(false);
                getSerialPort().closePort();
            }
        });

        startRecordButton.addActionListener(e -> setStartRecordButtonUI());

        stopAndSaveButton.addActionListener(e -> {
            setStopAndSaveButtonUI();
            runSave();
        });
    }

    private void setUpDetails(String reads, String path, String port) {
        setMaxDataRead(Integer.parseInt(reads));
        numberOfReads.setText(dataRead + " of " + reads);
        pathName.setText(path);
        serialPortName.setText(port);
        statusLabel.setForeground(Color.DARK_GRAY);
        statusOfSaveFile.setVisible(false);
        statusOfSaveFile.setForeground(Color.decode(Constants.darkModerateLimeGreenColor));
    }

    private void bindFrame() {
        setTitle(windowName);
        setContentPane(mainJPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void showDataFromSerialPort(SerialPort port) {
        SerialPortExtensionKt.setDataListenerForSerialPort(port, dataTextArea);
    }

    private boolean saveFile(String prefix) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathName.getText() + "/" + prefix + dataRead + ".txt"));
            dataTextArea.write(bufferedWriter);
            updateDataReadLabel(++dataRead);
        } catch (IOException e) {
            showDialog(Constants.titleErrorDialog, e.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void updateDataReadLabel(int dataRead) {
        if (!(dataRead > maxDataRead)) {
            numberOfReads.setText(dataRead + " of " + getMaxDataRead());
        }
        if (referenceData) {
            numberOfReads.setText(Constants.textComplete);
        }
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    private void setDataRecordedUI() {
        int numFileSaved = dataRead;
        statusOfSaveFile.setText("File " + --numFileSaved + " saved");
        statusOfSaveFile.setVisible(true);
        showDialog(Constants.titleSuccessDialog, Constants.textDataRecordedDialog, JOptionPane.INFORMATION_MESSAGE);
    }

    private void setReferenceDataRecordUI() {
        dataTextArea.setBackground(Color.decode(Constants.softCyanLimeGreenColor));
        numberOfReads.setForeground(Color.decode(Constants.darkModerateLimeGreenColor));
    }

    private void setStopAndSaveButtonUI() {
        dataTextArea.setBackground(Color.WHITE);
        stopAndSaveButton.setEnabled(false);
        startRecordButton.setEnabled(true);
        statusLabel.setEnabled(false);
        statusLabel.setText(Constants.waitingForRecording);
        statusLabel.setForeground(Color.DARK_GRAY);
    }

    private void setStartRecordButtonUI() {
        dataTextArea.setBackground(Color.PINK);
        stopAndSaveButton.setEnabled(true);
        startRecordButton.setEnabled(false);
        statusLabel.setEnabled(true);
        statusOfSaveFile.setVisible(false);
        statusLabel.setText(Constants.recordingText);
        statusLabel.setForeground(Color.RED);
    }

    private void runSave() {
        if (saveFile(Constants.prefixConcentrationSample)) {
            setDataRecordedUI();
            if (referenceData) {
                showDialog(Constants.titleDataPlotDialog, Constants.textDataPlotDialog, JOptionPane.INFORMATION_MESSAGE);
                runPythonScript();
                this.dispose();
            } else if (checkDataRecordComplete()) {
                setReferenceDataRecordUI();
                showDialog(Constants.titleDataReferenceDialog, Constants.textDataReferenceDialog, JOptionPane.WARNING_MESSAGE);
                referenceData = true;
            }
        }
    }

    private boolean checkDataRecordComplete() {
        return dataRead > maxDataRead;
    }

    private void showDialog(String title, String text, int messageType) {
        JOptionPane.showMessageDialog(this, text, title, messageType);
    }

    public int getMaxDataRead() {
        return maxDataRead;
    }

    public void setMaxDataRead(int maxDataRead) {
        this.maxDataRead = maxDataRead;
    }

    private void runPythonScript() {
        try {
            Runtime run = Runtime.getRuntime();
            run.exec(Constants.CONCENTRATION_SCRIPT);
        } catch (IOException e) {
            showDialog(Constants.titleErrorDialog, e.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

}