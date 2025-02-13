package com.example.photoquestv3.Repositories

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photoquestv3.Models.ChallengeObjects
import com.example.photoquestv3.Models.Challenges
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.text.SimpleDateFormat
import java.util.Locale

class ChallengesRepository {

    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid
    val collection = uid?.let { db.collection("users").document(it) }
<<<<<<< HEAD


//    val listOfChallenges = mutableListOf<Challenges>()
=======
>>>>>>> origin/master

    val _listOfChallenges = MutableLiveData<List<Challenges>>()
    val listOfChallenges: LiveData<List<Challenges>> get() = _listOfChallenges

    fun getChallengesFromDatabase() {

        val today = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(today)

<<<<<<< HEAD

=======
>>>>>>> origin/master
        collection?.collection("challenges")?.whereLessThanOrEqualTo("date", formattedDate)
            ?.get()
            ?.addOnSuccessListener { documentSnapShot ->

                val challengesList = mutableListOf<Challenges>()

                for (document in documentSnapShot.documents) {

                    val challenge = document.toObject<Challenges>()

                    if (challenge != null) {
                        challengesList.add(challenge)

                        Log.d("!!!", "Success $document")
                    }
                }
                _listOfChallenges.value = challengesList

            }?.addOnFailureListener { exception ->
                Log.w("!!!", "Error getting documents: ", exception)
            }
    }

<<<<<<< HEAD

    fun getLatestChallenge(onResult: (Challenges?) -> Unit) {
        getChallengesFromDatabase()

        _listOfChallenges.observeForever { challengesList ->
            val latestChallenge = challengesList.lastOrNull()
            onResult(latestChallenge)
        }
    }







    //Does not work (tried in ChallengesFragment). GoogleServices?!?!?!
//    fun doNotUse() {
//
//        val today = Calendar.getInstance().time
//        collection.whereLessThanOrEqualTo("date", today)
//            .get()
//            .addOnSuccessListener { documents ->
//
////                listOfChallenges.clear()
//
//                for (document in documents) {
//                    val challenge = document.toObject(Challenges::class.java)
////                    listOfChallenges.add(challenge)
//
//                    Log.d("!!!", "Success fetched challenges and added to list")
//                }
//            }.addOnFailureListener { exception ->
//                Log.w("!!!", "Error getting documents: ", exception)
//            }
//    }

//    Works fine to add a list of object from ChallengeObjects to database.(Tested in ChallengesFragment).
//    fun addChallengesToDatabase() {
//
//        for (challenge in ChallengeObjects.ChallengeLists) {
//            collection.add(challenge)
//                .addOnSuccessListener {
//                    Log.d("!!!", "challenge added Success")
//                }.addOnFailureListener {
//                    Log.d("!!!", "challenge added Failed.")
//                }
//        }
//    }
}
=======
    //Works fine to add a list of object from ChallengeObjects to database.(Tested in ChallengesFragment).
    fun addChallengesToDatabase() {

        val db = FirebaseFirestore.getInstance()
       // val user = FirebaseAuth.getInstance().currentUser

        val userIds = mutableListOf<String>()

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val uid = document.id
                    userIds.add(uid)
                }
                Log.d("!!!!", "All userIDs: $userIds")

               // listOfChallenges.clear()

                for (uid in userIds) {
                    val userDocRef = db.collection("users").document(uid)

                    for (challenge in ChallengeObjects.ChallengeLists) {
                        userDocRef.collection("challenges").add(challenge)
                            .addOnSuccessListener {
                                Log.d("!!!", "challenge added Success")
                            }.addOnFailureListener {
                                Log.d("!!!", "challenge added Failed.")
                            }
                    }
                }
            }.addOnFailureListener { exception ->
                Log.d("!!!", "Error getting documents: $exception")
            }
    }
}
>>>>>>> origin/master
