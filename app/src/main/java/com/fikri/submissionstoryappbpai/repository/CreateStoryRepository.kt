package com.fikri.submissionstoryappbpai.repository

import com.fikri.submissionstoryappbpai.api.ApiConfig
import com.fikri.submissionstoryappbpai.application.MyApplication
import com.fikri.submissionstoryappbpai.data_model.AddStoryResponseModel
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.RefreshModal
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.other_class.reduceFileImage
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

class CreateStoryRepository {
    fun fetchToken(pref: DataStorePreferences, callback: ((token: String) -> Unit)?) {
        MyApplication().applicationScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                val token = pref.getDataStoreValue(DataStorePreferences.TOKEN_KEY).first()
                callback?.invoke(token)
            }
        }
    }

    fun postDataToServer(
        token: String,
        desc: String,
        photo: File?,
        callback: ((responseType: String, responseMessage: String?) -> Unit)? = null
    ) {
        val photoFile = reduceFileImage(photo as File)
        val description = desc.trim().toRequestBody("text/plain".toMediaType())
        val requestImageFile = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            photoFile.name,
            requestImageFile
        )
        val apiRequest =
            ApiConfig.getApiService().addStory("Bearer $token", imageMultipart, description)
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