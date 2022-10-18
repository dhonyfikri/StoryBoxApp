package com.fikri.submissionstoryappbpai.repository

import com.fikri.submissionstoryappbpai.api.ApiConfig
import com.fikri.submissionstoryappbpai.application.MyApplication
import com.fikri.submissionstoryappbpai.data_model.AllStoryResponseModel
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.RefreshModal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRepository {
    fun getAllStories(pref: DataStorePreferences, callback: ((token: String) -> Unit)? = null) {
        MyApplication().applicationScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                val token = pref.getDataStoreValue(DataStorePreferences.TOKEN_KEY).first()
                callback?.invoke(token)
            }
        }
    }

    fun fetchAllStories(
        token: String,
        requestSize: Int,
        callback: ((
            currentStoriesCount: Int?,
            stories: ArrayList<Story>?,
            responseType: String?,
            responseMessage: String?
        ) -> Unit)? = null
    ) {
        val apiRequest = ApiConfig.getApiService().getAllStories("Bearer $token", requestSize, 0)
        apiRequest.enqueue(object : Callback<AllStoryResponseModel> {
            override fun onResponse(
                call: Call<AllStoryResponseModel>,
                response: Response<AllStoryResponseModel>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        callback?.invoke(
                            responseBody.listStory.size,
                            responseBody.listStory,
                            null,
                            null
                        )
                    } else {
                        callback?.invoke(
                            null,
                            null,
                            RefreshModal.TYPE_FAILED,
                            responseBody?.message
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
                    callback?.invoke(
                        null,
                        null,
                        RefreshModal.TYPE_FAILED,
                        "${response.code()} ${response.message()} | $errorMessage"
                    )
                }
            }

            override fun onFailure(call: Call<AllStoryResponseModel>, t: Throwable) {
                callback?.invoke(
                    null,
                    null,
                    RefreshModal.TYPE_ERROR,
                    null
                )
            }
        })
    }

    fun clearDataStore(pref: DataStorePreferences) {
        MyApplication().applicationScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                pref.clearDataStore()
            }
        }
    }
}