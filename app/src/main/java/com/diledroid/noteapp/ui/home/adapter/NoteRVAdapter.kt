package com.diledroid.noteapp.ui.home.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diledroid.noteapp.R
import com.diledroid.noteapp.data.model.Note
import com.diledroid.noteapp.databinding.NoteRvItemBinding

class NoteRVAdapter(
    val noteClickDeleteInterface: NoteClickDeleteInterface,
    val noteClickInterface: NoteClickInterface
) : RecyclerView.Adapter<NoteRVAdapter.ViewHolder>() {

    // on below line we are creating a
    // variable for our all notes list.
    private val allNotes = ArrayList<Note>()
    private lateinit var binding: NoteRvItemBinding

    // on below line we are creating a view holder class.
    inner class ViewHolder(private val binding: NoteRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // on below line we are creating an initializing all our
        // variables which we have added in layout file.
        fun bind(note: Note) {
            binding.note = note
           // binding.idTVNote.setText(note.noteTitle)
            binding.idTVDate.setText(note.timeStamp)
            binding.idIVDelete.setOnClickListener{
                noteClickDeleteInterface.onDeleteIconClick(note)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflating our layout file for each item of recycler view.
        binding = NoteRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // on below line we are setting data to item of recycler view.

        val note = allNotes[position]
        holder.bind(note)

        // on below line we are adding click listener
        // to our recycler view item.
        holder.itemView.setOnClickListener {
            // on below line we are calling a note click interface
            // and we are passing a position to it.
            noteClickInterface.onNoteClick(allNotes.get(position))
        }
    }

    override fun getItemCount(): Int {
        // on below line we are
        // returning our list size.
        return allNotes.size
    }

    // below method is use to update our list of notes.
    fun updateList(newList: List<Note>) {
        // on below line we are clearing
        // our notes array list
        allNotes.clear()
//        var sortedList = newList.sortedWith(compareBy<Note> {
//            it.timeStamp
//        })
        var sortedList = newList.sortedWith(
            compareByDescending<Note> {
            it.timeStamp
        })

        // on below line we are adding a
        // new list to our all notes list.
        allNotes.addAll(sortedList)
        // on below line we are calling notify data
        // change method to notify our adapter.
        notifyDataSetChanged()
    }
}

interface NoteClickDeleteInterface {
    // creating a method for click
    // action on delete image view.
    fun onDeleteIconClick(note: Note)
}

interface NoteClickInterface {
    // creating a method for click action
    // on recycler view item for updating it.
    fun onNoteClick(note: Note)
}
