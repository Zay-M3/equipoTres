package com.example.equipotres.repository
import com.example.equipotres.model.UserRequest
import com.example.equipotres.model.UserResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class LoginRepository  {
    private var firebase = FirebaseAuth.getInstance()
    suspend fun registerUser(userRequest: UserRequest, userResponse: (UserResponse) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                firebase.createUserWithEmailAndPassword(userRequest.email, userRequest.password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val email = task.result?.user?.email
                            userResponse(
                                UserResponse(
                                    email = email,
                                    isRegister = true,
                                    message = "Registro Exitoso!"
                                )
                            )
                        } else {
                            val error = task.exception
                            if (error is FirebaseAuthUserCollisionException) {
                                userResponse(
                                    UserResponse(
                                        isRegister = false,
                                        message = "Este usuario ya se encuentra registrado!"
                                    )
                                )
                            } else {
                                userResponse(
                                    UserResponse(
                                        isRegister = false,
                                        message = "Error en el registro"
                                    )
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                userResponse(
                    UserResponse(
                        isRegister = false,
                        message = e.message.toString()
                    )
                )
            }
        }
    }
}