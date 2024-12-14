package com.alocacaprofs.controller

sealed class Screen(val route: String) {
    object LoginScreen : Screen(route = "login_screen")
    object RegisterScreen : Screen(route = "register_screen")
    object HomeScreen : Screen(route = "home_screen")
    object SettingsScreen : Screen(route = "settings_screen")
    object ProfessorAddScreen : Screen(route = "professor_add_screen")
    object CursoAddScreen : Screen(route = "curso_add_screen")
    object ProfessorListScreen : Screen(route = "professor_list_screen")
    object CursoListScreen : Screen(route = "curso_list_screen")
    object ProfessorEditScreen : Screen(route = "professor_edit_screen/{matricula}") {
        fun createRoute(matricula: String): String {
            return "professor_edit_screen/$matricula"
        }
    }
    object CursoEditScreen : Screen(route = "curso_edit_screen/{cursoId}") {
        fun createRoute(cursoId: String): String {
            return "curso_edit_screen/$cursoId"
        }
    }
}