package com.alocacaprofs.controller

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.alocacaprofs.view.screens.CursoAddScreen
import com.alocacaprofs.view.screens.CursoEditScreen
import com.alocacaprofs.view.screens.CursoListScreen
import com.alocacaprofs.view.screens.HomeScreen
import com.alocacaprofs.view.screens.LoginScreen
import com.alocacaprofs.view.screens.ProfessorAddScreen
import com.alocacaprofs.view.screens.ProfessorEditScreen
import com.alocacaprofs.view.screens.ProfessorListScreen
import com.alocacaprofs.view.screens.RegisterScreen
import com.alocacaprofs.view.screens.SettingsScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route
    ) {
        composable(//tela de login
            route = Screen.LoginScreen.route
        ) {
            LoginScreen(navController = navController)
        }
        composable(//tela de cadastro
            route = Screen.RegisterScreen.route
        ) {
            RegisterScreen(navController = navController)
        }
        composable(//tela principal
            route = Screen.HomeScreen.route
        ) {
            HomeScreen(navController = navController)
        }
        composable(//tela de configurações
            route = Screen.SettingsScreen.route
        ) {
            SettingsScreen(navController = navController)
        }
        composable(//tela de cadastro de professores
            route = Screen.ProfessorAddScreen.route
        ) {
            ProfessorAddScreen(navController = navController)
        }
        composable(//tela de cadastro de cursos
            route = Screen.CursoAddScreen.route
        ) {
            CursoAddScreen(navController = navController)
        }
        composable(//tela de lista de professores
            route = Screen.ProfessorListScreen.route
        ) {
            ProfessorListScreen(navController = navController)
        }
        composable(//tela de lista de cursos
            route = Screen.CursoListScreen.route
        ) {
            CursoListScreen(navController = navController)
        }
        composable(//tela de edição de professores
            route = Screen.ProfessorEditScreen.route,
            arguments = listOf(navArgument("matricula") { type = NavType.StringType })
        ) { backStackEntry ->
            val ID = backStackEntry.arguments?.getString("matricula") ?: ""
            ProfessorEditScreen(matricula = ID, navController = navController)
        }
        composable(//tela de edição de cursos
            route = Screen.CursoEditScreen.route,
            arguments = listOf(navArgument("cursoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ID = backStackEntry.arguments?.getString("cursoId") ?: ""
            CursoEditScreen(cursoId = ID, navController = navController)
        }
    }
}
