package com.alocacaprofs.view.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alocacaprofs.controller.Screen
import com.alocacaprofs.view.components.Botao
import com.alocacaprofs.view.components.CampoTexto
import com.alocacaprofs.view.components.CampoTextoSenha
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun LoginScreen(
    navController: NavController
) {
    var usuario by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }

    val database = Firebase.firestore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center),
        horizontalAlignment = Alignment
            .CenterHorizontally
    ) {
        CampoTexto(
            label = "Usuário",
            value = usuario,
            onValueChange = { usuario = it },
            modifier = Modifier.padding(top = 16.dp)
        )
        CampoTextoSenha(
            label = "Senha",
            value = senha,
            onValueChange = { senha = it },
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement
                .SpaceAround
        ){
            Botao(texto = "Entrar",
                onClick = {
                    if (!usuario.isBlank() && !senha.isBlank()) {
                        database.collection("usuario")
                            .whereEqualTo("usuario", usuario)
                            .whereEqualTo("senha", senha)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (!documents.isEmpty) {
                                    navController.navigate(route = Screen.HomeScreen.route)
                                    Log.i("Login", "Login successful for user: $usuario")
                                } else {
                                    Log.i("Login", "Usuário ou senha incorretos")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Login", "Erro ao acessar o Firestore: ${exception.message}")
                            }
                    } else {
                        Log.i("Teste", "Preencha todos os campos")
                    }
                })
            Botao(texto = "Cadastrar",
                onClick = { navController.navigate(route = Screen.RegisterScreen.route) })
        }
    }
}
