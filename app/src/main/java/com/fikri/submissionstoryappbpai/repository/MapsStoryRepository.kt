package com.fikri.submissionstoryappbpai.repository

import android.content.res.Resources
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.application.MyApplication
import com.fikri.submissionstoryappbpai.data_model.AllStoryResponseModel
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.RefreshModal
import com.fikri.submissionstoryappbpai.other_class.reverseGeocoding
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsStoryRepository(
    private val myApplication: MyApplication,
    private val resources: Resources,
    private val pref: DataStorePreferences,
    private val geocoder: Geocoder,
    private val apiService: ApiService
) {
    fun getToken(callback: ((token: String) -> Unit)?) {
        myApplication.applicationScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                val token = pref.getDataStoreValue(DataStorePreferences.TOKEN_KEY).first()
                callback?.invoke(token)
            }
        }
    }

    fun getMapsStory(
        token: String,
        onSuccess: ((stories: ArrayList<Story>, message: String) -> Unit)? = null,
        onFailed: ((responseType: String, message: String) -> Unit)? = null
    ) {
        val apiRequest = apiService.getAllStories("Bearer $token", 400, 1)
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
                    resources.getString(R.string.connection_problem)
                )
            }
        })
    }

    fun getMapMode(): LiveData<String> {
        return pref.getDataStoreValue(DataStorePreferences.MAP_MODE_KEY).asLiveData()
    }

    fun getAddressName(latLng: LatLng): String {
        return reverseGeocoding(geocoder, latLng, resources.getString(R.string.location_unknown))
    }
}