package edu.bhcc.cho.noteserver.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.bhcc.cho.noteserver.data.network.AuthApiService

class SignupViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apiService = AuthApiService(context.applicationContext)
        return SignupViewModel(apiService) as T
    }
}