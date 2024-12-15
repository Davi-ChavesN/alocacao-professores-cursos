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
import com.alocacaprofs.model.Professor
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@Composable
fun ProfessorListScreen(
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val professores = remember { mutableStateOf<List<Professor>>(emptyList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Lista de Professores")
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
            professores.value = fetchProfessores()
            Log.i("Teste","Cursos updated: ${professores.value}") // Debugging log
        }
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(professores.value) { professor ->
                ProfessorItem(
                    professor,
                    onClick = { navController.navigate(Screen.ProfessorEditScreen.createRoute(professor.matricula)) }
                )
            }
        }
    }
}

@Composable
fun ProfessorItem(professor: Professor, onClick: () -> Unit) {
    val tempoServico = calcularTempoServico(professor.dataEntrada)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .clickable { onClick() }
    ) {
        Text(text = "Professor: ${professor.nome}", style = MaterialTheme.typography.h6)
        Text(text = "Matrícula: ${professor.matricula}")
        Text(text = "Salário: R$${professor.salario}")
        Text(text = "Área de Atuação: ${professor.areaAtuacao}")
        Text(text = "Data de Entrada: ${professor.dataEntrada}")
        Text(text = "Tempo de Serviço: $tempoServico")
        Text(text = "Cursos de Atuação: ${professor.cursosAtuacao.joinToString(", ")}")
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

suspend fun fetchProfessores(): List<Professor> {
    val db = Firebase.firestore
    return try {
        val snapshot = db.collection("professor").get().await()
        snapshot.documents.mapNotNull { document ->
            val nome = document.getString("nome") ?: ""
            val salario = document.getLong("salario")?.toDouble() ?: 0.0
            val areaAtuacao = document.getString("areaAtuacao") ?: ""
            val dataEntrada = document.getString("dataEntrada") ?: ""
            val cursosAtuacao = document.get("cursosAtuacao") as? List<String> ?: emptyList()
            Professor(
                matricula = document.id,
                nome = nome,
                salario = salario,
                areaAtuacao = areaAtuacao,
                dataEntrada = dataEntrada,
                cursosAtuacao = cursosAtuacao
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

fun calcularTempoServico(dataEntrada: String): String {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dataAtual = Calendar.getInstance()

    // Tenta interpretar a data de entrada
    val dataEntradaCompleta = try {
        // Verifica se a data informada tem o formato "yyyy" (apenas ano)
        if (dataEntrada.length == 4) {
            // Caso seja só o ano, assume o primeiro dia do ano
            formato.parse("01/01/$dataEntrada")
        } else {
            // Caso tenha o formato completo "dd/MM/yyyy"
            formato.parse(dataEntrada)
        }
    } catch (e: Exception) {
        // Se não conseguir interpretar a data, retorna uma mensagem de erro
        return "Data inválida"
    }

    if (dataEntradaCompleta == null) return "Data inválida"

    // Converte as datas para Calendar
    val calendarEntrada = Calendar.getInstance().apply { time = dataEntradaCompleta }

    // Calcula a diferença de anos, meses e dias
    val anos = dataAtual.get(Calendar.YEAR) - calendarEntrada.get(Calendar.YEAR)
    val meses = dataAtual.get(Calendar.MONTH) - calendarEntrada.get(Calendar.MONTH)
    val dias = dataAtual.get(Calendar.DAY_OF_MONTH) - calendarEntrada.get(Calendar.DAY_OF_MONTH)

    // Ajusta para o caso de meses ou dias negativos
    var anosFinais = if (meses < 0 || (meses == 0 && dias < 0)) anos - 1 else anos
    var mesesFinais = if (meses < 0) 12 + meses else meses
    var diasFinais = if (dias < 0) {
        val ultimoDiaDoMes = Calendar.getInstance().apply {
            set(calendarEntrada.get(Calendar.YEAR), calendarEntrada.get(Calendar.MONTH), 1)
            add(Calendar.MONTH, 1)
            add(Calendar.DAY_OF_MONTH, -1) // Último dia do mês
            mesesFinais = mesesFinais-1
        }.getActualMaximum(Calendar.DAY_OF_MONTH)

        ultimoDiaDoMes + dias
    } else {
        dias
    }

    // Retorna o tempo de serviço formatado
    return "$anosFinais anos, $mesesFinais meses e $diasFinais dias"
}