package com.example.equipotres.ui.fragment.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.equipotres.R
import com.example.equipotres.databinding.FragmentLoginBinding
import com.example.equipotres.utils.SessionManager
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sessionManager = SessionManager(requireContext())
        
        // Configurar el botón para mostrar huella
        binding.lottieAnimationView.setOnClickListener {
            binding.lottieAnimationView.pauseAnimation()
            mostrarAutenticacionBiometrica()
        }
    }

    private fun mostrarAutenticacionBiometrica() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    sessionManager.login()

                    binding.tvStatus.text = getString(R.string.biometric_success)
                    binding.tvStatus.visibility = View.VISIBLE

                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    binding.lottieAnimationView.resumeAnimation()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Mostrar mensaje de error
                    binding.tvStatus.text = getString(R.string.biometric_failed)
                    binding.tvStatus.visibility = View.VISIBLE
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Mostrar el error
                    binding.tvStatus.text = errString
                    binding.tvStatus.visibility = View.VISIBLE
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_auth_title))
            .setSubtitle(getString(R.string.biometric_auth_subtitle))
            .setNegativeButtonText(getString(R.string.biometric_cancel))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}