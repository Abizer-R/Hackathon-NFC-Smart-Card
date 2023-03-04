package com.example.nfcsmartcard.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nfcsmartcard.R
import com.example.nfcsmartcard.data.network.model.user.UserRequest
import com.example.nfcsmartcard.databinding.FragmentLoginBinding
import com.example.nfcsmartcard.utils.NetworkResult
import com.example.nfcsmartcard.utils.TokenManager
import com.example.nfcsmartcard.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val TAG = LoginFragment::class.java.simpleName + "TESTING"

    @Inject lateinit var tokenManager: TokenManager

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

//        if(tokenManager.getToken() != null) {
//            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.tvSignInUsingEmail.setOnClickListener {
            switchSignInOption(isContact =  false)
        }

        binding.tvSignInUsingContact.setOnClickListener {
            switchSignInOption(isContact =  true)
        }

        binding.btnSignIn.setOnClickListener {
            val userRequest = getUserRequest()
            val credentialValidation = userViewModel.validateCredentials(userRequest)

            if(credentialValidation.first) {
                userViewModel.loginUser(userRequest)
            } else {
                binding.tvError.text = credentialValidation.second
            }
        }
    }

    private fun setupObservers() {
        userViewModel.userResponseLiveData.observe(viewLifecycleOwner) {
            updateLoadingViews(isLoading = false)
            when(it) {
                is NetworkResult.Loading -> {
                    updateLoadingViews(isLoading = true)
                    binding.tvError.text = ""
                }
                is NetworkResult.Error -> { binding.tvError.text = it.message }
                is NetworkResult.Success -> {
                    tokenManager.saveTokenAndUser(it.data!!.token, it.data.user)
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
            }
        }
    }

    private fun switchSignInOption(isContact: Boolean) {
        binding.layoutEmail.visibility = if(isContact) View.GONE else View.VISIBLE
        binding.tvSignInUsingContact.visibility = if(isContact) View.GONE else View.VISIBLE

        binding.layoutContact.visibility = if(isContact) View.VISIBLE else View.GONE
        binding.tvSignInUsingEmail.visibility = if(isContact) View.VISIBLE else View.GONE
    }

    private fun getUserRequest(): UserRequest {
        return UserRequest(
            contact = binding.etContact.text.toString(),
            email = binding.etEmail.text.toString(),
            password = binding.etPassword.text.toString()
            )
    }

    private fun updateLoadingViews(isLoading: Boolean) {
        binding.progressBar.visibility = if(isLoading) View.VISIBLE else View.INVISIBLE
        binding.btnSignIn.isClickable = !isLoading
        binding.btnSignIn.isEnabled = !isLoading

        binding.btnSignIn.isEnabled = !isLoading
        if(binding.tvSignInUsingContact.isVisible)
            binding.tvSignInUsingContact.isEnabled = !isLoading

        if(binding.tvSignInUsingEmail.isVisible)
            binding.tvSignInUsingEmail.isEnabled = !isLoading
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}