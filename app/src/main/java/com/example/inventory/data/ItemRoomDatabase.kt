package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inventory.model.Item

// public abstract class that extends RoomDatabase
// This new abstract class you defined acts as a database holder
// Set exportSchema to false, so as not to keep schema version history backups.
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class ItemRoomDatabase : RoomDatabase() {

    // Define an abstract method or property that returns an ItemDao Instance
    // and the Room will generate the implementation for you.
    abstract fun itemDao(): ItemDao

    // The companion object allows access to the methods for creating or getting the database
    // using the class name as the qualifier.
    companion object {


        // will keep a reference to the database, when one has been created.
        // This helps in maintaining a single instance of the database opened at a given time.
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null


        fun getDatabase(context: Context): ItemRoomDatabase {

            /**
             * Multiple threads can potentially run into a race condition and ask for a database
             * instance at the same time, resulting in two databases instead of one. Wrapping the
             * code to get the database inside a synchronized block means that only one thread of
             * execution at a time can enter this block of code, which makes sure the database only
             * gets initialized once.

             */
            return INSTANCE ?: synchronized(this) {

                // Use Room's Room.databaseBuilder to create your (item_database) database only
                // if it doesn't exist. Otherwise, return the existing database.

                // Inside the synchronized block, initialize the instance variable, and
                // use the database builder to get a database.

                // Pass in:
                // 1. application context
                // 2. database class
                // 3.  name for the database
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "item_database"
                )
                    // Migration Strategy
                    .fallbackToDestructiveMigration()
                    .build()

                // Defined recently with application context, database class and database name
                INSTANCE = instance

                return instance
            }

        }

    }
}

/**
 * Note:  will instantiate the database instance in the Application class
 *
 * DB Initializer File: BaseApplication.kt
 */