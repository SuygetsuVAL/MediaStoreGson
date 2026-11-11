package com.example.mediastoregson.data

import com.github.f4b6a3.uuid.UuidCreator

data class User (
    val id: String = UuidCreator.getTimeOrdered().toString(),
    val name: String,
    val email: String
)

