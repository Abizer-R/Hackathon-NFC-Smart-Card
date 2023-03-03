package com.example.nfcsmartcard.data.network.model.studentList

data class StudentListResponse(
    val cityList: List<City>,
    val notSubscribed: List<NotSubscribed>
)