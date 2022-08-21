package com.elthobhy.storyapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.elthobhy.storyapp.core.utils.closeKeyboard
import com.elthobhy.storyapp.core.utils.showDialogError
import com.elthobhy.storyapp.core.utils.showDialogLoading
import com.elthobhy.storyapp.core.utils.vo.Status
import com.elthobhy.storyapp.databinding.ActivityLoginBinding
import com.elthobhy.storyapp.ui.auth.AuthViewModel
import com.elthobhy.storyapp.ui.auth.register.RegisterActivity
import com.elthobhy.storyapp.ui.main.MainActivity
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by inject<LoginViewModel>()
    private val authViewModel by inject<AuthViewModel>()
    private lateinit var dialogLoading: AlertDialog
    private lateinit var dialogError: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        dialogLoading = showDialogLoading(this)
        dialogError = showDialogError(this)
        setUpViewModel()
    }

    private fun setUpViewModel() {
        binding.apply {
            btnLogin.setOnClickListener {
                if (loginToServer()) {
                    val email = editEmail.text.toString()
                    val passwd = editPassword.text.toString()

                    closeKeyboard(this@LoginActivity)
                    loginViewModel.login(email = email, passwd = passwd)
                        .observe(this@LoginActivity) {
                            when (it.status) {
                                Status.SUCCESS -> {
                                    if (!it.data.isNullOrEmpty()) {
                                        startActivity(
                                            Intent(
                                                this@LoginActivity,
                                                MainActivity::class.java
                                            )
                                        )
                                        finish()
                                        lifecycleScope.launch {
                                            authViewModel.saveKey(it.data)
                                        }
                                    }
                                }
                                Status.LOADING -> {
                                    dialogLoading.show()
                                }
                                Status.ERROR -> {
                                    dialogError = showDialogError(this@LoginActivity, it.message)
                                    dialogError.show()
                                    dialogLoading.dismiss()
                                }
                            }
                        }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please Field Email and Password",
                        Toast.LENGTH_LONG
                    ).show()
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