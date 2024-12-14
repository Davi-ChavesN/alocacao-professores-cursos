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
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alocacaprofs.controller.Screen
import com.alocacaprofs.model.Professor
import com.alocacaprofs.model.ProfessorDAO
import com.alocacaprofs.model.Usuario
import com.alocacaprofs.view.components.Botao
import com.alocacaprofs.view.components.CampoTexto
import com.alocacaprofs.view.components.CampoTextoNumero
import com.alocacaprofs.view.components.CampoTextoSenha
import kotlinx.coroutines.launch

@Composable
fun ProfessorAddScreen(
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Adicionar Professor")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = {
                        navController.navigate(Screen.HomeScreen.route) {
                            popUpTo(Screen.HomeScreen.route) {
                                inclusive = true
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Home, contentDescription = "Home")
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.SettingsScreen.route) {
                            popUpTo(Screen.SettingsScreen.route) {
                                inclusive = true
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            }
        },
        drawerContent = {
            DrawerContent(navController, scaffoldState, scope)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .wrapContentSize(align = Alignment.Center),
            horizontalAlignment = Alignment
                .CenterHorizontally
        ) {
            var nome by rememberSaveable { mutableStateOf("") }
            var salario by rememberSaveable { mutableStateOf("") }
            var areaAtuacao by rememberSaveable { mutableStateOf("") }
            var dataEntrada by rememberSaveable { mutableStateOf("") }

            val professorDAO = ProfessorDAO()

            fun limparCampos() {
                nome = ""
                salario = ""
                areaAtuacao = ""
                dataEntrada = ""
            }

            Text(text = "Adicionar Professor")

            CampoTexto(
                label = "Nome",
                value = nome,
                onValueChange = { nome = it },
                modifier = Modifier.padding(top = 16.dp)
            )
            CampoTextoNumero(
                label = "Salário",
                value = salario,
                onValueChange = { salario = it },
                modifier = Modifier.padding(top = 16.dp)
            )
            CampoTexto(
                label = "Área de Atuação",
                value = areaAtuacao,
                onValueChange = { areaAtuacao = it },
                modifier = Modifier.padding(top = 16.dp)
            )
            CampoTexto(
                label = "Data de Entrada",
                value = dataEntrada,
                onValueChange = { dataEntrada = it },
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
                    texto = "Adicionar",
                    onClick = {
                        if(nome.isBlank() || salario.isBlank() || areaAtuacao.isBlank() || dataEntrada.isBlank()) {
                            Log.i("Teste", "Preencha todos os campos")
                        }else {
                            var professor = Professor("id", nome, salario.toDouble(), areaAtuacao, dataEntrada, List<String>(0, {""}))
//                        Log.i("Teste", "Nome: $nome, Email: $email, Usuário: $usuario, Senha: $senha")
                            professorDAO.InputProfessor(professor)
                            limparCampos()
                        }
                    })
            }
        }
    }
}



