package view;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import util.SerialPortExtensionKt;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

public class DataRead extends JFrame {

    private static final String windowName = "Data Read";
    private static final int windowWidth = 800;
    private static final int windowHeight = 800;

    private int dataRead = 1;
    private SerialPort serialPort;

    private JPanel mainJPanel;
    private JTextArea dataTextArea;
    private JButton startRecordButton;
    private JButton stopAndSaveButton;
    private JButton clearButton;
    private JLabel numberOfReads;
    private JLabel pathName;
    private JLabel serialPortName;
    private JCheckBox showDataInputCheckBox;

    public DataRead() {
        stopAndSaveButton.addActionListener(e -> dataRead++);

        showDataInputCheckBox.addActionListener(e -> {
            if (showDataInputCheckBox.isSelected()) {
                showDataInputCheckBox.setSelected(false);
                //getSerialPort().closePort();
            } else {
                showDataInputCheckBox.setSelected(true);
                showDataFromSerialPort(getSerialPort());
            }
        });

        startRecordButton.addActionListener(e -> showDataFromSerialPort(getSerialPort()));
    }

    public DataRead(String reads, String path, SerialPort serialPort) {
        bindFrame();
        setSerialPort(serialPort);
        showDataFromSerialPort(serialPort);
        setUpDetails(reads, path, serialPort.getSystemPortName());
    }

    public static void main(String[] args) {
        new DataRead();
    }

    private void setUpDetails(String reads, String path, String port) {
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
//        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
//        port.openPort();
//        Scanner scannerDataInput = new Scanner(port.getInputStream());
//        port.addDataListener(new SerialPortDataListener() {
//            @Override
//            public int getListeningEvents() {
//                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
//            }
//
//            @Override
//            public void serialEvent(SerialPortEvent serialPortEvent) {
//                if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
//                    return;
//                String data = scannerDataInput.nextLine();
//                System.out.println(data);
//                dataTextArea.append(data + "\n");
//            }
//        });
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }
}
