package com.alocacaprofs.data

import android.content.Context

class Banco(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: Banco? = null

        fun getInstance(context: Context): Banco {
            return INSTANCE ?: synchronized(this) {
                val instance = Banco(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }


}