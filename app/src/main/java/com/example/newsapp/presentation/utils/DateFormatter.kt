package com.example.newsapp.presentation.utils

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat

private val formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT)

fun Long.formatDate(): String {
    return formatter.format(this)
}
