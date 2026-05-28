package com.example.userhub.ui.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.userhub.data.Result
import com.example.userhub.data.response.UserResponseItem
import com.example.userhub.databinding.ActivityAddUserBinding
import com.example.userhub.ui.viewmodel.AddUserViewModel
import com.example.userhub.ui.viewmodelfactory.ViewModelFactory
import java.util.UUID
import kotlin.getValue

class AddUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserBinding
    private val addUserViewModel: AddUserViewModel by viewModels {
        ViewModelFactory.Companion.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up custom Toolbar M3
        setupToolbar()

        binding.btnSave.setOnClickListener {
            saveUserData()
        }
    }

    private fun saveUserData() {
        val username = binding.edUsername.text.toString().trim()
        val email = binding.edEmail.text.toString().trim()
        val address = binding.edAddress.text.toString().trim()
        val phoneNumber = binding.edPhone.text.toString().trim()
        val city = binding.edCity.text.toString().trim()

        val gender = if (binding.rbMale.isChecked) 0 else 1

        if (username.isEmpty() || email.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val newUser = UserResponseItem(
            id = UUID.randomUUID().toString(),
            name = username,
            email = email,
            address = address,
            phoneNumber = phoneNumber,
            city = city,
            gender = gender
        )

        addUserViewModel.addUser(newUser).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                        binding.btnSave.isEnabled = false
                    }
                    is Result.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(this, "User berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is Result.Error -> {
                        binding.progressIndicator.visibility = View.GONE
                        binding.btnSave.isEnabled = true
                        Toast.makeText(this, "Gagal menambahkan user: ${result.error}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.materialBarDetail)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = "Add New User"
        }

        binding.materialBarDetail.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}