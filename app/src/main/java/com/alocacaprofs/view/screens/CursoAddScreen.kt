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
import com.alocacaprofs.model.Curso
import com.alocacaprofs.model.CursoDAO
import com.alocacaprofs.view.components.Botao
import com.alocacaprofs.view.components.CampoTexto
import com.alocacaprofs.view.components.CampoTextoNumero
import kotlinx.coroutines.launch

@Composable
fun CursoAddScreen(
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Adicionar Curso")
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
        ){
            var nome by rememberSaveable { mutableStateOf("") }
            var numAlunos by rememberSaveable { mutableStateOf("") }

            val cursoDAO = CursoDAO()

            fun limparCampos() {
                nome = ""
                numAlunos = ""
            }

            Text(text = "Adicionar Curso")

            CampoTexto(
                label = "Nome",
                value = nome,
                onValueChange = { nome = it },
                modifier = Modifier.padding(top = 16.dp)
            )
            CampoTextoNumero(
                label = "Número de Alunos",
                value = numAlunos,
                onValueChange = { numAlunos = it },
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
                        if(nome.isBlank() || numAlunos.isBlank()) {
                            Log.i("Teste", "Preencha todos os campos")
                        }else {
                            var curso = Curso("id", nome, numAlunos.toInt(), List<String>(0, {""}))
//                        Log.i("Teste", "Nome: $nome, Email: $email, Usuário: $usuario, Senha: $senha")
                            cursoDAO.InputCurso(curso)
                            limparCampos()
                        }
                    })
            }
        }
    }
}
