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
import com.alocacaprofs.model.Usuario
import com.alocacaprofs.model.UsuarioDAO
import com.alocacaprofs.view.components.Botao
import com.alocacaprofs.view.components.CampoTexto
import com.alocacaprofs.view.components.CampoTextoSenha

@Composable
fun RegisterScreen(
    navController: NavController
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var usuario by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }

    val usuarioDAO = UsuarioDAO()

    fun limparCampos() {
        nome = ""
        email = ""
        usuario = ""
        senha = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center),
        horizontalAlignment = Alignment
            .CenterHorizontally
    ) {
        CampoTexto(
            label = "Nome",
            value = nome,
            onValueChange = { nome = it },
            modifier = Modifier.padding(top = 16.dp)
        )
        CampoTexto(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.padding(top = 16.dp)
        )
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
            Botao(
                texto = "Registrar",
                onClick = {
                    if(nome.isBlank() || email.isBlank() || usuario.isBlank() || senha.isBlank()) {
                        Log.i("Teste", "Preencha todos os campos")
                    }else {
                        var usuario = Usuario("id", nome, email, usuario, senha)
//                        Log.i("Teste", "Nome: $nome, Email: $email, Usuário: $usuario, Senha: $senha")
                        usuarioDAO.InputUser(usuario)
                        limparCampos()
                    }
                })
            Botao(
                texto = "Voltar",
                onClick = {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                })
        }
    }
}








