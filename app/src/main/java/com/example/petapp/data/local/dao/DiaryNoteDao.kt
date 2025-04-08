package com.example.petapp.data.local.dao

import androidx.room.*
import com.example.petapp.data.model.DiaryNoteEntity

@Dao
interface DiaryNoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryNote(diaryNote: DiaryNoteEntity)

    @Update
    suspend fun updateDiaryNote(diaryNote: DiaryNoteEntity)

    @Delete
    suspend fun deleteDiaryNote(diaryNote: DiaryNoteEntity)

    @Query("SELECT * FROM diary_note WHERE id = :diaryNoteId")
    suspend fun getDiaryNoteById(diaryNoteId: String): DiaryNoteEntity?

    @Query("SELECT * FROM diary_note")
    suspend fun getAllDiaryNotes(): List<DiaryNoteEntity>
}