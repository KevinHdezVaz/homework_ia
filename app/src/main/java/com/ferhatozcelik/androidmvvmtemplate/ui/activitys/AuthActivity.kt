package com.ferhatozcelik.androidmvvmtemplate.ui.activitys
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ferhatozcelik.androidmvvmtemplate.data.repository.AuthRepository
import com.ferhatozcelik.androidmvvmtemplate.databinding.ActivityAuthBinding
import com.ferhatozcelik.androidmvvmtemplate.ui.viewmodel.AuthState
import com.ferhatozcelik.androidmvvmtemplate.ui.viewmodel.AuthViewModel
import com.ferhatozcelik.androidmvvmtemplate.ui.viewmodel.AuthViewModelFactory
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {



    private lateinit var binding: ActivityAuthBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = AuthRepository()
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)



        setupTabLayout()
        setupButtons()
        observeAuthState()
    }


    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showLoginLayout()
                    1 -> showRegisterLayout()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun showLoginLayout() {
        binding.loginLayout.visibility = View.VISIBLE
        binding.registerLayout.visibility = View.GONE
    }

    private fun showRegisterLayout() {
        binding.loginLayout.visibility = View.GONE
        binding.registerLayout.visibility = View.VISIBLE
    }

    private fun setupButtons() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val password = binding.etLoginPassword.text.toString()
            viewModel.login(email, password)
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.etRegisterName.text.toString()
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            viewModel.register(  email, password)
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        // Mostrar progreso
                    }
                    is AuthState.Success -> {

                        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                        Toast.makeText(applicationContext, "asfasdf", Toast.LENGTH_SHORT).show()
                    }
                    is AuthState.Error -> {
                        Toast.makeText(this@AuthActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
}