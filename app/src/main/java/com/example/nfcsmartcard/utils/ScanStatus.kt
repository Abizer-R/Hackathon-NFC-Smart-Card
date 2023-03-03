package com.example.nfcsmartcard.utils

sealed class ScanStatus<T>(val enrollment: String) {
    class Subscribed<T> (enrollment: String) : ScanStatus<T> (enrollment = enrollment)
    class NotSubscribed<T> (enrollment: String) : ScanStatus<T> (enrollment = enrollment)
    class OtherBus<T> (enrollment: String) : ScanStatus<T> (enrollment = enrollment)
}
