package com.diledroid.noteapp.ui.home.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diledroid.noteapp.data.local.NoteDatabase
import com.diledroid.noteapp.data.model.Note
import com.diledroid.noteapp.data.repository.NoteRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NoteViewModal(application: Application) : AndroidViewModel(application) {

    // on below line we are creating a variable
    // for our all notes list and repository
    val TAG = "FIRESTORE"
    val allNotes = MutableLiveData<List<Note>>()
    var fList: MutableList<Note> = ArrayList()
    val repository: NoteRepository
    val noteTitle = MutableLiveData<String>()
    val noteDesc = MutableLiveData<String>()
    private val filteredList = ArrayList<Note>()
    var db: FirebaseFirestore? = null

    // this live data observed from home fragment
    private val _oldFilteredImages: MutableLiveData<ArrayList<Note>> = MutableLiveData()
    val oldFilteredImages: LiveData<ArrayList<Note>>
        get() = _oldFilteredImages


    // on below line we are initializing
    // our dao, repository and all notes
    init {
        db = FirebaseFirestore.getInstance();
        val dao = NoteDatabase.getDatabase(application).getNotesDao()
        repository = NoteRepository(dao)
        //allNotes = repository.allNotes
        readDataFromFirestore()

    }

    fun setValueDefault(items: ArrayList<Note>) {
        _oldFilteredImages.postValue(items)
    }

    // on below line we are creating a new method for deleting a note. In this we are
    // calling a delete method from our repository to delete our note.
    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }

    fun deleteFireStoreNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        db!!.collection("Notes").document(note.id).delete().addOnSuccessListener {
            Toast.makeText(getApplication(), "Note deleted", Toast.LENGTH_SHORT).show()
            readDataFromFirestore()
        }.addOnFailureListener{
            exception ->
            Toast.makeText(getApplication(), "Error deleting note $exception", Toast.LENGTH_SHORT).show()
        }
    }


    // on below line we are creating a new method for updating a note. In this we are
    // calling a update method from our repository to update our note.
    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }

    fun updateFirestoreNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {

        db!!.collection("Notes").document(note.id).update("noteTitle",note.noteTitle,
        "noteDescription",note.noteDescription,
        "timeStamp",note.timeStamp).addOnSuccessListener {

            Toast.makeText(getApplication(), "Note updated", Toast.LENGTH_SHORT).show()

        }
            .addOnFailureListener{exception ->

                Toast.makeText(getApplication(), "Error updating document$exception", Toast.LENGTH_SHORT).show()

            }
    }

    // on below line we are creating a new method for adding a new note to our database
    // we are calling a method from our repository to add a new note.
    fun addNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    fun addNoteToFirestore(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        val dbCourses = db!!.collection("Notes").document(note.id)
        dbCourses.set(note).addOnSuccessListener {
            Toast.makeText(getApplication(), "${note.noteTitle} Added", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document $exception")
                Toast.makeText(getApplication(), "Error adding document$exception", Toast.LENGTH_SHORT).show()

            }
    }

    // method to filter list based on the search text
    fun filter(text: String) {
        filteredList.clear()
        // running a for loop to compare elements.
        for (item in allNotes.value!!) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.noteTitle.lowercase().contains(text.lowercase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredList.add(item)
            }
        }
        _oldFilteredImages.postValue(filteredList)

    }

    fun readDataFromFirestore() {
        fList.clear()
        val dbCourses = db!!.collection("Notes")
        dbCourses
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { document ->
                    Log.d(TAG, "Read document with ID ${document.id}")
                    fList.add(document.toObject(Note::class.java))
                }
                allNotes.postValue(fList)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents $exception")
            }
    }


}