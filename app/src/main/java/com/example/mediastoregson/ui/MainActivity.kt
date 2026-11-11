package com.example.mediastoregson.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.example.mediastoregson.R
import com.example.mediastoregson.data.User
import com.example.mediastoregson.data.UserRepository
import com.example.mediastoregson.databinding.ActivityMainBinding
import com.example.mediastoregson.viewmodel.UserViewModel
import com.example.mediastoregson.viewmodel.UserViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: UserAdapter
    private var editingUser: User? = null

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let { viewModel.exportToUri(it) }
    }

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            viewModel.mergeImport(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = UserRepository(this)
        val factory = UserViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        adapter = UserAdapter(
            onEdit = { user ->
                editingUser = user
                binding.etId.setText(user.id)
                binding.etName.setText(user.name)
                binding.etEmail.setText(user.email)
                binding.btnAdd.text = getString(R.string.update)
            },
            onDelete = { user ->
                viewModel.deleteUser(user)
                Snackbar.make(binding.root, "Deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        viewModel.undoDelete()
                    }.show()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.users.observe(this) { userList ->
            adapter.submitList(userList)
        }

        viewModel.loadUsers()

        binding.btnAdd.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Name and Email cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = if (editingUser != null) {
                editingUser!!.copy(name = name, email = email)
            } else {
                User(name = name, email = email)
            }

            viewModel.addOrUpdate(user)
            clearForm()
        }

        binding.btnExport.setOnClickListener {
            exportLauncher.launch("user.json")
        }

        binding.btnImport.setOnClickListener {
            importLauncher.launch(arrayOf("application/json"))
        }
    }

    private fun clearForm() {
        binding.etId.text?.clear()
        binding.etName.text?.clear()
        binding.etEmail.text?.clear()
        binding.btnAdd.text = getString(R.string.add)
        editingUser = null
    }
}

