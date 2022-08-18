package com.elthobhy.storyapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.core.utils.Resource
import com.elthobhy.storyapp.core.utils.closeKeyboard
import com.elthobhy.storyapp.core.utils.showDialogError
import com.elthobhy.storyapp.core.utils.showDialogLoading
import com.elthobhy.storyapp.databinding.ActivityRegisterBinding
import org.koin.android.ext.android.inject

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel by inject<AuthViewModel>()
    private lateinit var dialogLoading: AlertDialog
    private lateinit var dialogError: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        dialogLoading= showDialogLoading(this)
        dialogError= showDialogError(this)
        setUpViewModel()
    }

    private fun setUpViewModel() {
        binding.apply {
            buttonRegister.setOnClickListener {
                if(registerToServer()){
                    val name = editName.text.toString()
                    val email = editEmail.text.toString()
                    val password = editPassword.text.toString()

                    closeKeyboard(this@RegisterActivity)
                    authViewModel.register(name=name, email = email, passwd = password).observe(this@RegisterActivity){
                        when(it){
                            is Resource.Success->{
                                Toast.makeText(this@RegisterActivity,"User Created", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                                finishAffinity()
                            }
                            is Resource.Loading-> dialogLoading.show()
                            is Resource.Error->{
                                dialogError = showDialogError(this@RegisterActivity,it.message)
                                dialogError.show()
                                dialogLoading.dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun registerToServer(): Boolean =
        binding.editEmail.error == null
                && binding.editPassword.error == null
                && !binding.editEmail.text.isNullOrEmpty()
                && !binding.editPassword.text.isNullOrEmpty()

    override fun onStop() {
        super.onStop()
        dialogLoading.dismiss()
        dialogError.dismiss()
    }
}