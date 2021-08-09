package com.desk360.base.util

import android.text.format.DateUtils
import java.util.*

object DateUtils {
    fun isYesterday(date: Date) = DateUtils.isToday(date.time + DateUtils.DAY_IN_MILLIS)
    fun isToday(date: Date) = DateUtils.isToday(date.time)
    fun now() = Calendar.getInstance().time.time
}