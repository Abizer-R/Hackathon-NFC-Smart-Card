package com.example.nfcsmartcard.ui

import android.app.AlertDialog
import android.content.Intent
import android.icu.text.Normalizer.YES
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.nfcsmartcard.utils.TokenManager
import com.example.nfcsmartcard.R
import com.example.nfcsmartcard.databinding.FragmentHomeBinding
import com.example.nfcsmartcard.ui.ScannerActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val TAG = HomeFragment::class.java.simpleName + "TESTING"

    @Inject lateinit var tokenManager: TokenManager

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var confirmDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserDetails()
        setupViews()
    }

    private fun setupUserDetails() {
        val detailList = tokenManager.getUserDetails()
        // [0] : username
        // [1] : route category
        // [2] : bus number
        if(detailList[0] != null) binding.tvUsername.text = detailList[0]
        if(detailList[1] != null) binding.tvRouteCategory.text = detailList[1]
        if(detailList[2] != null) binding.tvBusNumber.text = detailList[2]

    }

    private fun setupViews() {

        createConfirmDialog()

        binding.btnScanCards.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_scannerFragment)
            startActivity(Intent(activity, ScannerActivity::class.java))
        }

        binding.btnLogOut.setOnClickListener {
            confirmDialog.show()
        }
    }

    private fun createConfirmDialog() {
        confirmDialog = AlertDialog.Builder(context)
            .setTitle(R.string.logout_dialog_text)
            .setPositiveButton(R.string.yes_text) { _, _ ->
                logout()
            }
            .setNegativeButton(R.string.no_text, null)
            .create()
    }

    private fun logout() {
        tokenManager.clearTokenAndUser()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}