package com.ekin.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateRegister: () -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val error = remember { mutableStateOf<String?>(null) }
    val isLoading = remember { mutableStateOf(false) }
    val isPasswordVisible = rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val auth = FirebaseAuth.getInstance()

    fun attemptLogin() {
        val trimmedEmail = email.value.trim()
        if (trimmedEmail.isEmpty() || password.value.isEmpty()) {
            error.value = "Lütfen e-posta ve şifre alanlarını doldurun."
            return
        }
            error.value = null
        isLoading.value = true
        auth.signInWithEmailAndPassword(trimmedEmail, password.value)
            .addOnSuccessListener {
                isLoading.value = false
                onLoginSuccess()
            }
            .addOnFailureListener { throwable ->
                isLoading.value = false
                error.value = throwable.message ?: "Giriş sırasında bir sorun oluştu."
            }
    }

    LaunchedEffect(auth.currentUser) {
        if (auth.currentUser != null) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFCF8))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Tekrar Hoş Geldin",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1F20),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Hesabına giriş yaparak e-kin deneyimine devam et.",
                fontSize = 16.sp,
                color = Color(0xFF5C5F66),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = { Text("E-posta adresi") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = { Text("Şifre") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                                Icon(
                                    imageVector = if (isPasswordVisible.value) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                attemptLogin()
                            }
                        )
                    )
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            attemptLogin()
                        },
                        enabled = !isLoading.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F8B53))
                    ) {
                        if (isLoading.value) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Giriş Yap", fontWeight = FontWeight.SemiBold)
                        }
                    }
                    OutlinedButton(
                        onClick = onGoogleSignInClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Google ile Devam Et")
                    }
                    TextButton(
                        onClick = onNavigateRegister,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hesabın yok mu? Şimdi kaydol", fontWeight = FontWeight.Medium)
                    }
                    if (error.value != null) {
                        Text(
                            text = error.value!!,
                            color = Color(0xFFB3261E),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val acceptedTerms = remember { mutableStateOf(false) }
    val acceptedPrivacy = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Kayıt Ol")
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Ad") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = email.value, onValueChange = { email.value = it }, label = { Text("E-posta") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = password.value, onValueChange = { password.value = it }, label = { Text("Şifre") })
        Spacer(Modifier.height(12.dp))
        RowCheck(text = "Gizlilik Koşullarını kabul ediyorum", checked = acceptedTerms.value) { acceptedTerms.value = it }
        Spacer(Modifier.height(4.dp))
        RowCheck(text = "Üye Sözleşmesini kabul ediyorum", checked = acceptedPrivacy.value) { acceptedPrivacy.value = it }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            if (!(acceptedTerms.value && acceptedPrivacy.value)) return@Button
            error.value = null
            CoroutineScope(Dispatchers.Main).launch {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email.value.trim(), password.value)
                    .addOnSuccessListener { onRegistered() }
                    .addOnFailureListener { error.value = it.message }
            }
        }, enabled = acceptedTerms.value && acceptedPrivacy.value) { Text("Kaydı Tamamla") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onBackToLogin) { Text("Geri") }
        if (error.value != null) {
            Spacer(Modifier.height(8.dp))
            Text(text = error.value!!)
        }
    }
}

@Composable
private fun RowCheck(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text)
    }
}


