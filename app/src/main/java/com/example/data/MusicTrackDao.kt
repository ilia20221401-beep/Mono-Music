package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicTrackDao {
    @Query("SELECT * FROM music_tracks ORDER BY createdAt DESC")
    fun getAllTracks(): Flow<List<MusicTrackEntity>>

    @Query("SELECT * FROM music_tracks WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteTracks(): Flow<List<MusicTrackEntity>>

    @Query("SELECT * FROM music_tracks WHERE id = :id LIMIT 1")
    suspend fun getTrackById(id: Long): MusicTrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: MusicTrackEntity): Long

    @Delete
    suspend fun deleteTrack(track: MusicTrackEntity)

    @Query("UPDATE music_tracks SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM music_tracks")
    suspend fun getTrackCount(): Int
}
