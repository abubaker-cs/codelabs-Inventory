package com.example.inventory.data

import androidx.room.*
import com.example.inventory.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    // The OnConflictStrategy.IGNORE strategy ignores a new item if it's primary key is already in the database.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    /**
     * Flow:
     *
     * Using Flow or LiveData as return type will ensure you get notified whenever the data in the database changes.
     *
     * Because of the Flow return type, Room also runs the query on the background thread.
     * You don't need to explicitly make it a suspend function and call inside a coroutine scope.
     */
    @Query("SELECT * from item WHERE id = :id")
    fun getItem(id: Int): Flow<Item>

    @Query("SELECT * from item ORDER BY name ASC")
    fun getItems(): Flow<List<Item>>

}