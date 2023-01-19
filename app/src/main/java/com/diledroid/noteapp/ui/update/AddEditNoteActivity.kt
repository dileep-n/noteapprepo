package com.diledroid.noteapp.ui.update

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.diledroid.noteapp.MainActivity
import com.diledroid.noteapp.R
import com.diledroid.noteapp.data.model.Note
import com.diledroid.noteapp.databinding.ActivityAddEditNoteBinding
import com.diledroid.noteapp.ui.home.viewmodel.NoteViewModal
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class AddEditNoteActivity : AppCompatActivity() {

    // on below line we are creating
    // variables for our UI components.

    lateinit var noteViewModal: NoteViewModal
    lateinit var binding: ActivityAddEditNoteBinding
    var noteTitle:String =""
    var noteDescription:String=""
    var noteID:String = ""
    var username = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_edit_note)
        // on below line we are initialing our view modal.
        noteViewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModal::class.java)

        binding.noteViewModel =noteViewModal
        binding.lifecycleOwner = this
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            username =
                user.email?.substringBefore(delimiter = "@", missingDelimiterValue = "User,")
                    .toString()
        }

        // on below line we are getting data passed via an intent.
        val noteType = intent.getStringExtra("noteType")
        if (noteType.equals("Edit")) {
            // on below line we are setting data to edit text.
            val noteTitle = intent.getStringExtra("noteTitle")
            val noteDescription = intent.getStringExtra("noteDescription")
            noteID = intent.getStringExtra("noteId").toString()
            binding.idBtn.setText("Update Note")
           noteViewModal.noteTitle.value = noteTitle
            noteViewModal.noteDesc.value = noteDescription
        } else {
            binding.idBtn.setText("Save Note")
        }

        // on below line we are adding
        // click listener to our save button.
        binding.idBtn.setOnClickListener {

            val millis = System.currentTimeMillis()
            var uniqueId = "$username$millis"

            // on below line we are checking the type
            // and then saving or updating the data.
            if (noteType.equals("Edit")) {
                if ( noteTitle.isNotEmpty() && noteDescription.isNotEmpty()) {
                    val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
                    val currentDateAndTime: String = sdf.format(Date())
                    var updatedNote = Note(noteID,noteTitle, noteDescription, currentDateAndTime)
                   noteViewModal.updateNote(updatedNote)
                    noteViewModal.updateFirestoreNote(updatedNote)
                    //Toast.makeText(this, "Note Updated..", Toast.LENGTH_LONG).show()
                }
            } else {
                if (noteTitle.isNotEmpty() && noteDescription.isNotEmpty()) {
                    val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
                    val currentDateAndTime: String = sdf.format(Date())
                    // if the string is not empty we are calling a
                    // add note method to add data to our room database.
                    noteViewModal.addNote(Note(uniqueId,noteTitle, noteDescription, currentDateAndTime))
                    noteViewModal.addNoteToFirestore(Note(uniqueId,noteTitle, noteDescription, currentDateAndTime))
                    //Toast.makeText(this, "$noteTitle Added", Toast.LENGTH_LONG).show()
                }
            }
            // opening the new activity on below line
            startActivity(Intent(applicationContext, MainActivity::class.java))
            this.finish()
        }

        noteViewModal.noteTitle.observe(this, androidx.lifecycle.Observer {
            noteTitle = it
        })
        noteViewModal.noteDesc.observe(this, androidx.lifecycle.Observer {
            noteDescription = it
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext, MainActivity::class.java))
        this.finish()
    }

}