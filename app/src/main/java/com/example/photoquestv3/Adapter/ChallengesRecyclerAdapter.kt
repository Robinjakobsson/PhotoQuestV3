package com.example.photoquestv3.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.R

class ChallengesRecyclerAdapter(
    private val context: Context,
    private val challenges: MutableList<Challenges>
) : RecyclerView.Adapter<ChallengesRecyclerAdapter.ViewHolder>() {

    lateinit var cardView: CardView
    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.challenges_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val challenge = challenges[position]

        //Writes the challenge in the textView.
        holder.challengesTextView.text = challenge.challenge
        holder.dateTextView.text = challenge.date

        cardView = holder.itemView.findViewById(R.id.cardViewListItem)

        // Changes the star to yellow if challenge is completed, else, an empty star is shown.
        if (challenge.completed == true) {
            holder.starImageView.setImageResource(R.drawable.photoquest_star)

        } else {
            holder.starImageView.setImageResource(R.drawable.photoquest_star_outline)
        }

        if (position == challenges.size - 1) {
            cardView.setCardBackgroundColor(Color.parseColor("#70ffff33"))
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#50ffffff"))
        }


    }

    override fun getItemCount(): Int {
        return challenges.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val challengesTextView: TextView = itemView.findViewById(R.id.challengesTextView)
        val starImageView: ImageView = itemView.findViewById(R.id.starImageView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }

    fun updateChallenges(newChallenges: List<Challenges>) {
        challenges.clear()
        challenges.addAll(newChallenges)
        notifyDataSetChanged()
    }


}
