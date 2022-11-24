package com.diledroid.noteapp


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.diledroid.noteapp.data.model.Note
import com.diledroid.noteapp.databinding.ActivityMainBinding
import com.diledroid.noteapp.ui.home.adapter.NoteClickDeleteInterface
import com.diledroid.noteapp.ui.home.adapter.NoteClickInterface
import com.diledroid.noteapp.ui.home.adapter.NoteRVAdapter
import com.diledroid.noteapp.ui.home.viewmodel.NoteViewModal
import com.diledroid.noteapp.ui.register.view.RegisterActivity
import com.diledroid.noteapp.ui.update.AddEditNoteActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), NoteClickInterface, NoteClickDeleteInterface {

    // on below line we are creating a variable
    // for our recycler view, exit text, button and viewmodel.
    lateinit var noteViewModal: NoteViewModal
    lateinit var binding: ActivityMainBinding
    private var auth: FirebaseAuth? = null
    private lateinit var searchView: SearchView
    private var backPressedTime:Long = 0
    lateinit var backToast:Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.diledroid.noteapp.R.layout.activity_main)
        // on below line we are
        // initializing our view modal.
        noteViewModal = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModal::class.java)

        // on below line we are setting layout
        // manager to our recycler view.
        binding.notesRV.layoutManager = LinearLayoutManager(this)

        // on below line we are initializing our adapter class.
        val noteRVAdapter = NoteRVAdapter(this, this)

        // on below line we are setting
        // adapter to our recycler view.
        binding.notesRV.adapter = noteRVAdapter


        // on below line we are calling all notes method
        // from our view modal class to observer the changes on list.
        noteViewModal.allNotes.observe(this, Observer { list ->
            list?.let {
                // on below line we are updating our list.
                noteRVAdapter.updateList(it)
            }
        })

        noteViewModal.oldFilteredImages.observe(this) {
            if (it.isEmpty()) {
                binding.noResult.visibility = View.VISIBLE
            } else {
                binding.noResult.visibility = View.GONE
            }
            noteRVAdapter.updateList(it)
        }
//        binding.noteViewModel =noteViewModal
//        binding.lifecycleOwner = this
        binding.idFAB.setOnClickListener {
            // adding a click listener for fab button
            // and opening a new intent to add a new note.
            val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_logout) {
            auth = FirebaseAuth.getInstance()
            auth?.let { authentation ->
                authentation.signOut()
                val intent = Intent(this@MainActivity, RegisterActivity::class.java)
                startActivity(intent)
                this.finish()
            }

            return true
        }

        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                noteViewModal.filter(newText)
                return false
            }
        })

        return super.onOptionsItemSelected(item)
    }

    override fun onNoteClick(note: Note) {
        // opening a new intent and passing a data to it.
        val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
        intent.putExtra("noteType", "Edit")
        intent.putExtra("noteTitle", note.noteTitle)
        intent.putExtra("noteDescription", note.noteDescription)
        intent.putExtra("noteId", note.id)
        startActivity(intent)
        this.finish()
    }

    override fun onDeleteIconClick(note: Note) {
        // in on note click method we are calling delete
        // method from our view modal to delete our not.
        noteViewModal.deleteNote(note)
        // displaying a toast message
        Toast.makeText(this, "${note.noteTitle} Deleted", Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        backToast = Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_LONG)
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel()
            super.onBackPressed()
            return
        } else {
            backToast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }


}
