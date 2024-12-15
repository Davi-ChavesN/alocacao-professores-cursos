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
import com.alocacaprofs.view.components.Botao
import com.alocacaprofs.view.components.CampoTexto
import com.alocacaprofs.view.components.CampoTextoReadOnly
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun CursoEditScreen (
    cursoId: String,
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val database = Firebase.firestore
    val curso = remember { mutableStateOf<Curso?>(null) }
    val allProfessors = remember { mutableStateOf<List<String>>(emptyList()) }
    val newProfessor = remember { mutableStateOf("") } // Dropdown selection
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val cursoDAO = CursoDAO()

    Log.i("TAGCURSOID", "Curso ID: $cursoId")

    LaunchedEffect(cursoId) {
        curso.value = fetchCursoById(cursoId)
        Log.i("TAGCURSOID", "Curso: ${curso}")

        val professorDocs = database.collection("professor").get().await()
        allProfessors.value = professorDocs.documents.mapNotNull { it.getString("nome") }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Edição de Curso")
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

        curso.value?.let { cursoData ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center),
                horizontalAlignment = Alignment
                    .CenterHorizontally
            ) {
                CampoTextoReadOnly(
                    label = "ID",
                    value = cursoData.id,
                    onValueChange = {},
                    modifier = Modifier
                        .padding(8.dp)
                )
                CampoTexto(
                    label = "Nome do Curso",
                    value = cursoData.nome,
                    onValueChange = { newNome ->
                        curso.value = cursoData.copy(nome = newNome)
                    },
                    modifier =Modifier
                        .padding(8.dp)
                )
                CampoTexto(
                    label = "Número de Alunos",
                    value = cursoData.numAlunos.toString(),
                    onValueChange = { newNumero ->
                        curso.value = cursoData.copy(numAlunos = newNumero.toIntOrNull() ?: 0)
                    },
                    modifier =Modifier
                        .padding(8.dp)
                )

                Text("Adicionar Professor:")
                IconButton(onClick = { isDropdownExpanded.value = true }) {
                    Text("Selecionar Professor")
                }

                DropdownMenu(
                    expanded = isDropdownExpanded.value,
                    onDismissRequest = { isDropdownExpanded.value = false }
                ) {
                    allProfessors.value.forEach { professor ->
                        DropdownMenuItem(onClick = {
                            if (professor !in cursoData.docentes) {
                                curso.value = cursoData.copy(docentes = cursoData.docentes + professor)
                            }
                            isDropdownExpanded.value = false // Fechar menu ao selecionar
                        }) {
                            Text(professor)
                        }
                    }
                }

                Text("Professores:")
                cursoData.docentes.forEach { professor ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = professor, modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            curso.value = cursoData.copy(docentes = cursoData.docentes - professor)
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
                        cursoDAO.updateCurso(curso.value!!)
                        navController.navigate(Screen.CursoListScreen.route){
                            popUpTo(Screen.CursoListScreen.route) { inclusive = true }
                        }
                    })
                    Botao(texto = "Deletar", onClick = {
                        cursoDAO.deleteCurso(curso.value!!)
                        navController.navigate(Screen.CursoListScreen.route){
                            popUpTo(Screen.CursoListScreen.route) { inclusive = true }
                        }
                    })
                }
            }
        }
    }
}

suspend fun fetchCursoById(cursoId: String): Curso? {
    val db = Firebase.firestore
    return try {
        val documentSnapshot = db.collection("curso").document(cursoId).get().await()

        if (documentSnapshot.exists()) {
            // Map the document fields to the Curso object
            val nome = documentSnapshot.getString("nome") ?: ""
            val numeroAlunos = documentSnapshot.getLong("numeroAlunos")?.toInt() ?: 0
            val docentes = documentSnapshot.get("docentes") as? List<String> ?: emptyList()

            // Return the mapped Curso object with the ID
            Curso(
                id = documentSnapshot.id,  // Assign the document ID
                nome = nome,
                numAlunos = numeroAlunos,
                docentes = docentes
            )
        } else {
            null // Return null if the document doesn't exist
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null // Return null in case of an error
    }
}
