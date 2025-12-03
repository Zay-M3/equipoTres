package com.example.equipotres.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.equipotres.R
import androidx.activity.viewModels
import android.widget.Toast
import com.example.equipotres.databinding.ActivityLoginBinding
import com.example.equipotres.model.UserRequest
import com.example.equipotres.utils.SessionManager
import com.example.equipotres.viewmodel.LoginViewModel
import android.view.View
import com.example.equipotres.widget.WidGetApp

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        sessionManager = SessionManager(this)

        setup()
        sesionSave()
        viewModelObservers()
    }

    private fun viewModelObservers() {
        observerIsRegister()
    }

    private fun observerIsRegister() {
        viewModel.isRegister.observe(this) { userResponse ->
            if (userResponse.isRegister) {
                Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
                val email = binding.etEmail.text.toString()
                sessionManager.saveUserEmail(email)
                notifyWidgetOfLogin()
                goToHome()
            } else {
                Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()
        val userRequest = UserRequest(email, pass)

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            viewModel.registerUser(userRequest)
        } else {
            Toast.makeText(this, "Rellene los campos porfavor!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setup() {
        binding.tvRegister.setOnClickListener {
            registerUser()
        }

        binding.btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun sesionSave() {
        if (sessionManager.isLoggedIn()) {
            binding.main.visibility = View.INVISIBLE
            goToHome()
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()
        viewModel.loginUser(email, pass) { isLogin ->
            if (isLogin) {
                sessionManager.saveUserEmail(email)
                notifyWidgetOfLogin()
                goToHome()
            } else {
                Toast.makeText(this, "No se pudo iniciar sesion!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun notifyWidgetOfLogin() {
        val intent = Intent(this, WidGetApp::class.java).apply {
            action = WidGetApp.ACTION_LOGIN_SUCCESS
        }
        sendBroadcast(intent)
    }
}