package com.example.userhub.ui.view

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import com.example.userhub.data.Result
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.userhub.databinding.ActivityMainBinding
import com.example.userhub.ui.viewmodel.MainViewModel
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

        mainViewModel.userResult.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        val userData = result.data

                        userAdapter.submitList(userData) {
                            binding.rvUser.scrollToPosition(0)
                        }
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

        mainViewModel.refreshUsers().observe(this) { }
        mainViewModel.setSearchQuery("")

        binding.searchUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mainViewModel.setSearchQuery(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mainViewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })

        binding.btnSort.setOnClickListener { view ->
            showSortPopupMenu(view)
        }
    }

    private fun showSortPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menu.add(0, 0, 0, "Default")
        popup.menu.add(0, 1, 1, "Nama: A ke Z")
        popup.menu.add(0, 2, 2, "Nama: Z ke A")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                0 -> {
                    mainViewModel.setSortOrder(0)
                    Toast.makeText(this, "Kembali ke urutan default", Toast.LENGTH_SHORT).show()
                    true
                }
                1 -> {
                    mainViewModel.setSortOrder(1)
                    Toast.makeText(this, "Diurutkan A-Z", Toast.LENGTH_SHORT).show()
                    true
                }
                2 -> {
                    mainViewModel.setSortOrder(2)
                    Toast.makeText(this, "Diurutkan Z-A", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}