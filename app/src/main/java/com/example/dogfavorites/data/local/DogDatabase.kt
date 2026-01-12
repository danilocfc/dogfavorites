package com.example.dogfavorites.data.local

import androidx.room.*
import android.content.Context
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites")
data class FavoriteDog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUrl: String,
    val breed: String // NOVO CAMPO
)

@Dao
interface DogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dog: FavoriteDog)

    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<FavoriteDog>>

    @Delete
    suspend fun delete(dog: FavoriteDog)
}

@Database(entities = [FavoriteDog::class], version = 1)
abstract class DogDatabase : RoomDatabase() {
    abstract fun dogDao(): DogDao

    companion object {
        @Volatile
        private var INSTANCE: DogDatabase? = null
        fun getDatabase(context: Context): DogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DogDatabase::class.java, "dog_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}