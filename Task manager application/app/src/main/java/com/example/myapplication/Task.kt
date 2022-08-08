package com.example.myapplication

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

public enum class TimeOptions(var str: String){
    Hours("Godzin"),
    Days("Dni"),

}
class ApproximateTime(
    var amount: Int,
    var timeType: TimeOptions
){
    override fun toString(): String {
        return "$amount $timeType"
    }
}
class Task(
    var priority: Int,
    var name: String,
    completion: Int,
    var deadLine: Date,
    var approximateTime: ApproximateTime

) {
    var completion: Int = completion
        set(value) {
            field = if (value > 100) 100 else if (value<0) 0 else value
        }
    fun increaseCompletionBy(amount: Int) {
        completion += amount
    }

    companion object {
        fun dateToString(d: Date): String {
            return "${d.date}.${d.month}.${d.year+1900}"
        }
    }


}
