package com.example.myapplication

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

object DataSource {
    fun sort() {
        var today = Date()
        var before = Date(today.time - (1000 * 60 * 60 * 24))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tasks.removeIf{
                it.deadLine.before(before)
            }

        }
        else {
            tasks.filter{
                it.deadLine.before(before)
            }.forEach{
                tasks.remove(it)
            }
        }
        tasks.sortBy{
            it.deadLine
        }
    }
    fun calculateTaskToWeekEnd(): Int {
        var re = 0
        tasks.forEach {
            if(TimeUnit.DAYS.convert(Math.abs(Date().time-it.deadLine.time),TimeUnit.MILLISECONDS)<7) {
                re+=1
            }
        }
        return re
    }
    val tasks =
        mutableListOf<Task>(
            Task(
                5,
                "Praca domowa",
                25,
                Date(123,4,15),
                ApproximateTime(4,TimeOptions.Days)
            ),
            Task(
                3,
                "Cos tam 2",
                65,
                Date(123,5,15),
                ApproximateTime(5,TimeOptions.Days)
            ),
            Task(
                1,
                "Matma",
                30,
                Date(123,5,16),
                ApproximateTime(3,TimeOptions.Hours)
            )
        )


}