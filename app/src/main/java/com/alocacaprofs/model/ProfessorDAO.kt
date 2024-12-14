package com.alocacaprofs.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ProfessorDAO {
    val database = Firebase.firestore

    fun InputProfessor(professor: Professor) {

        var dados = mapOf(
            "nome" to professor.nome,
            "salario" to professor.salario,
            "areaAtuacao" to professor.areaAtuacao,
            "dataEntrada" to professor.dataEntrada,
            "cursosAtuacao" to professor.cursosAtuacao
        )

        database.collection("professor")
            .add(dados)
            .addOnSuccessListener { documentReference ->
                Log.i("Teste", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.i("Teste", "Error adding document", e)
            }
    }

    fun updateProfessor(professor: Professor) {
        val dados = mapOf(
            "nome" to professor.nome,
            "salario" to professor.salario,
            "areaAtuacao" to professor.areaAtuacao,
            "dataEntrada" to professor.dataEntrada,
            "cursosAtuacao" to professor.cursosAtuacao
        )

        database.collection("professor").document(professor.matricula)
            .update(dados)
            .addOnSuccessListener {
                Log.i("Teste", "Curso with ID ${professor.matricula} successfully updated.")
            }
            .addOnFailureListener { e ->
                Log.i("Teste", "Error updating curso with ID ${professor.matricula}", e)
            }
    }

    fun deleteProfessor(professor: Professor) {
        database.collection("professor").document(professor.matricula)
            .delete()
            .addOnSuccessListener {
                Log.i("Teste", "Curso with ID ${professor.matricula} successfully deleted.")
            }
            .addOnFailureListener { e ->
                Log.i("Teste", "Error deleting curso with ID ${professor.matricula}", e)
            }
    }
}