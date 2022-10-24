package com.fikri.submissionstoryappbpai.repository

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.data_model.AddStoryResponseModel
import com.fikri.submissionstoryappbpai.other_class.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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
import java.io.IOException
import java.util.*

class CreateStoryMapRepository(
    private val context: Context,
    private val apiService: ApiService
) {
    val pref = DataStorePreferences.getInstance(context.dataStore)

    suspend fun getToken(): String {
        return withContext(Dispatchers.Main) {
            pref.getDataStoreValue(DataStorePreferences.TOKEN_KEY).first()
        }
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
                callback?.invoke(RefreshModal.TYPE_ERROR, null)
            }
        })
    }
}