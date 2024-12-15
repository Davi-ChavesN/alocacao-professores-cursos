package com.alocacaprofs.view.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.BottomAppBar
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alocacaprofs.controller.Screen
import com.alocacaprofs.model.Curso
import com.alocacaprofs.model.CursoDAO
import com.alocacaprofs.model.Professor
import com.alocacaprofs.model.ProfessorDAO
import com.alocacaprofs.view.components.Botao
import com.alocacaprofs.view.components.CampoTexto
import com.alocacaprofs.view.components.CampoTextoNumero
import com.alocacaprofs.view.components.CampoTextoReadOnly
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ProfessorEditScreen (
    matricula: String,
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val database = Firebase.firestore
    val professor = remember { mutableStateOf<Professor?>(null) }
    val allCursos = remember { mutableStateOf<List<String>>(emptyList()) }
    val newCurso = remember { mutableStateOf("") } // Dropdown selection
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val professorDAO = ProfessorDAO()

    Log.i("TAGPROFESSORID", "Curso ID: $matricula")

    LaunchedEffect(matricula) {
        professor.value = fetchProfessorById(matricula)
        Log.i("TAGCURSOID", "Curso: ${professor}")

        val cursoDocs = database.collection("curso").get().await()
        allCursos.value = cursoDocs.documents.mapNotNull { it.getString("nome") }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Edição de Professor")
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

        professor.value?.let { professorData ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center),
                horizontalAlignment = Alignment
                    .CenterHorizontally
            ) {
                CampoTextoReadOnly(
                    label = "Matrícula",
                    value = professorData.matricula,
                    onValueChange = {},
                    modifier = Modifier
                        .padding(8.dp)
                )
                CampoTexto(
                    label = "Nome",
                    value = professorData.nome,
                    onValueChange = { newNome ->
                        professor.value = professorData.copy(nome = newNome)
                    },
                    modifier =Modifier
                        .padding(8.dp)
                )
                CampoTextoNumero(
                    label = "Salário",
                    value = professorData.salario.toString(),
                    onValueChange = { newSalario ->
                        professor.value = professorData.copy(salario = newSalario.toDoubleOrNull() ?: 0.0)
                    },
                    modifier =Modifier
                        .padding(8.dp)
                )
                CampoTexto(
                    label = "Área de Atuação",
                    value = professorData.areaAtuacao,
                    onValueChange = { newAreaAtuacao ->
                        professor.value = professorData.copy(areaAtuacao = newAreaAtuacao)
                    },
                    modifier =Modifier
                        .padding(8.dp)
                )
                CampoTexto(
                    label = "Data de Entrada",
                    value = professorData.dataEntrada,
                    onValueChange = { newDataEntrada ->
                        professor.value = professorData.copy(dataEntrada = newDataEntrada)
                    },
                    modifier =Modifier
                        .padding(8.dp)
                )

                Text("Adicionar Curso:")
                IconButton(onClick = { isDropdownExpanded.value = true }) {
                    Text("Selecionar Curso")
                }

                DropdownMenu(
                    expanded = isDropdownExpanded.value,
                    onDismissRequest = { isDropdownExpanded.value = false }
                ) {
                    allCursos.value.forEach { curso ->
                        DropdownMenuItem(onClick = {
                            if (curso !in professorData.cursosAtuacao) {
                                professor.value = professorData.copy(cursosAtuacao = professorData.cursosAtuacao + curso)
                            }
                            isDropdownExpanded.value = false // Fechar menu ao selecionar
                        }) {
                            Text(curso)
                        }
                    }
                }

                Text("Cursos:")
                professorData.cursosAtuacao.forEach { curso ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = curso, modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            professor.value = professorData.copy(cursosAtuacao = professorData.cursosAtuacao - curso)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remover")
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement
                        .SpaceAround
                ) {
                    Botao(texto = "Atualizar", onClick = {
                        professorDAO.updateProfessor(professor.value!!)
                        navController.navigate(Screen.ProfessorListScreen.route){
                            popUpTo(Screen.CursoListScreen.route) { inclusive = true }
                        }
                    })
                    Botao(texto = "Deletar", onClick = {
                        professorDAO.deleteProfessor(professor.value!!)
                        navController.navigate(Screen.ProfessorListScreen.route){
                            popUpTo(Screen.CursoListScreen.route) { inclusive = true }
                        }
                    })
                }
            }
        }
    }
}

suspend fun fetchProfessorById(matricula: String): Professor? {
    val db = Firebase.firestore
    return try {
        val documentSnapshot = db.collection("professor").document(matricula).get().await()

        if (documentSnapshot.exists()) {
            val nome = documentSnapshot.getString("nome") ?: ""
            val salario = documentSnapshot.getLong("salario")?.toDouble() ?: 0.0
            val areaAtuacao = documentSnapshot.getString("areaAtuacao") ?: ""
            val dataEntrada = documentSnapshot.getString("dataEntrada") ?: ""
            val cursosAtuacao = documentSnapshot.get("cursosAtuacao") as? List<String> ?: emptyList()
            Professor(
                matricula = documentSnapshot.id,
                nome = nome,
                salario = salario,
                areaAtuacao = areaAtuacao,
                dataEntrada = dataEntrada,
                cursosAtuacao = cursosAtuacao
            )
        } else {
            null // Return null if the document doesn't exist
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null // Return null in case of an error
    }
}
