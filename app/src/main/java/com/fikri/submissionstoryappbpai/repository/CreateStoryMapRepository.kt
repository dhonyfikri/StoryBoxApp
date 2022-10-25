package com.fikri.submissionstoryappbpai.repository

import android.content.res.Resources
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.application.MyApplication
import com.fikri.submissionstoryappbpai.data_model.AddStoryResponseModel
import com.fikri.submissionstoryappbpai.other_class.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CreateStoryMapRepository(
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

    fun getMapMode(): LiveData<String> {
        return pref.getDataStoreValue(DataStorePreferences.MAP_MODE_KEY).asLiveData()
    }

    fun getAddressName(latLng: LatLng): String {
        return reverseGeocoding(geocoder, latLng, resources.getString(R.string.location_unknown))
    }

    fun postDataToServer(
        token: String,
        desc: String,
        latLng: LatLng,
        photo: File?,
        callback: ((responseType: String, responseMessage: String?) -> Unit)? = null
    ) {
        val photoFile = reduceFileImage(photo as File)
        val description = desc.trim().toRequestBody("text/plain".toMediaType())
        val latitude = latLng.latitude.toString().toRequestBody("text/plain".toMediaType())
        val longitude = latLng.longitude.toString().toRequestBody("text/plain".toMediaType())
        val requestImageFile = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            photoFile.name,
            requestImageFile
        )
        val apiRequest =
            apiService.addGeolocationStory(
                "Bearer $token",
                imageMultipart,
                description,
                latitude,
                longitude
            )
        apiRequest.enqueue(object : Callback<AddStoryResponseModel> {
            override fun onResponse(
                call: Call<AddStoryResponseModel>,
                response: Response<AddStoryResponseModel>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        callback?.invoke(ResponseModal.TYPE_SUCCESS, responseBody.message)
                    } else {
                        callback?.invoke(RefreshModal.TYPE_FAILED, responseBody?.message)
                    }
                } else {
                    var errorMessage: String? = null
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        errorMessage = jObjError.getString("message")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    callback?.invoke(
                        RefreshModal.TYPE_MISTAKE,
                        "${response.code()} ${response.message()} | $errorMessage"
                    )
                }
            }

            override fun onFailure(call: Call<AddStoryResponseModel>, t: Throwable) {
                callback?.invoke(
                    RefreshModal.TYPE_ERROR,
                    resources.getString(R.string.connection_problem)
                )
            }
        })
    }
}