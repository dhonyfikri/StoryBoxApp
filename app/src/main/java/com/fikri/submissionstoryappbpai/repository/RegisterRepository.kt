package com.fikri.submissionstoryappbpai.repository

import android.content.res.Resources
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.api.ApiConfig
import com.fikri.submissionstoryappbpai.data_model.RegisterResponseModel
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository(private val resources: Resources) {
    fun register(
        name: String,
        email: String,
        password: String,
        callback: ((responseType: String, responseMessage: String?) -> Unit)? = null
    ) {
        val apiRequest = ApiConfig.getApiService().register(name, email, password)
        apiRequest.enqueue(object : Callback<RegisterResponseModel> {
            override fun onResponse(
                call: Call<RegisterResponseModel>,
                response: Response<RegisterResponseModel>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        callback?.invoke(
                            ResponseModal.TYPE_SUCCESS,
                            responseBody.message
                        )
                    } else {
                        callback?.invoke(
                            ResponseModal.TYPE_FAILED,
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
                        ResponseModal.TYPE_MISTAKE,
                        "${response.code()} ${response.message()} | $errorMessage"
                    )
                }
            }

            override fun onFailure(call: Call<RegisterResponseModel>, t: Throwable) {
                callback?.invoke(
                    ResponseModal.TYPE_ERROR,
                    resources.getString(R.string.connection_problem)
                )
            }
        })
    }
}