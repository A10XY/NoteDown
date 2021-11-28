package com.ahmed.notedown.models.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "date_time")
    val dateTime: String,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "image_path")
    val imagePath: String?,

    @ColumnInfo(name = "audio_path")
    val audioPath: String?,

    @ColumnInfo(name = "favourite")
    val favourite: Boolean
): Serializable {
    override fun toString(): String {
        return "$title : $dateTime"
    }
}