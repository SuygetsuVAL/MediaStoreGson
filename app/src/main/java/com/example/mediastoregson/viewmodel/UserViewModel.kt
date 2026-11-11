package com.example.mediastoregson.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mediastoregson.data.User
import com.example.mediastoregson.data.UserRepository

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _users = MutableLiveData<List<User>>(emptyList())
    val users get() = _users

    private var currentList = mutableListOf<User>()
    private var deletedUser: User? = null

    private fun emit() {
        _users.value = currentList.sortedByDescending { it.id }
        repository.saveUsers(currentList)
    }

    fun loadUsers() {
        currentList.clear()
        currentList.addAll(repository.loadUsers())
        _users.value = currentList.sortedByDescending { it.id }
    }

    fun addOrUpdate(user: User) {
        val index = currentList.indexOfFirst { it.id == user.id }
        if (index >= 0) {
            currentList[index] = user
        } else {
            currentList.add(user)
        }
        emit()
    }

    fun deleteUser(user: User) {
        val index = currentList.indexOfFirst { it.id == user.id }
        if (index == -1) return
        deletedUser = user
        currentList.removeAt(index)
        println(currentList.toString())
        println(index)
        emit()
    }

    fun undoDelete() {
        deletedUser?.let { user ->
            if (currentList.none { it.id == user.id }) {
                currentList.add(user)
                emit()
            }
        }
        deletedUser = null
        emit()
    }

    fun exportToUri(uri: Uri) {
        repository.exportToUri(uri, currentList)
    }

    fun importFromUri(uri: Uri) {
        val imported = repository.importFromUri(uri)
        currentList.clear()
        currentList.addAll(imported)
        emit()
    }

    fun mergeImport(uri: Uri) {
        val importedList = repository.importFromUri(uri)
        if (importedList.isEmpty()) return

        val existingMap = currentList.associateBy { it.id }.toMutableMap()

        for (user in importedList) {
            if (!existingMap.containsKey(user.id)) {
                existingMap[user.id] = user
            }
        }

        currentList.clear()
        currentList.addAll(existingMap.values)
        emit()
    }
}

