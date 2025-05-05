package edu.bhcc.cho.noteserver.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bhcc.cho.noteserver.data.model.SignupRequest
import edu.bhcc.cho.noteserver.data.network.AuthApiService
import org.json.JSONObject

class SignupViewModel(private val apiService: AuthApiService) : ViewModel() {
    val signupState = MutableLiveData<Result>()

    fun signup(signupRequest: SignupRequest) {
        signupState.value = Result.Loading

        apiService.signupUser(
            signupRequest,
            onSuccess = { response -> signupState.value = Result.Success(response) },
            onError = { error -> signupState.value = Result.Error(error) }
        )
    }

    sealed class Result {
        object Loading : Result()
        data class Success(val data: JSONObject) : Result()
        data class Error(val error: String) : Result()
    }
}