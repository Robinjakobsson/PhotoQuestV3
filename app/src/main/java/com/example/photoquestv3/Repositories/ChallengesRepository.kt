package com.example.photoquestv3.Repositories

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photoquestv3.Models.ChallengeObjects
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.Views.Fragments.LoginFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class ChallengesRepository {

    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid
    val collection = uid?.let { db.collection("users").document(it) }

    private val _listOfChallenges = MutableLiveData<List<Challenges>>()
    val listOfChallenges: LiveData<List<Challenges>> get() = _listOfChallenges


    /**
     * Fetch all previous challenges from database and today's challenge.
     */
    fun getChallengesFromDatabase() {

        val today = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(today)


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


    fun getLatestChallenge(onResult: (Challenges?) -> Unit) {
        getChallengesFromDatabase()

        _listOfChallenges.observeForever { challengesList ->
            val latestChallenge = challengesList.lastOrNull()
            onResult(latestChallenge)
        }
    }


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

    fun addChallengesToNewUser() {

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            for (challenge in ChallengeObjects.ChallengeLists) {
                db.collection("users")
                    .document(user.uid)
                    .collection("challenges")
                    .add(challenge)
                    .addOnSuccessListener {
                        Log.d("!!!", "1. challenge added Success")
                    }.addOnFailureListener {
                        Log.d("!!!", "2. challenge added Failed.")
                    }
            }

        } else {
            Log.d("!!!", "user not found")
        }
    }


    fun markChallengeDone() {

        val today = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(today)

        collection?.collection("challenges")?.whereEqualTo("date", formattedDate)
            ?.get()
            ?.addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    val document = documents.documents[0]
                    document.reference.update("completed", true)
                        .addOnSuccessListener {
                            Log.d("ChallengesRepository", "$formattedDate challenge completed!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("ChallengesRepository", "Error updating challenge", e)
                        }
                } else {
                    Log.w("ChallengesRepository", "No challenge found $formattedDate")
                }
            }
            ?.addOnFailureListener { e ->
                Log.w("ChallengesRepository", "Can't find challenge", e)
            }
    }

    fun markChallengeNotDone() {

        val today = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(today)

        collection?.collection("challenges")?.whereEqualTo("date", formattedDate)
            ?.get()
            ?.addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    val document = documents.documents[0]
                    document.reference.update("completed", false)
                        .addOnSuccessListener {
                            Log.d("!!!!", "$formattedDate challenge not completed")
                        }
                        .addOnFailureListener { e ->
                            Log.w("!!!!", "Error updating challenge", e)
                        }
                } else {
                    Log.w("!!!!", "No challenge found $formattedDate")
                }
            }
            ?.addOnFailureListener { e ->
                Log.w("!!!!", "Can't find challenge", e)
            }
    }

    /**
     * Fetch how many challenges the users has completed. Where the challenges boolean is true.
     */
    fun countCompletedChallenges(uid: String, onResult: (Int?, String?) -> Unit) {

        db.collection("users").document(uid).collection("challenges")
            .whereEqualTo("completed", true)
            .get()
            .addOnSuccessListener { document ->

                val numberOfCompletedChallenges = document.size()
                Log.d(
                    "ChallengesRepository",
                    "Number of completed challenges: ${numberOfCompletedChallenges}"
                )
                onResult(numberOfCompletedChallenges, null)
            }.addOnFailureListener() { exception ->
                Log.d(
                    "ChallengesRepository",
                    "Failed to fetch number of completed challenges ${exception.message}"
                )

                onResult(null, exception.message)
            }
    }
}
