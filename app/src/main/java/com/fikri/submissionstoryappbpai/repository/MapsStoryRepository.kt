package com.fikri.submissionstoryappbpai.repository

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.data_model.AllStoryResponseModel
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.RefreshModal
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class MapsStoryRepository(
    private val context: Context,
    private val pref: DataStorePreferences,
    private val apiService: ApiService
) {
    suspend fun getToken(): String {
        return withContext(Dispatchers.Main) {
            pref.getDataStoreValue(DataStorePreferences.TOKEN_KEY).first()
        }
    }

    fun getMapsStory(
        token: String,
        onSuccess: ((stories: ArrayList<Story>, message: String) -> Unit)? = null,
        onFailed: ((responseType: String, message: String) -> Unit)? = null
    ) {
        val apiRequest = apiService.getAllStories("Bearer $token", 100, 1)
        apiRequest.enqueue(object : Callback<AllStoryResponseModel> {
            override fun onResponse(
                call: Call<AllStoryResponseModel>,
                response: Response<AllStoryResponseModel>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        onSuccess?.invoke(
                            responseBody.listStory,
                            responseBody.message
                        )
                    } else {
                        onFailed?.invoke(
                            RefreshModal.TYPE_FAILED,
                            responseBody?.message ?: ""
                        )
                    }
                } else {
                    var errorMessage: String? = null
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        errorMessage = jObjError.getString("message")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    onFailed?.invoke(
                        RefreshModal.TYPE_FAILED,
                        "${response.code()} ${response.message()} | $errorMessage"
                    )
                }
            }

            override fun onFailure(call: Call<AllStoryResponseModel>, t: Throwable) {
                onFailed?.invoke(
                    RefreshModal.TYPE_ERROR,
                    context.getString(R.string.connection_problem)
                )
            }
        })
    }

    fun getMapMode(): LiveData<String> {
        return pref.getDataStoreValue(DataStorePreferences.MAP_MODE_KEY).asLiveData()
    }

    fun getAddressName(latLng: LatLng): String {
        var addressName = context.getString(R.string.location_unknown)
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }
}