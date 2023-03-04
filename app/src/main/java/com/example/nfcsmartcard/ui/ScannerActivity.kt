package com.example.nfcsmartcard.ui

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.nfcsmartcard.R
import com.example.nfcsmartcard.data.network.model.studentDetailModel.StudentDetailsResponse
import com.example.nfcsmartcard.databinding.ActivityScannerBinding
import com.example.nfcsmartcard.utils.Constants.BASE64_IMAGE_TESTING
import com.example.nfcsmartcard.utils.Constants.SUCCESSFUL_KEY
import com.example.nfcsmartcard.utils.Constants.getLocalBase64
import com.example.nfcsmartcard.utils.NetworkResult
import com.example.nfcsmartcard.utils.NfcUtil
import com.example.nfcsmartcard.utils.ScanStatus
import com.example.nfcsmartcard.utils.TokenManager
import com.example.nfcsmartcard.viewModels.StudentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScannerActivity : AppCompatActivity() {

    private val TAG = ScannerActivity::class.java.simpleName + "TESTING"

    private var _binding: ActivityScannerBinding? = null
    private val binding get() = _binding!!

    private val studentViewModel by viewModels<StudentViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager

    private lateinit var conductorBusRoute: String

    // FOR NFC STUFF
    private lateinit var pendingIntent: PendingIntent
    private lateinit var readFilters: Array<IntentFilter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        convertBase64Testing()

        // TODO: This is removed
//        studentViewModel.getStudentListResponse()

        conductorBusRoute = tokenManager.getUserDetails()[1]!!
        setupViews()
        setupObservers()

        setupNfcRead()
    }

     private fun setupNfcRead() {
        /**
         * Code to enable foreground read [PART - 1/3]
         */
        val intent = Intent(this, javaClass)
        // This says that if I already have an instance of activity on top of the stack, just reuse it
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val textFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED, "text/plain")
        readFilters = arrayOf(textFilter)

        if(getIntent().action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            readEnrollment(getIntent())

        }
    }

    private fun readEnrollment(intent: Intent) {
        val messageList: Array<out Parcelable>? = intent.getParcelableArrayExtra(
            NfcAdapter.EXTRA_NDEF_MESSAGES)
        val nfcResult = NfcUtil.readFromNfcIntent(messageList)
        if(nfcResult.first) {
            studentViewModel.verifyEnrollment(nfcResult.second)
        } else {
            // TODO: Show Error in Reading the CARD
//            binding.tvEnrollment.text = "ERROR"
//            binding.tvScanResult.text = nfcResult.second
        }
    }

    /**
     * For reading the NFC when the app is already launched [PART - 2/3]
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent?.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            readEnrollment(intent)
        }
    }

    private fun enableRead() {
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, readFilters, null)
    }

    private fun disableRead() {
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this)
    }

    /**
     * Enabling and disabling foreground dispatch [PART - 3/3]
     */
    override fun onResume() {
        super.onResume()
        enableRead()
    }
    override fun onPause() {
        super.onPause()
        disableRead()
    }

    private fun setupViews() {
        val userDetails = tokenManager.getUserDetails()
//        binding.tvRouteCategory.text = "${userDetails[1]} : [${userDetails[2]}]"
//        binding.tvRouteCategory.text = "Bus Route = ${userDetails[1]}"

//        binding.btnTryAgain.setOnClickListener {
//            // TODO: I guess we don't need this button
////            studentViewModel.getStudentListResponse()
//        }

        // TODO: BtnViewMore is removed [maybe mimic its functions]
//        binding.btnViewMore.setOnClickListener {
//            studentViewModel.getStudentDetailsResponse()
//            binding.layoutScanning.visibility = View.INVISIBLE
//            binding.cvStudentDetails.visibility = View.VISIBLE
////            binding.layoutEnrollment.isEnabled = false
//            binding.btnViewMore.isEnabled = false
//        }

        // TODO: tvCancel is also removed
//        binding.tvCancel.setOnClickListener {
//            binding.cvStudentDetails.visibility = View.GONE
//            binding.layoutScanning.visibility = View.VISIBLE
//        }

    }

    private fun setupObservers() {

        studentViewModel.studentDetailsLiveData.observe(this) {
//            binding.layoutScanning.visibility = View.INVISIBLE
//            binding.cvError.visibility = View.GONE
//            binding.cvLoading.visibility = View.INVISIBLE
            updateLoadingViews(isLoading = false)

            // resetting default image
            binding.imageViewStudentProfile.setImageDrawable(getDrawable(R.drawable.ss_student))

            when(it) {
                is NetworkResult.Loading -> {
                    updateLoadingViews(isLoading = true)
                }
                is NetworkResult.Error -> {
                    // TODO: Errors [1] Can't read card, [2] Couldn't fetch details
                    // TODO: try again ka dekhlo
                    binding.cvError.visibility = View.VISIBLE
                    binding.tvErrorMessage.text = it.message
                }
                is NetworkResult.Success -> {
                    binding.cvStudentDetails.visibility = View.VISIBLE
                    binding.layoutScanResult.visibility = View.VISIBLE
                    handleViewsAccordingToResponse(it.data!!)
                }
            }


        }
    }

    private fun handleViewsAccordingToResponse(data: StudentDetailsResponse) {
        Log.i(TAG, "handleViewsAccordingToResponse: data = $data")
        val studentDetails = data.student_detail

        lifecycleScope.launch{
            val profileBitmapResult = NfcUtil.convertBase64Testing(studentDetails.image)
            if(profileBitmapResult.first != SUCCESSFUL_KEY) {
                // TODO: ERROR
//                updateLoadingViews(isLoading = false)
//                binding.cvError.visibility = View.VISIBLE
//                binding.tvErrorMessage.text = profileBitmapResult.first

            } else {
                if(profileBitmapResult.second != null) {
//                    binding.imageViewStudentProfile.setImageBitmap(profileBitmapResult.second)
                }
            }
        }

        binding.tvStudentName.text = studentDetails.name
        binding.tvStudentRouteCategory.text = studentDetails.route_category
        binding.tvStudentEnrollment.text = studentDetails.enroll_number

        if(studentDetails.subscription_status) {
            if(studentDetails.route_category == conductorBusRoute) {
                binding.lottieSubscribed.visibility = View.VISIBLE
                binding.lottieSubscribed.playAnimation()
                binding.tvBusScanStatus.text = getString(R.string.scan_result_subscribed_text)
                binding.tvBusScanStatus.setTextColor(resources.getColor(R.color.color_subscribed))

            } else {
                binding.lottieOtherBus.visibility = View.VISIBLE
                binding.lottieOtherBus.playAnimation()
                binding.tvBusScanStatus.text = getString(R.string.scan_result_other_bus_text)
                binding.tvBusScanStatus.setTextColor(resources.getColor(R.color.color_other_bus))


            }

        } else {
            binding.lottieNotSubscribed.visibility = View.VISIBLE
            binding.lottieNotSubscribed.playAnimation()
            binding.tvBusScanStatus.text = getString(R.string.scan_result_not_subscribed_text)
            binding.tvBusScanStatus.setTextColor(resources.getColor(R.color.color_not_subscribed))

            binding.tvStudentRouteCategory.text = ""


        }

        lifecycleScope.launch{
            val base64 = getLocalBase64(studentDetails.enroll_number)
            val profileBitmapResult = NfcUtil.convertBase64Testing(base64)
            if(profileBitmapResult.first != SUCCESSFUL_KEY) {
                // TODO: ERROR
                updateLoadingViews(isLoading = false)
                binding.cvError.visibility = View.VISIBLE
                binding.tvErrorMessage.text = profileBitmapResult.first

            } else {
                if(profileBitmapResult.second != null) {
                    binding.imageViewStudentProfile.setImageBitmap(profileBitmapResult.second)
                }
            }
        }

    }

    private fun updateLoadingViews(isLoading: Boolean) {
        binding.cvLoading.visibility = if(isLoading) View.VISIBLE else View.INVISIBLE
        binding.cvError.visibility = View.INVISIBLE
        binding.cvLayoutScanning.visibility = View.INVISIBLE

        binding.cvStudentDetails.visibility = View.INVISIBLE
        binding.layoutScanResult.visibility = View.INVISIBLE

        binding.lottieSubscribed.visibility = View.INVISIBLE
        binding.lottieNotSubscribed.visibility = View.INVISIBLE
        binding.lottieOtherBus.visibility = View.INVISIBLE

        binding.tvBusScanStatus.text = ""

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}