package com.example.listviewlayout.terminal

import java.io.*
import java.net.Socket
import kotlin.math.roundToInt

class Payment {
    fun doTransaction(price: Float): Int{
        val protocolVersion = byteArrayOf(0x00, 0x02)
        val messageType = byteArrayOf(0x00, 0x02)

        val tagEftMessageNumber = byteArrayOf(0x00, 0x00, 0x02, 0x00)
        val tagEftMessageNumberLength = byteArrayOf(0x02, 0x00, 0x00, 0x00) //2
        val tagEftMessageNumberValue = byteArrayOf(0x30, 0x31) // 0 1
        val tagEftMessageNumberFull = tagEftMessageNumber + tagEftMessageNumberLength + tagEftMessageNumberValue

        val tagEftAmount1 = byteArrayOf(0x01, 0x00, 0x02, 0x00)
        val tagEftAmount1Length = byteArrayOf(0x0C, 0x00, 0x00, 0x00) //12
        val tagEftAmount1Value = priceToAscii(price)
        val tagEftAmount1Full = tagEftAmount1 + tagEftAmount1Length + tagEftAmount1Value

        val tagEftAmount1Label = byteArrayOf(0x05, 0x00, 0x02, 0x00)
        val tagEftAmount1LabelLength = byteArrayOf(0x07, 0x00, 0x00, 0x00) //7
        val tagEftAmount1LabelValue = byteArrayOf(0x41, 0x6D, 0x6F, 0x75, 0x6E, 0x74, 0x31) // A m o u n t 1
        val tagEftAmount1LabelFull = tagEftAmount1Label + tagEftAmount1LabelLength + tagEftAmount1LabelValue

        val tagEftTransactionType = byteArrayOf(0x09, 0x00, 0x02, 0x00)
        val tagEftTransactionTypeLength = byteArrayOf(0x02, 0x00, 0x00, 0x00) //12
        val tagEftTransactionTypeValue = byteArrayOf(0x30, 0x30) // 0 0
        val tagEftTransactionTypeFull = tagEftTransactionType + tagEftTransactionTypeLength + tagEftTransactionTypeValue

        val tagEftAmount2 = byteArrayOf(0x02, 0x00, 0x02, 0x00)
        val tagEftAmount2Length = byteArrayOf(0x0C, 0x00, 0x00, 0x00) //12
        val tagEftAmount2Value = byteArrayOf(0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30) // 0 0 0 0 0 0 0 0 0 0 0 0
        val tagEftAmount2Full = tagEftAmount2 + tagEftAmount2Length + tagEftAmount2Value

        val tagEftAmount3 = byteArrayOf(0x03, 0x00, 0x02, 0x00)
        val tagEftAmount3Length = byteArrayOf(0x0C, 0x00, 0x00, 0x00) //12
        val tagEftAmount3Value = byteArrayOf(0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30) // 0 0 0 0 0 0 0 0 0 0 0 0
        val tagEftAmount3Full = tagEftAmount3 + tagEftAmount3Length + tagEftAmount3Value

        val tagEftAmount4 = byteArrayOf(0x04, 0x00, 0x02, 0x00)
        val tagEftAmount4Length = byteArrayOf(0x0C, 0x00, 0x00, 0x00) //12
        val tagEftAmount4Value = byteArrayOf(0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30) // 0 0 0 0 0 0 0 0 0 0 0 0
        val tagEftAmount4Full = tagEftAmount4 + tagEftAmount4Length + tagEftAmount4Value

        val tagEftAmount2Label = byteArrayOf(0x06, 0x00, 0x02, 0x00)
        val tagEftAmount2LabelLength = byteArrayOf(0x07, 0x00, 0x00, 0x00) //7
        val tagEftAmount2LabelValue = byteArrayOf(0x41, 0x6D, 0x6F, 0x75, 0x6E, 0x74, 0x32) // A m o u n t 2
        val tagEftAmount2LabelFull = tagEftAmount2Label + tagEftAmount2LabelLength + tagEftAmount2LabelValue

        val tagEftAmount3Label = byteArrayOf(0x07, 0x00, 0x02, 0x00)
        val tagEftAmount3LabelLength = byteArrayOf(0x07, 0x00, 0x00, 0x00) //7
        val tagEftAmount3LabelValue = byteArrayOf(0x41, 0x6D, 0x6F, 0x75, 0x6E, 0x74, 0x33) // A m o u n t 3
        val tagEftAmount3LabelFull = tagEftAmount3Label + tagEftAmount3LabelLength + tagEftAmount3LabelValue

        val tagEftAmount4Label = byteArrayOf(0x08, 0x00, 0x02, 0x00)
        val tagEftAmount4LabelLength = byteArrayOf(0x07, 0x00, 0x00, 0x00) //7
        val tagEftAmount4LabelValue = byteArrayOf(0x41, 0x6D, 0x6F, 0x75, 0x6E, 0x74, 0x34) // A m o u n t 4
        val tagEftAmount4LabelFull = tagEftAmount4Label + tagEftAmount4LabelLength + tagEftAmount4LabelValue

        val tagEftCommercialCode = byteArrayOf(0x0C, 0x00, 0x02, 0x00)
        val tagEftCommercialCodeLength = byteArrayOf(0x02, 0x00, 0x00, 0x00) //2
        val tagEftCommercialCodeValue = byteArrayOf(0x20, 0x20) // SPACE SPACE
        val tagEftCommercialCodeFull = tagEftCommercialCode + tagEftCommercialCodeLength + tagEftCommercialCodeValue

        val tagEftSuspectedFraudIndicator = byteArrayOf(0x5B, 0x00, 0x02, 0x00)
        val tagEftSuspectedFraudIndicatorLength = byteArrayOf(0x01, 0x00, 0x00, 0x00) //1
        val tagEftSuspectedFraudIndicatorValue = byteArrayOf(0x00) // 0
        val tagEftSuspectedFraudIndicatorFull = tagEftSuspectedFraudIndicator + tagEftSuspectedFraudIndicatorLength + tagEftSuspectedFraudIndicatorValue

        val tagEftReturnCardRespValue = byteArrayOf(0x7D, 0x00, 0x02, 0x00)
        val tagEftReturnCardRespValueLength = byteArrayOf(0x01, 0x00, 0x00, 0x00) //1
        val tagEftReturnCardRespValueValue = byteArrayOf(0x01) // 1
        val tagEftReturnCardRespValueFull = tagEftReturnCardRespValue + tagEftReturnCardRespValueLength + tagEftReturnCardRespValueValue

        val tagEftReturnSigReqValue = byteArrayOf(0x86.toByte(), 0x00, 0x02, 0x00)
        val tagEftReturnSigReqValueLength = byteArrayOf(0x01, 0x00, 0x00, 0x00) //1
        val tagEftReturnSigReqValueValue = byteArrayOf(0x01) // 1
        val tagEftReturnSigReqValueFull = tagEftReturnSigReqValue + tagEftReturnSigReqValueLength +tagEftReturnSigReqValueValue

        val tagEftEnableEcrBlik = byteArrayOf(0x89.toByte(), 0x00, 0x02, 0x00)
        val tagEftEnableEcrBlikLength = byteArrayOf(0x01, 0x00, 0x00, 0x00) //1
        val tagEftEnableEcrBlikValue = byteArrayOf(0x01) // 1
        val tagEftEnableEcrBlikFull = tagEftEnableEcrBlik + tagEftEnableEcrBlikLength + tagEftEnableEcrBlikValue

        val tagEftEnableToken = byteArrayOf(0x8D.toByte(), 0x00, 0x02, 0x00)
        val tagEftEnableTokenLength = byteArrayOf(0x01, 0x00, 0x00, 0x00) //1
        val tagEftEnableTokenValue = byteArrayOf(0x01) // 1
        val tagEftEnableTokenFull = tagEftEnableToken + tagEftEnableTokenLength + tagEftEnableTokenValue

        val tagEftEnableReturnMarkupTextIndicator = byteArrayOf(0x9B.toByte(), 0x00, 0x02, 0x00)
        val tagEftEnableReturnMarkupTextIndicatorLength = byteArrayOf(0x01, 0x00, 0x00, 0x00) //1
        val tagEftEnableReturnMarkupTextIndicatorValue = byteArrayOf(0x01) // 1
        val tagEftEnableReturnMarkupTextIndicatorFull = tagEftEnableReturnMarkupTextIndicator + tagEftEnableReturnMarkupTextIndicatorLength + tagEftEnableReturnMarkupTextIndicatorValue

        val tagEftReturnApmData = byteArrayOf(0x9D.toByte(), 0x00, 0x02, 0x00)
        val tagEftReturnApmDataLength = byteArrayOf(0x01, 0x00, 0x00, 0x00) //1
        val tagEftReturnApmDataValue = byteArrayOf(0x01) // 1
        val tagEftReturnApmDataFull = tagEftReturnApmData + tagEftReturnApmDataLength + tagEftReturnApmDataValue

        val payload = tagEftMessageNumberFull + tagEftAmount1Full + tagEftAmount1LabelFull + tagEftTransactionTypeFull + tagEftAmount2Full + tagEftAmount3Full + tagEftAmount4Full + tagEftAmount2LabelFull +
                tagEftAmount3LabelFull + tagEftAmount4LabelFull + tagEftCommercialCodeFull + tagEftSuspectedFraudIndicatorFull + tagEftReturnCardRespValueFull + tagEftReturnSigReqValueFull +
                tagEftEnableEcrBlikFull + tagEftEnableTokenFull + tagEftEnableReturnMarkupTextIndicatorFull + tagEftReturnApmDataFull

        val message = byteArrayOf(payload.size.toByte(), 0x00, 0x00, 0x00) + protocolVersion + messageType + payload

        var response: Int = 16

        System.out.println("message size ${message.size}")
        var string: String = ""
        for(i in 0 until message.size){
            string = string +" %X".format(message[i])
        }
        System.out.println(string)
        var socket: Socket = Socket()
        var dataInputStream: DataInputStream

        try{
            socket = Socket("10.3.15.90", 5188)
        } catch(e: IOException){
            e.printStackTrace()
        }

        val dataOutputStream = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))

        dataOutputStream.write(message)
        dataOutputStream.flush()

        try{
            val byte = socket.getInputStream().readBytes()
            response = printoutResponse(byte)

        }catch(e: IOException){
            e.printStackTrace()
        }

        return response
    }

    fun priceToAscii(price: Float): ByteArray{
        var outputArray: ByteArray = byteArrayOf()

        val newPrice = (price*100.0).roundToInt() / 100.0
        println("Price: $price, newPrice: $newPrice")
        var new = newPrice.toString()

        if(new[new.length-3] != "."[0]){
            new += "0"
        }

        for(i in 0 until 13 - new.length){
            outputArray += byteArrayOf(0x30)
        }
        for(i in new.indices){
            println(new[i])
            when(new[i]){
                "0"[0] -> outputArray += byteArrayOf(0x30)
                "1"[0] -> outputArray += byteArrayOf(0x31)
                "2"[0] -> outputArray += byteArrayOf(0x32)
                "3"[0] -> outputArray += byteArrayOf(0x33)
                "4"[0] -> outputArray += byteArrayOf(0x34)
                "5"[0] -> outputArray += byteArrayOf(0x35)
                "6"[0] -> outputArray += byteArrayOf(0x36)
                "7"[0] -> outputArray += byteArrayOf(0x37)
                "8"[0] -> outputArray += byteArrayOf(0x38)
                "9"[0] -> outputArray += byteArrayOf(0x39)
                else -> {}
            }
        }

        var string= ""
        for(element in outputArray){
            string+= " %X".format(element)
        }
        return outputArray
    }

    fun asciiToInt(byte: Byte): Int{
        var output = 10
        when(byte){
            0x30.toByte() -> output = 0
            0x31.toByte() -> output = 1
            0x32.toByte() -> output = 2
            0x33.toByte() -> output = 3
            0x34.toByte() -> output = 4
            0x35.toByte() -> output = 5
            0x36.toByte() -> output = 6
            0x37.toByte() -> output = 7
            0x38.toByte() -> output = 8
            0x39.toByte() -> output = 9
            else -> {}
        }

        return output
    }

    fun printoutResponse(byte: ByteArray): Int{
        println("HeaderSize ${byte[0].toInt() + byte[1].toInt()*256 + byte[2].toInt()*65536 + byte[3].toInt()*16777216}")
        println("Protocol Version ${byte[5]}.${byte[4]}")
        println("0x" + "%02X".format(byte[7].toInt()) + "%02X".format(byte[6].toInt()))

        var i = 8
        var currentTag = 0
        var currentTagString = ""
        var tagLength = 0
        var stringResponse = ""
        var response = 16
        while(i < byte.size){
            currentTag = byte[i].toInt() + byte[i+1].toInt()*256 + byte[i+2].toInt()*65536 + byte[i+3].toInt()*16777216
            currentTagString = "0x"+ "%02X".format(byte[i+3].toInt()) + "%02X".format(byte[i+2].toInt()) + "%02X".format(byte[i+1].toInt()) + "%02X".format(byte[i].toInt())
            tagLength = byte[i+4].toInt() + byte[i+5].toInt()*256 + byte[i+6].toInt()*65536 + byte[i+7].toInt()*16777216
            stringResponse = ""
            for(j in i+8..(i+7+tagLength)){
                stringResponse += " %02X".format(byte[j].toInt())
            }
            println("currentTag ${currentTag} or ${currentTagString}, tagLength ${tagLength} , Value ${stringResponse}")

            if(currentTag == 131086){
                if( tagLength == 1){
                    response = asciiToInt(byte[i+8])
                }else if(tagLength == 2){
                    response = asciiToInt(byte[i+8])*10 + asciiToInt(byte[i+9])
                }
            }

            i += 8+tagLength
        }

        return response
    }
}