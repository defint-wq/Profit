package com.example.profit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ProfitEntity::class],
    version = 1,
    exportSchema = false)
abstract class ProfitDataBase : RoomDatabase() {

    abstract fun profitDao(): ProfitDao

    companion object{
        @Volatile
        private var INSTANCE: ProfitDataBase? = null

        fun getInstance(context: Context): ProfitDataBase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ProfitDataBase::class.java,
                        "profits_db"
                    ).build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}