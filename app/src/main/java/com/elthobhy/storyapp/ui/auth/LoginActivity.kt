package com.elthobhy.storyapp.ui.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.elthobhy.storyapp.core.utils.Resource
import com.elthobhy.storyapp.core.utils.UserPreferences
import com.elthobhy.storyapp.core.utils.closeKeyboard
import com.elthobhy.storyapp.core.utils.showDialog
import com.elthobhy.storyapp.databinding.ActivityLoginBinding
import com.elthobhy.storyapp.ui.main.MainActivity
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel by inject<AuthViewModel>()
    private var mShouldFinish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        onClick()
        setupViewModel()
    }

    private fun setupViewModel() {
        authViewModel.auth.observe(this){
            when(it){
                is Resource.Success->{
                    showDialog(this,false)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    mShouldFinish = true
                }
                is Resource.Loading -> showDialog(this,true)
                is Resource.Error->{
                    showDialog(this,true,it.message)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if(mShouldFinish)
            finish()
    }

    private fun onClick() {
        binding.apply {
            btnLogin.setOnClickListener {
                if(loginToServer()){
                    val email = editEmail.text.toString()
                    val passwd = editPassword.text.toString()

                    closeKeyboard(this@LoginActivity)
                    authViewModel.login(email=email, passwd = passwd)
                }else{
                    Toast.makeText(this@LoginActivity,"Please Field Email and Password", Toast.LENGTH_LONG).show()
                }
            }
            buttonRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

        }
    }

    private fun loginToServer(): Boolean =
        binding.editEmail.error == null &&
                binding.editPassword.error == null &&
                !binding.editEmail.text.isNullOrEmpty() &&
                !binding.editPassword.text.isNullOrEmpty()
}