package com.garudpuran.postermakerpro.ui.authentication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import com.garudpuran.postermakerpro.MainActivity
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.ActivityOtpactivityBinding
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.garudpuran.postermakerpro.utils.ResponseStrings
import com.garudpuran.postermakerpro.utils.UserReferences
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class OTPActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var verificationActive = false
    private lateinit var handler: Handler
    private var timerSeconds = 60
    private val viewModel: UserViewModel by viewModels()

    private lateinit var OTP: String
    private lateinit var binding: ActivityOtpactivityBinding
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        init()
        binding.progress.root.visibility = View.INVISIBLE

        binding.resendOtpBtn.setOnClickListener {
            resendVerificationCode()
        }

        binding.verifyOtpBtn.setOnClickListener {
            if (verificationActive) {
                if (getTypedOTP().length == 6) {
                    sendForVerification(getTypedOTP())
                }
            } else {
                Toast.makeText(this, getString(R.string.enter_correct_otp), Toast.LENGTH_SHORT)
                    .show()
            }


        }

        binding.otpEditText1.addTextChangedListener {
            val otp1 = it?.trim().toString().filter { !it.isWhitespace() }
            if (otp1.length == 1) {
                if (getTypedOTP().length == 6) {
                    verificationActive = true
                    binding.verifyOtpBtn.background =
                        AppCompatResources.getDrawable(this, R.drawable.btn_enabled)
                    binding.verifyOtpBtn.setTextColor(Color.WHITE)
                } else {
                    binding.otpEditText2.requestFocus()
                }
            } else {
                verificationActive = false
                binding.verifyOtpBtn.background =
                    AppCompatResources.getDrawable(this, R.drawable.btn_disabled)
                binding.verifyOtpBtn.setTextColor(Color.BLACK)
            }
        }
        binding.otpEditText2.addTextChangedListener {
            val otp1 = it?.trim().toString().filter { !it.isWhitespace() }
            if (otp1.length == 1) {
                if (getTypedOTP().length == 6) {
                    verificationActive = true
                    binding.verifyOtpBtn.background =
                        AppCompatResources.getDrawable(this, R.drawable.btn_enabled)
                    binding.verifyOtpBtn.setTextColor(Color.WHITE)

                } else {
                    binding.otpEditText3.requestFocus()
                }
            } else {
                verificationActive = false
                binding.verifyOtpBtn.background =
                    AppCompatResources.getDrawable(this, R.drawable.btn_disabled)
                binding.verifyOtpBtn.setTextColor(Color.BLACK)
            }
        }
        binding.otpEditText3.addTextChangedListener {
            val otp1 = it?.trim().toString().filter { !it.isWhitespace() }
            if (otp1.length == 1) {
                if (getTypedOTP().length == 6) {
                    verificationActive = true
                    binding.verifyOtpBtn.background =
                        AppCompatResources.getDrawable(this, R.drawable.btn_enabled)
                    binding.verifyOtpBtn.setTextColor(Color.WHITE)

                } else {
                    binding.otpEditText4.requestFocus()
                }
            } else {
                verificationActive = false
                binding.verifyOtpBtn.background =
                    AppCompatResources.getDrawable(this, R.drawable.btn_disabled)
                binding.verifyOtpBtn.setTextColor(Color.BLACK)
            }
        }
        binding.otpEditText4.addTextChangedListener {
            val otp1 = it?.trim().toString().filter { !it.isWhitespace() }
            if (otp1.length == 1) {
                if (getTypedOTP().length == 6) {
                    verificationActive = true
                    binding.verifyOtpBtn.background =
                        AppCompatResources.getDrawable(this, R.drawable.btn_enabled)
                    binding.verifyOtpBtn.setTextColor(Color.WHITE)

                } else {
                    binding.otpEditText5.requestFocus()
                }

            } else {
                verificationActive = false
                binding.verifyOtpBtn.background =
                    AppCompatResources.getDrawable(this, R.drawable.btn_disabled)
                binding.verifyOtpBtn.setTextColor(Color.BLACK)
            }
        }

        binding.otpEditText5.addTextChangedListener {
            val otp1 = it?.trim().toString().filter { !it.isWhitespace() }
            if (otp1.length == 1) {
                if (getTypedOTP().length == 6) {
                    verificationActive = true
                    binding.verifyOtpBtn.background =
                        AppCompatResources.getDrawable(this, R.drawable.btn_enabled)
                    binding.verifyOtpBtn.setTextColor(Color.WHITE)

                } else {
                    binding.otpEditText6.requestFocus()
                }

            } else {
                verificationActive = false
                binding.verifyOtpBtn.background =
                    AppCompatResources.getDrawable(this, R.drawable.btn_disabled)
                binding.verifyOtpBtn.setTextColor(Color.BLACK)
            }
        }


        binding.otpEditText6.addTextChangedListener {
            val otp1 = it?.trim().toString().filter { !it.isWhitespace() }
            if (otp1.length == 1) {
                if (getTypedOTP().length == 6) {
                    verificationActive = true
                    binding.verifyOtpBtn.background =
                        AppCompatResources.getDrawable(this, R.drawable.btn_enabled)
                    binding.verifyOtpBtn.setTextColor(Color.WHITE)
                } else {
                    verificationActive = false
                    binding.verifyOtpBtn.background =
                        AppCompatResources.getDrawable(this, R.drawable.btn_disabled)
                    binding.verifyOtpBtn.setTextColor(Color.BLACK)
                }

            } else {
                verificationActive = false
                binding.verifyOtpBtn.background =
                    AppCompatResources.getDrawable(this, R.drawable.btn_disabled)
                binding.verifyOtpBtn.setTextColor(Color.BLACK)
            }
        }

        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                timerSeconds--
                if (timerSeconds >= 0) {
                    updateTimerUI()
                    sendEmptyMessageDelayed(0, 1000) // Delay by 1 second (1000 milliseconds)
                } else {
                    // Timer finished
                    // Perform any desired action
                    onTimerFinished()
                }
            }
        }
        startTimer()

        binding.resendOtpBtn.setOnClickListener {
            resendVerificationCode()
        }


    }

    private fun startTimer() {
        timerSeconds = 60
        binding.resendOtpBtn.visibility = View.GONE
        binding.resendOtpTv.visibility = View.VISIBLE
        handler.sendEmptyMessage(0)
    }

    private fun updateTimerUI() {
        binding.resendOtpTv.text = getString(R.string.resend_otp_timer, timerSeconds.toString())
    }

    private fun onTimerFinished() {
        binding.resendOtpTv.visibility = View.GONE
        binding.resendOtpBtn.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun getTypedOTP(): String {
        return (binding.otpEditText1.text.toString() + binding.otpEditText2.text.toString() + binding.otpEditText3.text.toString()
                + binding.otpEditText4.text.toString() + binding.otpEditText5.text.toString() + binding.otpEditText6.text.toString())
    }

    private fun sendForVerification(typedOTP: String) {
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
            OTP, typedOTP
        )
        binding.progress.root.visibility = View.VISIBLE
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode() {
        binding.resendOtpBtn.visibility = View.GONE

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            }
            binding.progress.root.visibility = View.VISIBLE
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            OTP = verificationId
            resendToken = token
            startTimer()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //  val userData = UserPersonalProfileModel(task.result.user!!.uid, "", phoneNumber, "")
                    val uid = task.result.user!!.uid
                    val params = mutableMapOf<String, Any>()
                    params["uid"] = uid
                    params["mobile_number"] = phoneNumber

                    val db =
                        FirebaseFirestore.getInstance().collection(UserReferences.USER_MAIN_NODE)
                    db.document(uid).get().addOnSuccessListener {
                        if (it.exists()) {
                            sendToMain()
                        } else {
                            updateUserData(uid, params)
                        }
                    }


                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }

            }
    }

    private fun sendToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


    private fun init() {
        auth = FirebaseAuth.getInstance()
    }

    private fun updateUserData(id: String, data: Map<String, Any>) {
        observeUpdateUserData()
        viewModel.updateUserDetailsParams(id, data)
    }

    private fun observeUpdateUserData() {
        viewModel.onObserveUpdateUserDetailsParamsData().observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progress.root.visibility = View.VISIBLE
                }

                Status.ERROR -> {
                    binding.progress.root.visibility = View.GONE
                }

                Status.SUCCESS -> {
                    if (it.data == ResponseStrings.SUCCESS) {
                        Toast.makeText(this, getString(R.string.authenticated_successfully), Toast.LENGTH_SHORT)
                            .show()
                        sendToMain()
                    }
                }

                Status.SESSION_EXPIRE -> {

                }
            }
        }

    }
}