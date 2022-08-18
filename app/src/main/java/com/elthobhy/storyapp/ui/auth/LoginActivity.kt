package com.elthobhy.storyapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.elthobhy.storyapp.core.utils.*
import com.elthobhy.storyapp.databinding.ActivityLoginBinding
import com.elthobhy.storyapp.ui.main.MainActivity
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel by inject<AuthViewModel>()
    private lateinit var dialogLoading: AlertDialog
    private lateinit var dialogError: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        dialogLoading= showDialogLoading(this)
        onClick()
    }

    private fun onClick() {
        binding.apply {
            btnLogin.setOnClickListener {
                if(loginToServer()){
                    val email = editEmail.text.toString()
                    val passwd = editPassword.text.toString()

                    closeKeyboard(this@LoginActivity)
                    authViewModel.login(email=email, passwd = passwd).observe(this@LoginActivity){
                        when(it){
                            is Resource.Success->{
                                if (!it.data.isNullOrEmpty()){
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    finish()
                                }
                            }
                            is Resource.Loading -> {
                                dialogLoading.show()
                            }
                            is Resource.Error->{
                                dialogError = showDialogError(this@LoginActivity,it.message)
                                dialogError.show()
                                dialogLoading.dismiss()
                            }
                        }
                    }
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

    override fun onStop() {
        super.onStop()
        dialogLoading.dismiss()
        dialogError.dismiss()
    }
}