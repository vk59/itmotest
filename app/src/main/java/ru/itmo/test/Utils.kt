package ru.itmo.test

import java.text.SimpleDateFormat
import java.util.*


class Utils {
    fun getFormattedTimeStamp(millis: Long): String {
        val date = Date(millis)
        val formatter = SimpleDateFormat("mm:ss.SS")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(date)
    }
}