package com.alocacaprofs.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class UsuarioDAO() {

    val database = Firebase.firestore

    fun InputUser(usuario: Usuario) {

        var dados = mapOf(
            "nome" to usuario.nome,
            "email" to usuario.email,
            "usuario" to usuario.usuario,
            "senha" to usuario.senha
        )

        database.collection("usuario")
            .add(dados)
            .addOnSuccessListener { documentReference ->
                Log.i("Teste", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.i("Teste", "Error adding document", e)
            }
    }
}