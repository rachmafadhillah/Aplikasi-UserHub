package com.example.userhub.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.userhub.R
import com.example.userhub.databinding.ActivityMainBinding
import com.example.userhub.data.Result
import com.example.userhub.databinding.LayoutBottomSheetMenuBinding
import com.example.userhub.ui.viewmodel.MainViewModel
import com.example.userhub.ui.adapter.UserAdapter
import com.example.userhub.ui.viewmodelfactory.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(this) }

    private var availableCities: List<String> = listOf()

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
                            binding.rvUser.scrollToPosition(0) // Reset scroll ke atas
                        }
                    }
                    is Result.Error -> {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(this, "Terjadi kesalahan: ${result.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        mainViewModel.cities.observe(this) { cities ->
            if (cities != null) {
                availableCities = cities
            }
        }

        mainViewModel.refreshUsers().observe(this) {}
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

        binding.btnSort.setOnClickListener {
            val sortOptions = listOf("Default (Tanpa Urutan)", "Nama: A ke Z", "Nama: Z ke A")

            showCustomBottomSheet("Urutkan Berdasarkan", sortOptions) { index, optionText ->
                mainViewModel.setSortOrder(index)

                val isSortingActive = index > 0
                setButtonActiveState(binding.btnSort, isSortingActive)

                Toast.makeText(this, optionText, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnFilter.setOnClickListener {
            val filterOptions = mutableListOf("Semua Kota")
            filterOptions.addAll(availableCities)

            showCustomBottomSheet("Filter Berdasarkan Kota", filterOptions) { index, optionText ->
                if (index == 0) {
                    mainViewModel.setCityFilter("")

                    setButtonActiveState(binding.btnFilter, false)
                    binding.btnFilter.text = "Filter Kota"
                } else {
                    mainViewModel.setCityFilter(optionText)

                    setButtonActiveState(binding.btnFilter, true)
                    binding.btnFilter.text = "Kota: $optionText"
                }
                Toast.makeText(this, "Filter: $optionText", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCustomBottomSheet(title: String, options: List<String>, onSelected: (index: Int, text: String) -> Unit) {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val sheetBinding = LayoutBottomSheetMenuBinding.inflate(layoutInflater)

        sheetBinding.root.setBackgroundResource(R.drawable.textinputlayout)
        sheetBinding.root.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
        sheetBinding.tvBottomSheetTitle.text = title

        options.forEachIndexed { index, optionText ->
            val itemView = TextView(this).apply {
                // 1. Perbaikan cara hitung padding tanpa fungsi ekstensi yang error
                val density = resources.displayMetrics.density
                val paddingPx = (16 * density).toInt()
                setPadding(paddingPx, paddingPx, paddingPx, paddingPx)

                text = optionText
                textSize = 14f // Nilai float untuk ukuran sp

                // 2 & 3. 💡 SOLUSI ERROR TYPEFACE: Gunakan properti 'typeface' bukan 'fontFamily'
                typeface = resources.getFont(R.font.poppins_regular)

                setTextColor(ContextCompat.getColor(this@MainActivity, R.color.text_secondary))

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                val outValue = TypedValue()
                theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                setBackgroundResource(outValue.resourceId)

                setOnClickListener {
                    onSelected(index, optionText)
                    dialog.dismiss()
                }
            }
            sheetBinding.itemContainer.addView(itemView)
        }

        dialog.setContentView(sheetBinding.root)
        dialog.show()
    }

    private fun setButtonActiveState(button: MaterialButton, isActive: Boolean) {
        if (isActive) {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_light))
            button.setTextColor(ContextCompat.getColor(this, R.color.primary_dark))
            button.iconTint = ContextCompat.getColorStateList(this, R.color.primary_dark)
            button.strokeColor = ContextCompat.getColorStateList(this, R.color.primary_dark)
        } else {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            button.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            button.iconTint = ContextCompat.getColorStateList(this, R.color.text_secondary)
            button.strokeColor = ContextCompat.getColorStateList(this, R.color.card_stroke)
        }
    }
}