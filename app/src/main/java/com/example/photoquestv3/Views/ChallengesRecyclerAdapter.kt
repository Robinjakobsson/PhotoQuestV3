package com.example.photoquestv3.Views
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.R

class ChallengesRecyclerAdapter (
        private val context: Context,
        private val challenges: MutableList<Challenges>
    ) : RecyclerView.Adapter<ChallengesRecyclerAdapter.ViewHolder>() {

        private val layoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = layoutInflater.inflate(R.layout.challenges_list_item, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val challenge = challenges[position]

            //Writes the challenge in the textView.
            holder.challengesTextView.text = challenge.challenge

            // Changes the star to yellow if challenge is completed, else, an empty star is shown.
            if (challenge.completed) {
                holder.starImageView.setImageResource(R.drawable.baseline_star_24)
            } else {
                holder.starImageView.setImageResource(R.drawable.baseline_star_outline_24)
            }
        }
       override fun getItemCount(): Int {
            return challenges.size
        }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val challengesTextView: TextView = itemView.findViewById(R.id.challengesTextView)
            val starImageView : ImageView = itemView.findViewById(R.id.starImageView)
        }
    }
