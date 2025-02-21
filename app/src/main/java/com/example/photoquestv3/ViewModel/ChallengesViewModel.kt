package com.example.photoquestv3.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.Repositories.ChallengesRepository

class ChallengesViewModel : ViewModel() {

    val challengesRepository = ChallengesRepository()

    val challenges: LiveData<List<Challenges>> = challengesRepository.listOfChallenges

    private val _isChecked = MutableLiveData<Boolean>()
    val isChecked: LiveData<Boolean> get() = _isChecked

    fun setChallengeCheckedState(state: Boolean) {
        _isChecked.value = state
        Log.d("!!!!", "Checkbox state set to: $state")
    }


    fun getChallengesFromDatabase() {
        challengesRepository.getChallengesFromDatabase()
    }

        fun getDailyChallenge(onResult: (Challenges?) -> Unit) {
            challengesRepository.getLatestChallenge { latestChallenge ->
                onResult(latestChallenge)
            }
        }

    fun markChallengeDone() {
        challengesRepository.markChallengeDone()
    }

    fun markChallengeNotDone() {
        challengesRepository.markChallengeNotDone()
    }

    }