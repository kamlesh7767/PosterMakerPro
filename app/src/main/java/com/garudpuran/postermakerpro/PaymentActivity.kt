package com.garudpuran.postermakerpro

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.garudpuran.postermakerpro.databinding.ActivityPaymentBinding
import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.RechargeItem
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.models.SuccessfulRecharges
import com.garudpuran.postermakerpro.models.TrendingStoriesItemModel
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.ui.home.HomeViewModel
import com.garudpuran.postermakerpro.utils.ResponseStrings
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.utils.Utils
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity(), PaymentResultWithDataListener, ExternalWalletListener,
    DialogInterface.OnClickListener {

    private lateinit var binding: ActivityPaymentBinding
    private val userViewModel: UserViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private var userData = UserPersonalProfileModel()
    private var rechargeItem = RechargeItem()
    private var options = JSONObject()

    private val TAG = PaymentActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        Checkout.preload(applicationContext)
        fetchData()




    }


    private fun fetchData() {
        this.lifecycleScope.launch {
            try {

                val trendingStoriesDeferred1 = async { userViewModel.getRechargeItemAsync(intent.getStringExtra("rechargeId",)!!) }
                val trendingStoriesDeferred2 = async { userViewModel.getUserProfileAsync(auth.uid!!) }

                val results = awaitAll(
                    trendingStoriesDeferred1,
                    trendingStoriesDeferred2,
                )

                // Check results and proceed
                val allSuccess = results.all { it.status == Status.SUCCESS }
                if(allSuccess){
                    rechargeItem = results[0].data as RechargeItem
                    userData = results[1].data as UserPersonalProfileModel
                    startPayment()
                }else{
                    //handle error
                }



            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    private fun startPayment() {
        options.put("currency", "INR")
        options.put("amount", rechargeItem.amount * 100)
        options.put("send_sms_hash", true)
        val activity: Activity = this
        val co = Checkout()
        co.setKeyID("rzp_test_k5JDmaxsSPG5WK")

        try {
            Log.d(TAG, options.toString())
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        try {
            initPaymentSuccess(p0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initPaymentSuccess(p0: String?) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
val successRcg = SuccessfulRecharges(p0!!,rechargeItem,currentDate.toString())
        userData.points = userData.points + rechargeItem.points
        userData.recharges.add(successRcg)

        observeResponse()
        userViewModel.updatePersonalProfileItem("",userData)
    }

    private fun observeResponse() {
        userViewModel.onObserveUpdatePersonalProfileItemResponseData().observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progress.root.visibility = View.VISIBLE
                }

                Status.ERROR -> {
                    Utils.showToast(this,"Error!")
                    binding.progress.root.visibility = View.GONE
                }

                Status.SUCCESS -> {
                    if (it.data == ResponseStrings.SUCCESS) {
                        binding.progress.root.visibility = View.GONE
                        Utils.showToast(this,"Recharged Successfully")
                       setSuccessUI()
                    }
                }

                Status.SESSION_EXPIRE -> {

                }
            }
        }
    }

    private fun setSuccessUI() {
        finish()
    }


    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        try {
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
    }

}