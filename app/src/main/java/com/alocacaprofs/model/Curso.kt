package com.alocacaprofs.model

import com.google.firebase.database.PropertyName

data class Curso(
    val id: String,
    @PropertyName("nome")
    val nome: String,
    @PropertyName("numeroAlunos")
    val numAlunos: Int,
    @PropertyName("docentes")
    val docentes: List<String>
)
