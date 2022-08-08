package com.example.mp2.data

import androidx.room.*

@Dao
interface WishDao {
    @Query("SELECT * FROM wish")
    fun getAll(): List<Wish>

    @Query("SELECT * FROM wish WHERE id = :wishIds")
    fun loadAllByIds(wishIds: Int): List<Wish>


    @Insert
    fun insertAll(vararg wishes: Wish)

    @Update
    fun update(wish: Wish)

    @Delete
    fun delete(wish: Wish)
}