package com.example.usb_seria_testing

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.usb_seria_testing.databinding.ActivityMainBinding
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import javax.net.ssl.SSLSessionBindingEvent

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding
    lateinit var manager: UsbManager
    lateinit var device: UsbDevice

    lateinit var epOut: UsbEndpoint
    lateinit var epIn: UsbEndpoint

    lateinit var serial: UsbSerialDevice
    var str: String = ""
    var count = 0

    private val ACTION_USB_PERMISSION: String = "com.example.usb_seria_testing.USB_PERMISSION"

    private var mCallback: UsbSerialInterface.UsbReadCallback = UsbSerialInterface.UsbReadCallback { arg0 ->
        str = str + String(arg0)
        binding.textView1var = "string recived: $str"
        //Toast.makeText(applicationContext, "git", Toast.LENGTH_SHORT).show()
        count = 0

        for(i in arg0){
            if(i >0){
                count++
            }

        }
        binding.textView4var = "read: nr of 1: $count,   ${arg0.size}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.textView1var="aa"
        binding.textView2var="Device names: "
        binding.textView3var="Interfaces: "

        binding.button1.setOnClickListener(this)

        manager = getSystemService(Context.USB_SERVICE) as UsbManager

        device_search()


        device = manager.deviceList["/dev/bus/usb/001/010"]!!  //Tu trzeba znalesc odpowiednia nazwe urzadzenia
                                                            //docelowo powinno byc automatycznaie narazie recznie


        //Sekcja wywolania pozwolen na dostep
        if (!manager.hasPermission(device)){
            val permissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
            manager.requestPermission(device, permissionIntent)
        }

        //native_method()
        serial_method()

    }

    override fun onClick(v: View?) {
        if(v != null){
            if(v.id == binding.button1.id){
                serial.write("bga".toByteArray())
            }
        }
    }

    private fun serial_method() { //https://github.com/felHR85/UsbSerial
        val connection: UsbDeviceConnection = manager.openDevice(device)

        serial = UsbSerialDevice.createUsbSerialDevice(device, connection)

        if(serial == null){
            Toast.makeText(applicationContext, "device driver is null", Toast.LENGTH_SHORT).show()
        }

        serial.open()
        serial.setBaudRate(115200)
        serial.setDataBits(UsbSerialInterface.DATA_BITS_8)
        serial.setParity(UsbSerialInterface.PARITY_ODD)
        serial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)

        serial.read(mCallback)
        //serial.close()
    }


    private fun native_method() {
        binding.textView3var = binding.textView3var + device.interfaceCount + "  Endpoints "

        for(i in 0 until device.interfaceCount){
            binding.textView3var= binding.textView3var + " $i| ${device.getInterface(i).endpointCount},  "
        }


        if(manager.openDevice(device) != null){
            val connection: UsbDeviceConnection = manager.openDevice(device) //Utworzenie polaczenia z urzadzeniem

            connection.claimInterface(device.getInterface(1), true) //wybranie interfaceu

            connection.controlTransfer(0x40, 0, 0, 0, null, 0, 0) //Clear Rx
            connection.controlTransfer(0x40, 0, 2, 0, null, 0, 0) //Clear TX
            connection.controlTransfer(0x40, 0x02, 0x0000, 0, null, 0, 0) //flow control none
            connection.controlTransfer(0x40, 0x03, 0x001A, 0, null, 0, 0)  //TO DO 0x0034 is 57600 switch to 115200 is 0x001A
            connection.controlTransfer(0x40, 0x04, 0x0008, 0, null, 0, 0) //data bit 8, parity none, stop bit 1, tx off

            Toast.makeText(applicationContext, "Dir 0 = ${device.getInterface(1).getEndpoint(0).direction},   1 = ${device.getInterface(1).getEndpoint(1).direction}", Toast.LENGTH_SHORT).show()

            epOut = device.getInterface(1).getEndpoint(0)
            epIn = device.getInterface(1).getEndpoint(1)

/*          //TO check
            for(i in 0 until device.getInterface(1).endpointCount){
                if(device.getInterface(1).getEndpoint(i).direction == 0){
                    epOut = device.getInterface(1).getEndpoint(i)
                } else if(device.getInterface(1).getEndpoint(i).direction == 128){
                    var epIn = device.getInterface(1).getEndpoint(i)
                }
            }
*/
            var by = "a".toByteArray()
            var anta = connection.bulkTransfer(epOut, by, by.size, 500)
            var buff: ByteArray = ByteArray(4096)
            var str: String = String()

            var ant = connection.bulkTransfer(epIn, buff, 4096, 500)
            if ( ant >= 0) {
                str = String(buff)
            }

            binding.textView1var = "string recived: $str"

            binding.textView4var = "Send status: $anta, Recive status: $ant"
        }
    }

    private fun device_search() {
        val deviceList = manager.deviceList

        deviceList.forEach { device ->
            Toast.makeText(applicationContext, "name: ${device.value.deviceName}, vid: ${device.value.vendorId} id: ${device.value.deviceId}, class: ${device.value.deviceClass}, subclass: ${device.value.deviceSubclass}", Toast.LENGTH_LONG).show()
            binding.textView2var = binding.textView2var + " ${device.value.deviceName},"
        }
    }

}