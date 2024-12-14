package com.alocacaprofs.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CursoDAO {

    val database = Firebase.firestore

    fun InputCurso(curso: Curso) {

        var dados = mapOf(
            "nome" to curso.nome,
            "numeroAlunos" to curso.numAlunos,
            "docentes" to curso.docentes
        )

        database.collection("curso")
            .add(dados)
            .addOnSuccessListener { documentReference ->
                Log.i("Teste", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.i("Teste", "Error adding document", e)
            }
    }

    fun updateCurso(curso: Curso) {
        val dados = mapOf(
            "nome" to curso.nome,
            "numeroAlunos" to curso.numAlunos,
            "docentes" to curso.docentes
        )

        database.collection("curso").document(curso.id)
            .update(dados)
            .addOnSuccessListener {
                Log.i("Teste", "Curso with ID ${curso.id} successfully updated.")
            }
            .addOnFailureListener { e ->
                Log.i("Teste", "Error updating curso with ID ${curso.id}", e)
            }
    }

    fun deleteCurso(curso: Curso) {
        database.collection("curso").document(curso.id)
            .delete()
            .addOnSuccessListener {
                Log.i("Teste", "Curso with ID ${curso.id} successfully deleted.")
            }
            .addOnFailureListener { e ->
                Log.i("Teste", "Error deleting curso with ID ${curso.id}", e)
            }
    }
}