package view;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import util.SerialPortExtensionKt;

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

    private SerialPort port;

    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea serialMonitorTextArea;

    public SerialMonitorDialog(SerialPort serialPort) {
        bindDialog();
        setUpComponents();
        showDataFromSerialPort(serialPort);

        buttonOK.addActionListener(e -> onOK());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        contentPane.registerKeyboardAction(e ->
                        onOK(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        getPort().closePort();
        dispose();
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
        SerialPortExtensionKt.setDataListenerForSerialPort(port, serialMonitorTextArea);
        setPort(port);
    }

    private void showStringDataSplitOnTextArea(String data, String regex, int limit, JTextArea jTextArea) {
        String[] dataStrings = data.split(regex, limit);
        System.out.println(dataStrings[0]);
        System.out.println(dataStrings[1]);
        for (String dataString : dataStrings) {
            jTextArea.append(dataString + "\n");
        }
    }

    public SerialPort getPort() {
        return port;
    }

    public void setPort(SerialPort port) {
        this.port = port;
    }
}
