package com.example.userhub.ui.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
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
    private lateinit var userAdapter: UserAdapter
    private var availableCities: List<String> = listOf()
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowStyle()
        setupRecyclerView()
        setupNetworkObserver()
        setupViewModelObservers()
        setupActionListeners()

        mainViewModel.refreshUsers().observe(this) {}
        mainViewModel.setSearchQuery("")
    }

    private fun setupWindowStyle() {
        supportActionBar?.hide()
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter()
        binding.rvUser.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = userAdapter
        }
    }

    private fun setupViewModelObservers() {
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
    }

    private fun setupActionListeners() {
        // Input Pencarian
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
            val sortOptions = listOf("Default", "A ke Z", "Z ke A")
            showCustomBottomSheet("Urutkan Berdasarkan Nama", sortOptions) { index, optionText ->
                mainViewModel.setSortOrder(index)
                val isSortingActive = index > 0
                setButtonActiveState(binding.btnSort, isSortingActive)
                Toast.makeText(this, optionText, Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Filter Kota
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

        binding.fabAddUser.setOnClickListener {
            val intent = Intent(this@MainActivity, AddUserActivity::class.java)
            intent.putExtra("EXTRA_GENDER_DEFAULT", 0)
            startActivity(intent)
        }
    }

    private fun setupNetworkObserver() {
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val isInitialOnline = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        binding.tvOfflineBanner.visibility = if (isInitialOnline) View.GONE else View.VISIBLE

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                runOnUiThread {
                    binding.tvOfflineBanner.visibility = View.GONE
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                runOnUiThread {
                    binding.tvOfflineBanner.visibility = View.VISIBLE
                }
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    private fun showCustomBottomSheet(title: String, options: List<String>, onSelected: (index: Int, text: String) -> Unit) {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val sheetBinding = LayoutBottomSheetMenuBinding.inflate(layoutInflater)

        sheetBinding.root.setBackgroundResource(R.drawable.textinputlayout)
        sheetBinding.root.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
        sheetBinding.tvBottomSheetTitle.text = title

        options.forEachIndexed { index, optionText ->
            val itemView = TextView(this).apply {
                val density = resources.displayMetrics.density
                val paddingPx = (16 * density).toInt()
                setPadding(paddingPx, paddingPx, paddingPx, paddingPx)

                text = optionText
                textSize = 14f
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

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}