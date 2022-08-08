package com.example.mp2.data

import android.content.Context
import androidx.room.*
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun toDate(dateString: String) : LocalDateTime ? {
        return LocalDateTime.parse(dateString)
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String {
        return date.toString()
    }
}
@Database(entities = [Wish::class], version = 2)
@TypeConverters(Converters::class)
abstract class WishDatabase : RoomDatabase() {
    abstract fun wishDao(): WishDao
    companion object {
        private lateinit var db: WishDatabase
        fun open(context: Context) {
            db = Room.databaseBuilder(
            context, WishDatabase::class.java, "wishes.db"
        ).fallbackToDestructiveMigration()
                .build()
            //return db
        }
        fun db(): WishDatabase {
            return db
        }
    }
}