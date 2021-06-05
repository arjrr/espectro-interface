package view;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import util.SerialPortExtensionKt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class DataRead extends JFrame {

    private static final String windowName = "Data Read";
    private static final int windowWidth = 800;
    private static final int windowHeight = 800;

    private int dataRead = 1;
    private int maxDataRead = 1;
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

    public DataRead(String reads, String path, SerialPort port) {
        bindFrame();
        setSerialPort(port);
        setUpDetails(reads, path, port.getSystemPortName());

        clearButton.addActionListener(e -> {
            dataTextArea.selectAll();
            dataTextArea.replaceSelection("");
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

        startRecordButton.addActionListener(e -> {
            dataTextArea.setBackground(Color.PINK);
            statusLabel.setVisible(true);
            statusLabel.setText("Recording...");
            statusLabel.setForeground(Color.RED);
        });

        stopAndSaveButton.addActionListener(e -> {
            dataTextArea.setBackground(Color.WHITE);
            statusLabel.setVisible(false);
            saveFile("c");
        });
    }

    private void setUpDetails(String reads, String path, String port) {
        setMaxDataRead(Integer.parseInt(reads));
        numberOfReads.setText(dataRead + " of " + reads);
        pathName.setText(path);
        serialPortName.setText(port);
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

    private void saveFile(String prefix) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathName.getText() + "/" + prefix + dataRead + ".txt"));
            dataTextArea.write(bufferedWriter);
            updateDataReadLabel(++dataRead);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateDataReadLabel(int dataRead) {
        numberOfReads.setText(dataRead + " of " + getMaxDataRead());
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public int getMaxDataRead() {
        return maxDataRead;
    }

    public void setMaxDataRead(int maxDataRead) {
        this.maxDataRead = maxDataRead;
    }
}
