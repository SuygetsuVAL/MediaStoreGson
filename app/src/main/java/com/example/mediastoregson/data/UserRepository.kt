package com.example.mediastoregson.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.mediastoregson.helper.Config
import java.io.BufferedReader
import java.io.InputStreamReader

class UserRepository(private val context: Context) {

    fun saveUsers(users: List<User>) {
        val json = Gson().toJson(users)
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, Config.FILE_NAME)
            put(MediaStore.MediaColumns.MIME_TYPE, Config.MIME_TYPE)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Config.RELATIVE_PATH)
        }

        val uri = getFileUri() ?: resolver.insert(
            MediaStore.Files.getContentUri(Config.VOLUME_NAME),
            contentValues
        )

        uri?.let {
            try {
                resolver.openOutputStream(it, "rwt")?.use { outputStream ->
                    outputStream.write(json.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadUsers(): MutableList<User> {
        val uri = getFileUri() ?: return mutableListOf()

        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val json = reader.readText()
            val type = object : TypeToken<MutableList<User>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    private fun getFileUri(): Uri? {
        val resolver = context.contentResolver

        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME}=? AND ${MediaStore.MediaColumns.RELATIVE_PATH}=?"
        val selectionArgs = arrayOf(Config.FILE_NAME, Config.RELATIVE_PATH)

        val queryUri = MediaStore.Files.getContentUri(Config.VOLUME_NAME)

        resolver.query(queryUri, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    return Uri.withAppendedPath(queryUri, id.toString())
                }
            }

        return null
    }

    fun exportToUri(uri: Uri, users: List<User>) {
        val json = Gson().toJson(users)
        try {
            context.contentResolver.openOutputStream(uri)?.use {
                it.write(json.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun importFromUri(uri: Uri): MutableList<User> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val json = inputStream?.bufferedReader().use { it?.readText() }
            val type = object : TypeToken<MutableList<User>>() {}.type
            Gson().fromJson(json, type)
        } catch (_: Exception) {
            mutableListOf()
        }
    }
}

