package view;

import com.fazecast.jSerialComm.SerialPort;
import util.Constants;

import javax.swing.*;
import java.util.Objects;

public class DataSetup extends JFrame {

    private static final String windowName = "Data Setup";
    private static final int windowWidth = 800;
    private static final int windowHeight = 400;

    private JPanel dataSetupJPanel;
    private JButton setPathButton;
    private JLabel pathSelected;
    private JComboBox<String> samplesComboBox;
    private JComboBox<String> serialPortComboBox;
    private JButton serialMonitorButton;
    private JButton nextButton;
    private JFileChooser jFileChooser;

    public DataSetup() {
        bindFrame();
        setUpComponents();

        setPathButton.addActionListener(e -> {
            jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
            jFileChooser.showOpenDialog(this);
            pathSelected.setText(jFileChooser.getSelectedFile().toString());
        });

        serialMonitorButton.addActionListener(e ->
                new SerialMonitorDialog(SerialPort.getCommPort(Objects.requireNonNull(serialPortComboBox.getSelectedItem()).toString())).setVisible(true));

        nextButton.addActionListener(e ->
                new DataRead(
                        Objects.requireNonNull(samplesComboBox.getSelectedItem()).toString(),
                        pathSelected.getText(),
                        SerialPort.getCommPort(Objects.requireNonNull(serialPortComboBox.getSelectedItem()).toString())
                ).setVisible(true));
    }

    private void bindFrame() {
        setTitle(windowName);
        setContentPane(dataSetupJPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void setUpComponents() {
        samplesComboBox.setModel(new DefaultComboBoxModel<>(Constants.Companion.getNumberOfSamples()));
        getSerialPorts();
    }

    private void getSerialPorts() {
        for (SerialPort port : SerialPort.getCommPorts()) {
            serialPortComboBox.addItem(port.getSystemPortName());
        }
    }

}
