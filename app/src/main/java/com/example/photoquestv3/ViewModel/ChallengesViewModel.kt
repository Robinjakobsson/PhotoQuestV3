package com.example.photoquestv3.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.Repositories.ChallengesRepository

class ChallengesViewModel : ViewModel() {

    private val challengesRepository = ChallengesRepository()

    val challenges: LiveData<List<Challenges>> = challengesRepository.listOfChallenges

    private val _isChecked = MutableLiveData<Boolean>()
    val isChecked: LiveData<Boolean> get() = _isChecked


    private val _numberOfCompletedChallenges = MutableLiveData<Int?>()
    val numberOfCompletedChallenges: MutableLiveData<Int?> get() = _numberOfCompletedChallenges

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

    /**
     * Fetch number of completed challenges.
     */

    fun countCompletedChallenges(uid: String) {

        challengesRepository.countCompletedChallenges(uid) { count, error ->
            if (error != null) {
                Log.d("ChallengesViewModel", "Error fetching number of completed challenges")
            } else {
                _numberOfCompletedChallenges.value = count
            }

        }
    }

    fun markChallengeDone() {
        challengesRepository.markChallengeDone()
    }

    fun markChallengeNotDone() {
        challengesRepository.markChallengeNotDone()
    }

}