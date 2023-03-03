package com.example.nfcsmartcard.ui

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.example.nfcsmartcard.R
import com.example.nfcsmartcard.databinding.ActivityScannerBinding
import com.example.nfcsmartcard.utils.NetworkResult
import com.example.nfcsmartcard.utils.NfcUtil
import com.example.nfcsmartcard.utils.ScanStatus
import com.example.nfcsmartcard.utils.TokenManager
import com.example.nfcsmartcard.viewModels.StudentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScannerActivity : AppCompatActivity() {

    private val TAG = ScannerActivity::class.java.simpleName + "TESTING"

    private var _binding: ActivityScannerBinding? = null
    private val binding get() = _binding!!

    private val studentViewModel by viewModels<StudentViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager

    // FOR NFC STUFF
    private lateinit var pendingIntent: PendingIntent
    private lateinit var readFilters: Array<IntentFilter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentViewModel.getStudentListResponse()
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
            binding.tvEnrollment.text = "ERROR"
            binding.tvScanResult.text = nfcResult.second
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
        binding.tvRouteCategory.text = "Bus Route = ${userDetails[1]}"

        binding.btnTryAgain.setOnClickListener {
            studentViewModel.getStudentListResponse()
        }

//        binding.layoutEnrollment.setOnClickListener {
        binding.btnViewMore.setOnClickListener {
            studentViewModel.getStudentDetailsResponse()
            binding.layoutScanning.visibility = View.INVISIBLE
            binding.cvStudentDetails.visibility = View.VISIBLE
//            binding.layoutEnrollment.isEnabled = false
            binding.btnViewMore.isEnabled = false
        }

        binding.tvCancel.setOnClickListener {
            binding.cvStudentDetails.visibility = View.GONE
            binding.layoutScanning.visibility = View.VISIBLE
        }

    }

    private fun setupObservers() {
        studentViewModel.studentListLiveData.observe(this) {
            updateLoadingViews(isLoading = false)
            when(it) {
                is NetworkResult.Loading -> { updateLoadingViews(isLoading = true)}
                is NetworkResult.Error -> {
                    binding.cvError.visibility = View.VISIBLE
                    binding.tvErrorMessage.text = it.message
                }
                is NetworkResult.Success -> {
                    studentViewModel.setValidStudentList(it.data!!.cityList[0].student_list)
                    studentViewModel.setInvalidStudentList(it.data.notSubscribed[0].student_list)

                    binding.layoutScanning.visibility = View.VISIBLE
                    binding.lottieTapCard.playAnimation()

                    Log.i(TAG, "validStudents = ${studentViewModel.validStudentList}")
                    Log.i(TAG, "invalidStudents = ${studentViewModel.invalidStudentList}")
                }
            }
        }

        studentViewModel.scanStatusLiveData.observe(this) {
            binding.cvStudentDetails.visibility = View.GONE
            binding.layoutScanResult.visibility = View.VISIBLE
            binding.layoutScanning.visibility = View.VISIBLE
            binding.lottieSubscribed.visibility = View.INVISIBLE
            binding.lottieNotSubscribed.visibility = View.INVISIBLE
            binding.lottieOtherBus.visibility = View.INVISIBLE

//            binding.layoutEnrollment.isEnabled = true
            binding.btnViewMore.isEnabled = true

            binding.tvEnrollment.text = it.enrollment
            when(it) {
                is ScanStatus.Subscribed -> {
                    binding.lottieSubscribed.visibility = View.VISIBLE
                    binding.lottieSubscribed.playAnimation()
                    binding.tvScanResult.text = getString(R.string.scan_result_subscribed_text)
                }
                is ScanStatus.NotSubscribed -> {
                    binding.lottieNotSubscribed.visibility = View.VISIBLE
                    binding.lottieNotSubscribed.playAnimation()
                    binding.tvScanResult.text = getString(R.string.scan_result_not_subscribed_text)
                }
                is ScanStatus.OtherBus -> {
                    binding.lottieOtherBus.visibility = View.VISIBLE
                    binding.lottieOtherBus.playAnimation()
                    binding.tvScanResult.text = getString(R.string.scan_result_other_bus_text)
                }
            }
        }

        studentViewModel.studentDetailsLiveData.observe(this) {
            binding.progressBar2.visibility = View.GONE
            binding.tvStudentName.visibility = View.GONE
            binding.tvStudentRouteCategory.visibility = View.GONE

            when(it) {
                is NetworkResult.Loading -> {
                    binding.progressBar2.visibility = View.VISIBLE
                    binding.tvStudentDetailStatus.text = getString(R.string.fetching_details_text)
                }
                is NetworkResult.Error -> {
                    binding.tvStudentDetailStatus.text = it.message
                }
                is NetworkResult.Success -> {
                    binding.tvStudentDetailStatus.text = it.data!!.studentDetail[0].enroll_number
                    binding.tvStudentName.text = it.data.studentDetail[0].name
                    binding.tvStudentName.visibility = View.VISIBLE
                    binding.tvStudentRouteCategory.text = it.data.studentDetail[0].route_category
                    binding.tvStudentRouteCategory.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateLoadingViews(isLoading: Boolean) {
        binding.cvLoading.visibility = if(isLoading) View.VISIBLE else View.INVISIBLE
        binding.cvError.visibility = View.INVISIBLE
        binding.layoutScanning.visibility = View.INVISIBLE


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}