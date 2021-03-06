package com.example.myclasses.ui.lesson.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myclasses.database.LessonsDatabaseDao

class LessonDetailsViewModelFactory(
    private val lessonId: Int,
    private val dataSource: LessonsDatabaseDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonDetailsViewModel::class.java)) {
            return LessonDetailsViewModel(lessonId, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}