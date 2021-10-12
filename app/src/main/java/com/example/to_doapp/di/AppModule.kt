package com.example.to_doapp.di

import android.content.Context
import androidx.room.Room
import com.example.to_doapp.db.TodoDao
import com.example.to_doapp.db.TodoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun getTodoDatabase(@ApplicationContext context: Context): TodoDatabase =
        Room.databaseBuilder(context, TodoDatabase::class.java, "todo_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun getTodoDao(todoDatabase: TodoDatabase) : TodoDao =
        todoDatabase.getTodoDao()

}