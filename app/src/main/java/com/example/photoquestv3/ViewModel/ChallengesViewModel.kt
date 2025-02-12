package com.example.photoquestv3.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.Repositories.ChallengesRepository

class ChallengesViewModel : ViewModel() {

    val challengesRepository = ChallengesRepository()

    val challenges: LiveData<List<Challenges>> = challengesRepository.listOfChallenges

    fun getChallengesFromDatabase() {
        challengesRepository.getChallengesFromDatabase()
    }

}