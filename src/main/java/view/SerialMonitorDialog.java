package view;

import com.fazecast.jSerialComm.SerialPort;
import util.SerialPortExtensionKt;

import javax.swing.*;
import java.awt.event.*;

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

    public SerialPort getPort() {
        return port;
    }

    public void setPort(SerialPort port) {
        this.port = port;
    }
}
