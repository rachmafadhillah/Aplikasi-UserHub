package com.example.userhub.ui.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.userhub.data.Result
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.userhub.databinding.ActivityMainBinding
import com.example.userhub.ui.MainViewModel
import com.example.userhub.ui.adapter.UserAdapter
import com.example.userhub.ui.viewmodelfactory.ViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        @Suppress("DEPRECATION")

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val userAdapter = UserAdapter()

        binding.rvUser.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = userAdapter
        }

        mainViewModel.getUsers().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        val userData = result.data
                        userAdapter.submitList(userData) // Data masuk ke adapter
                    }

                    is Result.Error -> {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Terjadi kesalahan: ${result.error}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}