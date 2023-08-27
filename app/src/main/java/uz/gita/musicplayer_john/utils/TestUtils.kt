package uz.gita.musicplayer_john.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

fun logging(msg: String) {
    Log.d("GGG", msg)
}

fun makeToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}