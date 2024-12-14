package com.alocacaprofs.view.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alocacaprofs.controller.Screen
import com.alocacaprofs.model.Curso
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun CursoListScreen(
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val cursos = remember { mutableStateOf<List<Curso>>(emptyList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Lista de Cursos")
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
        LaunchedEffect(Unit) {
            cursos.value = fetchCursos()
            Log.i("Teste","Cursos updated: ${cursos.value}") // Debugging log
        }
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(cursos.value) { curso ->
                CursoItem(
                    curso,
                    onClick = {
                        Log.i("Teste", "Curso ID: ${curso.id}")
                        val id = curso.id ?: "unknown" // Handle null gracefully
                        navController.navigate(Screen.CursoEditScreen.createRoute(cursoId = id))
                    }
                )
            }
        }
    }
}

@Composable
fun CursoItem(curso: Curso, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .clickable { onClick() }
    ) {
        Text(text = "Curso: ${curso.nome}", style = MaterialTheme.typography.h6)
        Text(text = "ID: ${curso.id}")
        Text(text = "Número de Alunos: ${curso.numAlunos}")
        Text(text = "Docentes: ${curso.docentes.joinToString(", ")}")
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

suspend fun fetchCursos(): List<Curso> {
    val db = Firebase.firestore
    return try {
        val snapshot = db.collection("curso").get().await()
        snapshot.documents.mapNotNull { document ->
            // Map the document fields to the Curso object
            val nome = document.getString("nome") ?: ""
            val numeroAlunos = document.getLong("numeroAlunos")?.toInt() ?: 0
            val docentes = document.get("docentes") as? List<String> ?: emptyList()
            Curso(
                id = document.id, // Assign the document ID
                nome = nome,
                numAlunos = numeroAlunos,
                docentes = docentes
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}