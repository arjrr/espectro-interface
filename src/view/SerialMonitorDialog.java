package view;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.swing.*;
import java.awt.event.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SerialMonitorDialog extends JDialog {

    private static final String windowName = "Serial Monitor";
    private static final int windowWidth = 600;
    private static final int windowHeight = 400;

    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea serialMonitorTextArea;

    public SerialMonitorDialog() {
    }

    public SerialMonitorDialog(SerialPort serialPort) {
        bindDialog();
        setUpComponents();
        showDataFromSerialPort(serialPort);

        buttonOK.addActionListener(e -> onOK());
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        // dispose on ESCAPE
        contentPane.registerKeyboardAction(e ->
                        dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    public static void main(String[] args) {
        new SerialMonitorDialog();
    }

    private void bindDialog() {
        setTitle(windowName);
        setModal(true);
        setContentPane(contentPane);
        setSize(windowWidth, windowHeight);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void setUpComponents() {
        getRootPane().setDefaultButton(buttonOK);
    }

    private void showDataFromSerialPort(SerialPort port) {
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        port.openPort();
        Scanner scannerDataInput = new Scanner(port.getInputStream());
        port.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                String data = scannerDataInput.nextLine();
                System.out.println(data);
                serialMonitorTextArea.append(data + "\n");
            }
        });
    }

    private void showStringDataSplitOnTextArea(String data, String regex, int limit, JTextArea jTextArea) {
        String[] dataStrings = data.split(regex, limit);
        System.out.println(dataStrings[0]);
        System.out.println(dataStrings[1]);
        for (String dataString : dataStrings) {
            jTextArea.append(dataString + "\n");
        }
    }

}
