package com.example.nfcsmartcard.utils

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.Parcelable
import android.util.Base64
import android.util.Log
import android.widget.TextView
import com.example.nfcsmartcard.R
import com.example.nfcsmartcard.utils.Constants.SUCCESSFUL_KEY
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*
import kotlin.experimental.and

object NfcUtil {

//    fun readFromNfcIntent(intent: Intent) : String {
    fun readFromNfcIntent(messageList: Array<out Parcelable>?) : Pair<Boolean, String> {
//        val messageList: Array<out Parcelable>? = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        if(messageList != null) {

            /**
             * Since we only have one string, we won't run a loop for all messages and records
             */
            val ndefMessage = messageList[0] as NdefMessage
            val record = ndefMessage.records[0]
            when(record.tnf) {
                NdefRecord.TNF_WELL_KNOWN -> {
                    if(Arrays.equals(record.type, NdefRecord.RTD_TEXT)) {
                        // This means the actual type of data inside is plain text
                        val text = extractText(record.payload)
                        if(text != null) {
                            return Pair(true, text.toString())
                        } else {
                            return Pair(false, "Error in extracting text")
                        }

                    } else {
                        return Pair(false, "Not plain Text")
                    }
                }
                else -> {
                    return Pair(false, "Unknown Type")
                }
            }
        } else {
            return Pair(false, "null body")
        }
    }

    fun extractText(payload: ByteArray): CharSequence? {
        /**
         * in payload[0], the 7th bit (excluding bits 0...6) represents text encoding
         *  7th bit == 0 : "UTF-8"
         *  7th bit == 1 : "UTF-16"
         *
         * NOTE: (2)^7 = 128, therefore we can check the 7th bit by doing 'AND' with 128
         */
        val textEncoding: Charset = if ((payload[0] and (128).toByte()).toInt() == 0) Charsets.UTF_8 else Charsets.UTF_16

        /**
         * bits 0...5 (total 6 bits) represent the length of IANA language code
         * To get the length, we will use 'AND'
         * NOTE: 6 bits = 111111 in binary = 63 in decimal
         */
        val languageCodeLength = (payload[0] and (63).toByte()).toInt()

        try {
            // Get the Text
            return String(
                payload,
                languageCodeLength + 1,
                payload.size - languageCodeLength - 1,
                textEncoding
            )
        } catch (e: UnsupportedEncodingException) {
            Log.e("UnsupportedEncoding", e.toString())
            return null
        }
    }

    suspend fun convertBase64Testing(base64String: String) : Pair<String, Bitmap?> {
        try {
            val decodeString = Base64.decode(base64String, Base64.DEFAULT)
            val decoded = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
            return Pair(SUCCESSFUL_KEY, decoded)
        } catch (e: Exception) {
            return Pair(e.message.toString(), null)
        }
    }
}