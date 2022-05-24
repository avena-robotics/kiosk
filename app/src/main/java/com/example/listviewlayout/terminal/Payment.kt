package com.example.listviewlayout.terminal

import kotlinx.coroutines.delay
import java.io.*
import java.net.Socket
import kotlin.math.roundToInt

class Payment {
    suspend fun startTransaction(price: Float) : Int{
        while(true){
            if(getPedState() == 0x01){
                break
            }else{
                delay(2000)
            }
        }

        return doTransaction(price)
    }

    private fun getPedState(): Int{
        val message = pedStateMessage()
        var state = 0x22
        var socket = Socket()

        try{
            socket = Socket("10.3.15.90", 5189)
        } catch(e: IOException){
            e.printStackTrace()
        }

        val dataOutputStream = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
        dataOutputStream.write(message)
        dataOutputStream.flush()

        try{
            val byte = socket.getInputStream().readBytes()
            state = printOutState(byte)
        }catch(e: IOException){
            e.printStackTrace()
        }

        return state
    }

    private fun printOutState(byte: ByteArray):Int{
        println("HeaderSize ${byte[0].toInt() + byte[1].toInt()*256 + byte[2].toInt()*65536 + byte[3].toInt()*16777216}")
        println("Protocol Version ${byte[5]}.${byte[4]}")
        println("0x" + "%02X".format(byte[7].toInt()) + "%02X".format(byte[6].toInt()))

        println("Tag: 0x"+ "%02X".format(byte[11].toInt()) + "%02X".format(byte[10].toInt()) + "%02X".format(byte[9].toInt()) + "%02X".format(byte[8].toInt()))
        println("Length: ${byte[12].toInt() + byte[13].toInt()*256 + byte[14].toInt()*65536 + byte[15].toInt()*16777216}")
        println("Value")

        val state = byte[16].toInt()

        when(state){
            0x00 -> println("State: UNKNOWN (%02X".format(state) + ")")
            0x01 -> println("State: IDLE (%02X".format(state) + ")")
            0x02 -> println("State: BUSY (%02X".format(state) + ")")
            0x03 -> println("State: CARD INSERT (%02X".format(state) + ")")
            0x04 -> println("State: PIN ENTRY FIRST ATTEMPT (%02X".format(state) + ")")
            0x05 -> println("State: PIN ENTRY SECOND ATTEMPT (%02X".format(state) + ")")
            0x06 -> println("State: PIN ENTRY THIRD ATTEMPT (%02X".format(state) + ")")
            0x07 -> println("State: PIN ENTRY FAILED (%02X".format(state) + ")")
            0x08 -> println("State: GRATUITY ENTRY (%02X".format(state) + ")")
            0x09 -> println("State: AUTHORIZING (%02X".format(state) + ")")
            0x0A -> println("State: COMPLETION (%02X".format(state) + ")")
            0x0B -> println("State: CANCELLED (%02X".format(state) + ")")
            0x0C -> println("State: AMOUNT CONFIRMATION (%02X".format(state) + ")")
            0x0D -> println("State: SENDING (%02X".format(state) + ")")
            0x0E -> println("State: RECEIVING (%02X".format(state) + ")")
            0x0F -> println("State: UNSPECIFIED INPUT (%02X".format(state) + ")")
            0x10 -> println("State: PROCESSING (%02X".format(state) + ")")
            0x11 -> println("State: CARD REMOVAL (%02X".format(state) + ")")
            0x12 -> println("State: PRINTING MERCHANT COPY (%02X".format(state) + ")")
            0x13 -> println("State: PRINTING CUSTOMER COPY (%02X".format(state) + ")")
            0x14 -> println("State: No More Paper (%02X".format(state) + ")")
            0x15 -> println("State: Loyalty Option Selection (%02X".format(state) + ")")
            0x16 -> println("State: Phone Entry (%02X".format(state) + ")")
            0x17 -> println("State: Promo Code Entry (%02X".format(state) + ")")
            0x18 -> println("State: Loyalty Member Selection (%02X".format(state) + ")")
            0x19 -> println("State: Reward Offer (%02X".format(state) + ")")
            0x1A -> println("State: existing account (%02X".format(state) + ")")
            0x1B -> println("State: Invalid Account (%02X".format(state) + ")")
            0x1C -> println("State: Link Card Payment (%02X".format(state) + ")")
            0x1D -> println("State: Add Card Payment (%02X".format(state) + ")")
            0x1E -> println("State: Cashback Entry (%02X".format(state) + ")")
            0x1F -> println("State: Commercial Code Entry (%02X".format(state) + ")")
            0x20 -> println("State: Waiting Card (%02X".format(state) + ")")
            0x21 -> println("State: Waiting Dcc Acceptance (%02X".format(state) + ")")
            else -> {}
        }

        return state
    }


    private fun pedStateMessage() : ByteArray{
        val protocolVersion = byteArrayOf(0x00, 0x02)
        val messageType = byteArrayOf(0x00, 0x1C)

        return byteArrayOf(0x00, 0x00, 0x00, 0x00) + protocolVersion + messageType
    }

