package util

import com.fazecast.jSerialComm.SerialPort
import java.util.*
import javax.swing.JTextArea

fun SerialPort.setDataListenerForSerialPort(outData: JTextArea) {
    this.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0)
    this.openPort()
    val scannerDataInput = Scanner(this.inputStream)
    this.addDataListener(SerialPortListener(outData, scannerDataInput))
}