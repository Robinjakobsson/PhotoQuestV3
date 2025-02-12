package com.example.photoquestv3.Repositories

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photoquestv3.Models.ChallengeObjects
import com.example.photoquestv3.Models.Challenges
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class ChallengesRepository {

    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("Challenges")


//    val listOfChallenges = mutableListOf<Challenges>()

    val _listOfChallenges = MutableLiveData<List<Challenges>>()
    val listOfChallenges: LiveData<List<Challenges>> get() = _listOfChallenges


    fun getChallengesFromDatabase() {

        // val today = Calendar.getInstance().time

        collection
            .get()
            .addOnSuccessListener { documentSnapShot ->

                val challengesList = mutableListOf<Challenges>()

                for (document in documentSnapShot.documents) {

                    val challenge = document.toObject<Challenges>()

                    if (challenge != null) {
                        challengesList.add(challenge)

                        Log.d("!!!", "Success $document")
                    }
                }

                _listOfChallenges.value = challengesList

            }.addOnFailureListener { exception ->
                Log.w("!!!", "Error getting documents: ", exception)
            }
    }



    //Does not work (tried in ChallengesFragment). GoogleServices?!?!?!
    fun doNotUse() {

        val today = Calendar.getInstance().time
        collection.whereLessThanOrEqualTo("date", today)
            .get()
            .addOnSuccessListener { documents ->

//                listOfChallenges.clear()

                for (document in documents) {
                    val challenge = document.toObject(Challenges::class.java)
//                    listOfChallenges.add(challenge)

                    Log.d("!!!", "Success fetched challenges and added to list")
                }
            }.addOnFailureListener { exception ->
                Log.w("!!!", "Error getting documents: ", exception)
            }
    }

    //Works fine to add a list of object from ChallengeObjects to database.(Tested in ChallengesFragment).
    fun addChallengesToDatabase() {

        for (challenge in ChallengeObjects.ChallengeLists) {
            collection.add(challenge)
                .addOnSuccessListener {
                    Log.d("!!!", "challenge added Success")
                }.addOnFailureListener {
                    Log.d("!!!", "challenge added Failed.")
                }
        }
    }
}