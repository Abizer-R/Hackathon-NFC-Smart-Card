package com.example.nfcsmartcard.data.network.model.studentDetailModel

data class StudentDetail(
    val _id: String,
    val contact: Long,
    val enroll_number: String,
    val image: String,
    val name: String,
    val route_category: String,
    val subscription_status: Boolean
)