    private fun doTransaction(price: Float): Int{
        val message = transactionMessage(price)
        var response = 16
        var socket = Socket()

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

    private fun transactionMessage(price: Float): ByteArray {
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
        val tagEftTransactionTypeLength = byteArrayOf(0x02, 0x00, 0x00, 0x00) //2
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
        val tagEftReturnCardRespValueValue = byteArrayOf(0x00) // 1
        val tagEftReturnCardRespValueFull = tagEftReturnCardRespValue + tagEftReturnCardRespValueLength + tagEftReturnCardRespValueValue

        val tagEftReturnSigReqValue = byteArrayOf(0x86.toByte(), 0x00, 0x02, 0x00)
        val tagEftReturnSigReqValueLength = byteArrayOf(0x01, 0x00, 0x00, 0x00) //1
        val tagEftReturnSigReqValueValue = byteArrayOf(0x01) // 1
        val tagEftReturnSigReqValueFull = tagEftReturnSigReqValue + tagEftReturnSigReqValueLength + tagEftReturnSigReqValueValue

        val tagEftEnableEcrBlik = byteArrayOf(0x89.toByte(), 0x00, 0x02, 0x00)
        val tagEftEnableEcrBlikLength = byteArrayOf(0x01, 0x00, 0x00, 0x00) //1
        val tagEftEnableEcrBlikValue = byteArrayOf(0x01) // 1
        val tagEftEnableEcrBlikFull = tagEftEnableEcrBlik + tagEftEnableEcrBlikLength + tagEftEnableEcrBlikValue

        val tagEftEnableToken = byteArrayOf(0x8D.toByte(), 0x00, 0x02, 0x00)
        val tagEftEnableTokenLength = byteArrayOf(0x01, 0x00, 0x00, 0x00) //1
        val tagEftEnableTokenValue = byteArrayOf(0x00) // 1
        val tagEftEnableTokenFull = tagEftEnableToken + tagEftEnableTokenLength + tagEftEnableTokenValue

        val tagEftEnableReturnMarkupTextIndicator = byteArrayOf(0x9B.toByte(), 0x00, 0x02, 0x00)
        val tagEftEnableReturnMarkupTextIndicatorLength = byteArrayOf(0x01, 0x00, 0x00, 0x00) //1
        val tagEftEnableReturnMarkupTextIndicatorValue = byteArrayOf(0x00) // 1
        val tagEftEnableReturnMarkupTextIndicatorFull = tagEftEnableReturnMarkupTextIndicator + tagEftEnableReturnMarkupTextIndicatorLength + tagEftEnableReturnMarkupTextIndicatorValue

        val tagEftReturnApmData = byteArrayOf(0x9D.toByte(), 0x00, 0x02, 0x00)
        val tagEftReturnApmDataLength = byteArrayOf(0x01, 0x00, 0x00, 0x00) //1
        val tagEftReturnApmDataValue = byteArrayOf(0x00) // 1
        val tagEftReturnApmDataFull = tagEftReturnApmData + tagEftReturnApmDataLength + tagEftReturnApmDataValue

        val payload = tagEftMessageNumberFull + tagEftAmount1Full + tagEftAmount1LabelFull + tagEftTransactionTypeFull + tagEftAmount2Full + tagEftAmount3Full + tagEftAmount4Full + tagEftAmount2LabelFull +
                tagEftAmount3LabelFull + tagEftAmount4LabelFull + tagEftCommercialCodeFull + tagEftSuspectedFraudIndicatorFull + tagEftReturnCardRespValueFull + tagEftReturnSigReqValueFull +
                    tagEftEnableEcrBlikFull + tagEftEnableTokenFull + tagEftEnableReturnMarkupTextIndicatorFull + tagEftReturnApmDataFull

        val header = byteArrayOf(payload.size.toByte(), 0x00, 0x00, 0x00)
        val output = header + protocolVersion + messageType + payload

        printoutResponse(output)
        return output
    }

    private fun priceToAscii(price: Float): ByteArray{
        var outputArray: ByteArray = byteArrayOf()

        val newPrice = (price*100.0).roundToInt() / 100.0
        var new = newPrice.toString()

        if(new[new.length-3] != "."[0]){
            new += "0"
        }

        for(i in 0 until 13 - new.length){
            outputArray += byteArrayOf(0x30)
        }

        for(i in new.indices){
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

    private fun asciiToInt(byte: Byte): Int{
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

    private fun printoutResponse(byte: ByteArray): Int{
        println()
        println("New TLV Message")
        println()

        println("HeaderSize ${fromByteToInt(byte[0]) + fromByteToInt(byte[1])*256 + fromByteToInt(byte[2])*65536 + fromByteToInt(byte[3])*16777216}")
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
            println("currentTag $currentTagString, tagLength $tagLength , Value $stringResponse")

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

    fun fromByteToInt(byte: Byte): Int {
        val output: Int

        val string = "%02X".format(byte)

        output = fromHex(string[0])* 16 + fromHex(string[1])

        return output
    }

    fun fromHex(input: Char): Int{
        val output: Int
        when(input){
            "0"[0] -> output = 0
            "1"[0] -> output = 1
            "2"[0] -> output = 2
            "3"[0] -> output = 3
            "4"[0] -> output = 4
            "5"[0] -> output = 5
            "6"[0] -> output = 6
            "7"[0] -> output = 7
            "8"[0] -> output = 8
            "9"[0] -> output = 9
            "A"[0] -> output = 10
            "B"[0] -> output = 11
            "C"[0] -> output = 12
            "D"[0] -> output = 13
            "E"[0] -> output = 14
            "F"[0] -> output = 15
            else -> output = 0
        }

        return output
    }
}