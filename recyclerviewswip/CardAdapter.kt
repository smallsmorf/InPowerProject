package com.inpower.webguruz.recyclerviewswip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inpower.webguruz.model.CardItem
import com.inpower.webguruz.R

class CardAdapter(private val cardList: List<CardItem>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group_discover, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val cardItem = cardList[position]
        holder.bind(cardItem)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_name)
        private val detailsTextView: TextView = itemView.findViewById(R.id.tv_desc)
        private val imageView: ImageView = itemView.findViewById(R.id.bg_feed)
        private val joinButton: Button = itemView.findViewById(R.id.btn_join)
        private val membersImageView: ImageView = itemView.findViewById(R.id.iv_members)
        private val membersTextView: TextView = itemView.findViewById(R.id.tv_members)

        fun bind(cardItem: CardItem) {
            titleTextView.text = cardItem.title
            detailsTextView.text = cardItem.details
            imageView.setImageResource(cardItem.imageResId)
            membersTextView.text = cardItem.membersCount
            // Set an image resource for membersImageView if needed

            joinButton.setOnClickListener {
                // Handle join button click
            }

            itemView.setOnClickListener {
                if (detailsTextView.visibility == View.GONE) {
                    detailsTextView.visibility = View.VISIBLE
                    joinButton.visibility = View.VISIBLE
                } else {
                    detailsTextView.visibility = View.GONE
                    joinButton.visibility = View.GONE
                }
            }
        }
    }
}