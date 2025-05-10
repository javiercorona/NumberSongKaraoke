package com.example.numbersongkaraoke

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.numbersongkaraoke.databinding.ItemSongBinding

class SongAdapter(
    private val items: List<Pair<Song, Boolean>>,
    private val onClick: (Song, Boolean) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(val b: ItemSongBinding)
        : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Pair<Song, Boolean>) {
            val (song, unlocked) = item
            b.title.text = song.title
            b.root.alpha = if (unlocked) 1f else 0.4f
            b.lockIcon.visibility = if (unlocked) View.GONE else View.VISIBLE
            b.root.setOnClickListener { onClick(song, unlocked) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
