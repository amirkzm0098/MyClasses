package com.example.myclasses.database.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.myclasses.database.entities.Lesson
import com.example.myclasses.database.entities.Session

data class LessonWithSessions(
    @Embedded val lesson: Lesson,
    @Relation(
        parentColumn = "lessonId",
        entityColumn = "lesson_id"
    )
    val sessions: List<Session>
) {
    override fun toString(): String {
        return lesson.lessonName
    }
}