package util

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import java.util.*
import javax.swing.JTextArea

class SerialPortListener(
    private val outData: JTextArea,
    private val scannerDataInput: Scanner
) : SerialPortDataListener {

    override fun getListeningEvents(): Int {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE
    }

    override fun serialEvent(serialPortEvent: SerialPortEvent?) {
        if (serialPortEvent?.eventType != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return
        outData.append("${scannerDataInput.nextLine()} \n")
    }
}