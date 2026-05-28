package com.example.userhub.ui.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.userhub.R
import com.example.userhub.data.Result
import com.example.userhub.data.response.UserResponseItem
import com.example.userhub.databinding.ActivityAddUserBinding
import com.example.userhub.ui.viewmodel.AddUserViewModel
import com.example.userhub.ui.viewmodelfactory.ViewModelFactory
import java.util.UUID

class AddUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserBinding
    private val addUserViewModel: AddUserViewModel by viewModels {
        ViewModelFactory.Companion.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupCityDropdown()
        setupActionListeners()
    }

    private fun setupCityDropdown() {
        addUserViewModel.fetchCitiesFromApi().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.cityEditTextLayout.hint = "Loading cities..."
                        binding.edCity.isEnabled = false
                    }
                    is Result.Success -> {
                        binding.cityEditTextLayout.hint = getString(R.string.city)
                        binding.edCity.isEnabled = true
                        val cityList = result.data.map { it.name }
                        setDropdownAdapter(cityList)
                    }
                    is Result.Error -> {
                        loadLocalCitiesFallback()
                    }
                }
            }
        }
    }

    private fun loadLocalCitiesFallback() {
        addUserViewModel.getUniqueCitiesLocal().observe(this) { localCities ->
            binding.cityEditTextLayout.hint = getString(R.string.city)
            binding.edCity.isEnabled = true

            if (!localCities.isNullOrEmpty()) {
                setDropdownAdapter(localCities)
                Toast.makeText(this, "Mode Offline: Memuat kota dari cache.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Cache kota lokal kosong. Hubungkan ke internet terlebih dahulu.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setDropdownAdapter(cities: List<String>) {
        val adapter = ArrayAdapter(
            this,
            R.layout.item_dropdown_city,
            cities
        )
        val cityAutoComplete = binding.edCity as? AutoCompleteTextView
        cityAutoComplete?.setAdapter(adapter)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.materialBarDetail)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = "Tambah User Baru"
        }

        binding.materialBarDetail.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupActionListeners() {
        binding.btnSave.setOnClickListener {
            if (!isOnline()) {
                Toast.makeText(
                    this,
                    "Koneksi terputus, gagal menyimpan data.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            saveUserData()
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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
}