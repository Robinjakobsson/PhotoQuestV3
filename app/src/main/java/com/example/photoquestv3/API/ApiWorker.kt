package com.example.photoquestv3.API

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.photoquestv3.Repositories.ApiRepository
import com.example.photoquestv3.ViewModel.ApiViewModel

class ApiWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    val apiRepository = ApiRepository()

    override suspend fun doWork(): Result {

        try {
            val photo = apiRepository.getRandomPhoto("xoqINlwV1EP_aaTfLsLqwPpqlGMM9Wau696KtmitXtg")

            if (photo != null) {

                apiRepository.saveApiPostToDatabase(photo.urls?.regular, photo.description)
                Log.d("!!!", "Did fetch photo!!!!!")
                return Result.success()
            } else {
                return Result.failure()
                Log.d("!!!", "Did not fetch photo")
            }

        } catch (e: Exception) {
            Log.d("!!!", "API did not work")
            return Result.failure()
        }
    }
}
