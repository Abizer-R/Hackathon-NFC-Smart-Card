package com.example.nfcsmartcard.data.network.model.user

data class UserRequest(
    val contact: String,
    val email: String,
    val password: String
)