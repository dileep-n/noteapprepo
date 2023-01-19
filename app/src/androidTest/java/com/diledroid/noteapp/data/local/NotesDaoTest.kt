package com.diledroid.noteapp.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diledroid.noteapp.data.model.Note
import com.diledroid.noteapp.getOrAwaitValue
import com.google.common.truth.Truth.assertThat

import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class NotesDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: NoteDatabase
    private lateinit var dao: NotesDao

    @Before
    fun setup(){

        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
        NoteDatabase::class.java).allowMainThreadQueries().build()
        dao = database.getNotesDao()
    }

    @After
    fun teardown(){
        database.close()
    }

    @Test
    fun insert()= runBlockingTest {

        val note = Note("hdhjn125","hai","desc","15 jan 2023")
        dao.insert(note)

        val allNotes = dao.getAllNotes().getOrAwaitValue()
        assertThat(allNotes).contains(note)

    }



}