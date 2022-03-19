package view;

import com.fazecast.jSerialComm.SerialPort;
import util.Constants;
import util.SerialPortExtensionKt;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.*;
import java.util.Locale;

public class DataReading extends JFrame {

    private static final String windowName = Constants.titleDataRead;
    private static final int windowWidth = 800;
    private static final int windowHeight = 400;

    private int dataReading = 1;
    private int maxDataReading = 1;
    private boolean referenceData;
    private boolean secondConcentration;
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
    private JScrollPane dataScrollPane;

    public DataReading(String reads, String path, SerialPort port) {
        bindFrame();
        setSerialPort(port);
        setUpDetails(reads, path, port.getSystemPortName());
        setUpScripts(path);
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

        startRecordButton.addActionListener(e -> setStartRecordingButtonUI());

        stopAndSaveButton.addActionListener(e -> {
            setStopAndSaveButtonUI();
            runSave();
        });
    }

    private void setUpDetails(String reads, String path, String port) {
        setMaxDataReading(Integer.parseInt(reads));
        numberOfReads.setText(dataReading + " of " + reads);
        pathName.setText(path);
        serialPortName.setText(port);
        statusLabel.setForeground(Color.DARK_GRAY);
        statusOfSaveFile.setVisible(false);
        statusOfSaveFile.setForeground(Color.decode(Constants.darkModerateLimeGreenColor));
    }

    private void setUpScripts(String path) {
        Constants.Companion.setScriptsPath(path);
    }

    private void bindFrame() {
        setTitle(windowName);
        setContentPane(mainJPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void showDataFromSerialPort(SerialPort port) {
        SerialPortExtensionKt.setDataListenerForSerialPort(port, dataTextArea);
    }

    private void updateDataReadLabel(int dataRead) {
        if (!(dataRead > maxDataReading)) {
            numberOfReads.setText(dataRead + " of " + getMaxDataReading());
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
        int numFileSaved = dataReading;
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

    private void setStartRecordingButtonUI() {
        getConcentration();
        dataTextArea.setBackground(Color.PINK);
        stopAndSaveButton.setEnabled(true);
        startRecordButton.setEnabled(false);
        statusLabel.setEnabled(true);
        statusOfSaveFile.setVisible(false);
        statusLabel.setText(Constants.recordingText);
        statusLabel.setForeground(Color.RED);
    }

    private void runSave() {
        if (saveFile(setPrefixFileName(referenceData))) {
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

    private boolean saveFile(String prefix) {
        String fileName;
        if (prefix.equals(Constants.prefixRefSample)) {
            fileName = prefix;
        } else {
            fileName = prefix + dataReading;
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathName.getText() + "/" + fileName + ".txt"));
            dataTextArea.write(bufferedWriter);
            updateDataReadLabel(++dataReading);
        } catch (IOException e) {
            showDialog(Constants.titleErrorDialog, e.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String setPrefixFileName(Boolean isReferenceData) {
        if (isReferenceData) {
            return Constants.prefixRefSample;
        } else {
            return Constants.prefixConcentrationSample;
        }
    }

    private boolean checkDataRecordComplete() {
        return dataReading > maxDataReading;
    }

    private void showDialog(String title, String text, int messageType) {
        JOptionPane.showMessageDialog(this, text, title, messageType);
    }

    public int getMaxDataReading() {
        return maxDataReading;
    }

    public void setMaxDataReading(int maxDataReading) {
        this.maxDataReading = maxDataReading;
    }

    private void runPythonScript() {
        try {
            Runtime run = Runtime.getRuntime();
            String command = Constants.pythonCommand + Constants.Companion.getScriptsPath() + getConcentrationScript();
            System.out.println(command);
            run.exec(command);
        } catch (IOException e) {
            showDialog(Constants.titleErrorDialog, e.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String getConcentrationScript() {
        if (Constants.Companion.isWindowsWorkstation())
            return Constants.WIN_CONCENTRATION_SCRIPT;
        return Constants.MAC_CONCENTRATION_SCRIPT;
    }

    private void getConcentration() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathName.getText() + "/" + Constants.prefixConcentration + ".txt", true));
            if (secondConcentration) {
                bufferedWriter.newLine();
            }
            bufferedWriter.append(showConcentrationInputDialog().trim());
            bufferedWriter.close();
            secondConcentration = true;
        } catch (IOException e) {
            showDialog(Constants.titleErrorDialog, e.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String showConcentrationInputDialog() {
        String output = JOptionPane.showInputDialog(
                this,
                Constants.textConcentrationDialog,
                Constants.titleConcentrationDialog,
                JOptionPane.INFORMATION_MESSAGE);
        if (output.equals("null")) {
            return "";
        } else {
            return output;
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainJPanel = new JPanel();
        mainJPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(mainJPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainJPanel.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, -1, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Details");
        panel2.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel2.add(separator1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        numberOfReads = new JLabel();
        Font numberOfReadsFont = this.$$$getFont$$$(null, Font.BOLD, -1, numberOfReads.getFont());
        if (numberOfReadsFont != null) numberOfReads.setFont(numberOfReadsFont);
        numberOfReads.setText("1 of 5");
        panel3.add(numberOfReads, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathName = new JLabel();
        pathName.setText("C:\\...");
        panel3.add(pathName, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        serialPortName = new JLabel();
        serialPortName.setText("COM:...");
        panel3.add(serialPortName, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, -1, -1, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Reading status:");
        panel4.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Path:");
        panel4.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Port:");
        panel4.add(label4, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainJPanel.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dataScrollPane = new JScrollPane();
        panel5.add(dataScrollPane, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        dataTextArea = new JTextArea();
        dataTextArea.setEditable(false);
        dataTextArea.setLineWrap(false);
        dataScrollPane.setViewportView(dataTextArea);
        clearButton = new JButton();
        clearButton.setText("Clear");
        panel5.add(clearButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        serialMonitorCheckBox = new JCheckBox();
        serialMonitorCheckBox.setText("Show serial monitor");
        panel5.add(serialMonitorCheckBox, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainJPanel.add(panel6, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        startRecordButton = new JButton();
        startRecordButton.setText("Start record");
        panel6.add(startRecordButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 34), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel6.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        stopAndSaveButton = new JButton();
        stopAndSaveButton.setEnabled(false);
        stopAndSaveButton.setText("Stop and Save");
        panel6.add(stopAndSaveButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 34), null, 0, false));
        final JSeparator separator2 = new JSeparator();
        mainJPanel.add(separator2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainJPanel.add(panel7, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statusLabel = new JLabel();
        statusLabel.setEnabled(false);
        Font statusLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, statusLabel.getFont());
        if (statusLabelFont != null) statusLabel.setFont(statusLabelFont);
        statusLabel.setText("Waiting for recording");
        panel7.add(statusLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statusOfSaveFile = new JLabel();
        statusOfSaveFile.setEnabled(true);
        Font statusOfSaveFileFont = this.$$$getFont$$$(null, Font.BOLD, -1, statusOfSaveFile.getFont());
        if (statusOfSaveFileFont != null) statusOfSaveFile.setFont(statusOfSaveFileFont);
        statusOfSaveFile.setText("...");
        panel7.add(statusOfSaveFile, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

}