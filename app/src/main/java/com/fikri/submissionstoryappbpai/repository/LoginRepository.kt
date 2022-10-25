package com.fikri.submissionstoryappbpai.repository

import android.content.res.Resources
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.api.ApiConfig
import com.fikri.submissionstoryappbpai.data_model.LoginResponseModel
import com.fikri.submissionstoryappbpai.data_model.LoginResult
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.other_class.getStringDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository(private val resources: Resources, private val pref: DataStorePreferences) {
    fun login(
        email: String,
        password: String,
        callback: ((
            responseType: String,
            responseMessage: String?,
            loginResponse: LoginResponseModel?
        ) -> Unit)? = null
    ) {
        val apiRequest = ApiConfig.getApiService().login(email, password)
        apiRequest.enqueue(object : Callback<LoginResponseModel> {
            override fun onResponse(
                call: Call<LoginResponseModel>,
                response: Response<LoginResponseModel>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        callback?.invoke(
                            ResponseModal.TYPE_SUCCESS,
                            responseBody.message,
                            responseBody
                        )
                    } else {
                        callback?.invoke(
                            ResponseModal.TYPE_FAILED,
                            responseBody?.message,
                            null
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
                        ResponseModal.TYPE_MISTAKE,
                        "${response.code()} ${response.message()} | $errorMessage",
                        null
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                callback?.invoke(
                    ResponseModal.TYPE_ERROR,
                    resources.getString(R.string.connection_problem),
                    null
                )
            }
        })
    }

    suspend fun saveLoginData(user: LoginResult?) {
        withContext(Dispatchers.Main) {
            pref.saveDataStoreValue(DataStorePreferences.USER_ID_KEY, user?.userId)
            pref.saveDataStoreValue(DataStorePreferences.NAME_KEY, user?.name)
            pref.saveDataStoreValue(DataStorePreferences.TOKEN_KEY, user?.token)
            pref.saveDataStoreValue(DataStorePreferences.SESSION_KEY, getStringDate())
            pref.saveDataStoreValue(DataStorePreferences.LAST_LOGIN_KEY, getStringDate())
        }
    }
}