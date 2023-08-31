package com.garudpuran.postermakerpro.ui.editing


import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.FloatMath
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.drawToBitmap
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.ActivityEditPostBinding
import com.garudpuran.postermakerpro.ui.commonui.ErrorDialogFrag
import com.garudpuran.postermakerpro.ui.commonui.HomeResources
import com.garudpuran.postermakerpro.ui.editing.adapter.EditingPostOptionsVPAdapter
import com.garudpuran.postermakerpro.ui.editing.options.OptionsAddressFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsFramesFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsLogoFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsMobileNumberFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsProfilePhotoFragment
import com.garudpuran.postermakerpro.ui.editing.options.OptionsUserNameFragment
import com.garudpuran.postermakerpro.utils.Utils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.slowmac.autobackgroundremover.BackgroundRemover
import com.slowmac.autobackgroundremover.OnBackgroundChangeListener
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import kotlin.math.sqrt


@AndroidEntryPoint
class EditPostActivity : AppCompatActivity(),
    ErrorDialogFrag.ErrorDialogListener,
    OptionsFramesFragment.OptionFramesListener,
    OptionsProfilePhotoFragment.OptionsProfilePicListener,
    OptionsLogoFragment.OptionsLogoListener,
    OptionsUserNameFragment.OptionsNameListener,
    OptionsAddressFragment.OptionsAddressListener,
    OptionsMobileNumberFragment.OptionsMoNoListener {
    private lateinit var binding: ActivityEditPostBinding
    private var selectedFramePosition = 0
    private val PROFILE_CROP_REQUEST_CODE = 101
    private val ICON_CROP_REQUEST_CODE = 202
    private val MASK_CROP_REQUEST_CODE = 303

    private var userPic: CircleImageView? = null
    private  var userName: TextView? = null
    private  var userDes: TextView? = null
    private  var userAddress: TextView? = null
    private  var changedURI = ""
    private  var changedBitmap: Bitmap? = null

    private var offsetX = 0
    private var offsetY = 0

    private var uPoffsetX = 0
    private var uPoffsetY = 0

    private val TAG = "EditPostActivity"


    var lastEvent: FloatArray? = null
    var d = 0f
    var newRot = 0f
    private val matrix: Matrix = Matrix()
    private val savedMatrix: Matrix = Matrix()
    var fileNAME: String? = null
    var framePos = 0

    private val scale = 0f
    private val newDist = 0f

    // We can be in one of these 3 states
    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2
    private var mode = NONE

    // Remember some things for zooming
    private val start = PointF()
    private val mid = PointF()
    var oldDist = 1f

    val TOUCH_THRESHOLD = 10 // Adjust this value according to your needs

    fun onTouch(v: View, event: MotionEvent): Boolean {
        val view = v as ImageView
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                start[event.x] = event.y
                mode = DRAG
                lastEvent = null
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = ZOOM
                }
                lastEvent = FloatArray(4)
                lastEvent!![0] = event.getX(0)
                lastEvent!![1] = event.getX(1)
                lastEvent!![2] = event.getY(0)
                lastEvent!![3] = event.getY(1)
                d = rotation(event)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (mode == DRAG) {
                    val end = PointF(event.x, event.y)
                    if (distance(start, end) < TOUCH_THRESHOLD) {

                        val idList = listOf<String>("cRpuFR8scMqGT2a9VHFO","0oEgG8hP5EX8Mi1XIMWL","PL33pn07rWAyjyFW57Yg")
                      if( idList.contains(intent.getStringExtra("postCatId"))){
                          maskedPicsContract.launch("image/*")
                      }


                    }
                }
                mode = NONE
                lastEvent = null
            }

            MotionEvent.ACTION_MOVE -> if (mode === DRAG) {
                // ...
                matrix.set(savedMatrix)
                matrix.postTranslate(
                    event.x - start.x, event.y
                            - start.y
                )
            } else if (mode === ZOOM && event.pointerCount == 2) {
                val newDist: Float = spacing(event)
                matrix.set(savedMatrix)
                if (newDist > 10f) {
                    val scale = newDist / oldDist
                    matrix.postScale(scale, scale, mid.x, mid.y)
                }
                if (lastEvent != null) {
                    newRot = rotation(event)
                    val r = newRot - d
                    matrix.postRotate(
                        r, (view.measuredWidth / 2).toFloat(),
                        (
                                view.measuredHeight / 2).toFloat()
                    )
                }
            }
        }
        view.imageMatrix = matrix
        return true
    }

    private fun distance(start: PointF, end: PointF): Float {
        val dx = end.x - start.x
        val dy = end.y - start.y
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }


    private fun rotation(event: MotionEvent): Float {
        val delta_x = (event.getX(0) - event.getX(1)).toDouble()
        val delta_y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = Math.atan2(delta_y, delta_x)
        return Math.toDegrees(radians).toFloat()
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    fun isEmptyFrameSelected(): Boolean {
        return selectedFramePosition == 0
    }

    private val profilePicsContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                startCrop(it, PROFILE_CROP_REQUEST_CODE)
                //userPic.setImageURI(it)
            }
        }

    private val maskedPicsContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                //startCrop(it, MASK_CROP_REQUEST_CODE)
                resetMatrix()
                binding.maskedIv.setImageURI(it)
                binding.maskedAddIv.visibility = View.GONE
            }
        }

    private val iconPicsContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                startCrop(it, ICON_CROP_REQUEST_CODE)
                //binding.iconIv.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUi()
showAd()

binding.maskedIv.setOnTouchListener { v, event ->
    onTouch(v, event)
}




        binding.iconIv.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    offsetX = event.rawX.toInt() - view.x.toInt()
                    offsetY = event.rawY.toInt() - view.y.toInt()
                }

                MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX.toInt() - offsetX
                    val newY = event.rawY.toInt() - offsetY

                    val parentLayout = binding.iconIv.parent as ConstraintLayout
                    val maxX = parentLayout.width - view.width
                    val maxY = parentLayout.height - view.height

                    // Ensure the image view stays within bounds
                    val constrainedX = newX.coerceIn(0, maxX)
                    val constrainedY = newY.coerceIn(0, maxY)

                    view.x = constrainedX.toFloat()
                    view.y = constrainedY.toFloat()
                }
            }
            true
        }






        Glide
            .with(this)
            .load(intent.getStringExtra("imageUrl"))
            .centerCrop()
            .into(binding.imageView)


        Glide
            .with(this)
            .load(intent.getStringExtra("profileLogoUrl"))
            .centerCrop()
            .into(binding.iconIv)

        binding.downloadBtn.setOnClickListener {
            val combinedBitmap = viewToBitmap(binding.fullPostLayout)
            saveImageToGallery(combinedBitmap!!, intent.getStringExtra("engTitle")!!)
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            }
        }

        binding.shareBtn.setOnClickListener {
            val combinedBitmap = viewToBitmap(binding.fullPostLayout)
            saveAndShareImage(combinedBitmap!!, System.currentTimeMillis().toString())
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            }

        }

        binding.backBtn.setOnClickListener {
            finish()
        }

    }

    private fun changeUserPicSize(size: Float) {
        val layoutParams = userPic?.layoutParams
        val density = this.resources.displayMetrics.density
        val pxValue = (size * density + 0.5f).toInt()
        layoutParams?.width = pxValue
        layoutParams?.height = pxValue
        userPic?.layoutParams = layoutParams
    }

    private fun changeIconSize(size: Float) {
        val layoutParams = binding.iconIv.layoutParams
        val density = this.resources.displayMetrics.density
        val pxValue = (size * density + 0.5f).toInt()
        layoutParams.width = pxValue
        layoutParams.height = pxValue
        binding.iconIv.layoutParams = layoutParams
    }


    private fun setUi() {


        val pagerAdapter = EditingPostOptionsVPAdapter(this, this, this, this, this, this, this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 4
        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.text = "Frames"
                1 -> tab.text = "Profile Photo"
                2 -> tab.text = "Logo"
                3 -> tab.text = "Name"
                4 -> tab.text = "Address"
                5 -> tab.text = "Mobile Number"
            }
        }.attach()
    }


    private fun startCrop(sourceUri: Uri, REQUEST_CODE: Int) {
        val destinationUri = Uri.fromFile(File(this.cacheDir, "cropped_image.jpg"))

        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f) // Set the desired aspect ratio
            .start(this, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PROFILE_CROP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val croppedUri = UCrop.getOutput(data!!)
            userPic?.setImageURI(croppedUri)
            changedURI = croppedUri.toString()
            changedBitmap = null
        } else if (requestCode == ICON_CROP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val croppedUri = UCrop.getOutput(data!!)
            binding.iconIv.setImageURI(croppedUri)
        }
        else if(requestCode == MASK_CROP_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val croppedUri = UCrop.getOutput(data!!)
            binding.maskedIv.setImageResource(R.drawable.add)
            binding.maskedIv.setImageURI(croppedUri)
        }
    }

    private fun saveAndShareImage(bitmap: Bitmap, fileName: String) {
        val resolver = this.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/")
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }


    private fun viewToBitmap(view: View): Bitmap? {
        var createBitmap: Bitmap? = null
        view.isDrawingCacheEnabled = true
        view.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        return try {
            createBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(createBitmap))
            createBitmap
        } catch (e: Exception) {
            createBitmap
        } finally {
            view.destroyDrawingCache()
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap, fileName: String) {
        val resolver = this.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/")
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
        Toast.makeText(this, "Downloaded", Toast.LENGTH_SHORT).show()
    }
    private fun resetMatrix() {
        matrix.reset()
        binding.maskedIv.imageMatrix = matrix
    }

    private fun setErrorDialog() {
        binding.progress.root.visibility = View.GONE
        val errorD = ErrorDialogFrag(this)
        errorD.show(supportFragmentManager, "ErrorDialogFrag")

    }

    override fun onDialogDismissed() {
        finish()
    }

    override fun onFrameSelected(position: Int) {
        selectedFramePosition = position
        binding.fullFrameLayout.removeAllViews()
        if (position != 0) {
            val view = LayoutInflater.from(this).inflate(HomeResources.fullFrames()[position], null)
            userPic = view.findViewById<CircleImageView>(R.id.user_profile_pic_iv)!!
            userName = view.findViewById<TextView>(R.id.user_name_tv)!!
            userDes = view.findViewById<TextView>(R.id.user_mobile_tv)!!
            userAddress = view.findViewById<TextView>(R.id.user_address_tv)!!
//  downloadAndSetFont("font_text_one.ttf")
            userDes!!.text = "Mob No: " + intent.getStringExtra("profileMobileNumber")
            userName!!.text = intent.getStringExtra("profileName")
            userAddress!!.text = intent.getStringExtra("profileAddress")

            if(changedBitmap !=null){
                userPic!!.setImageBitmap(changedBitmap)
            }else if(changedURI.isNotEmpty()){
                userPic!!.setImageURI(Uri.parse(changedURI))
            }else{
                Glide.with(this).load(intent.getStringExtra("profileLogoUrl")).into(userPic!!)
            }


            if(!monoHideVisible){
                userDes!!.visibility = View.GONE
            }
            if(!addressHideVisible){
                userAddress!!.visibility = View.GONE
            }

            if(!nameHideVisible){
                userName!!.visibility = View.GONE
            }
            if(!profilePicHideVisible){
                userPic!!.visibility = View.GONE
            }


            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            view.layoutParams = layoutParams
            binding.fullFrameLayout.addView(view)

            userPic?.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        uPoffsetX = event.rawX.toInt() - view.x.toInt()
                        uPoffsetY = event.rawY.toInt() - view.y.toInt()
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val newX = event.rawX.toInt() - uPoffsetX
                        val newY = event.rawY.toInt() - uPoffsetY

                        val parentLayout = userPic!!.parent as ConstraintLayout
                        val maxX = parentLayout.width - view.width
                        val maxY = parentLayout.height - view.height

                        // Ensure the image view stays within bounds
                        val constrainedX = newX.coerceIn(0, maxX)
                        val constrainedY = newY.coerceIn(0, maxY)

                        view.x = constrainedX.toFloat()
                        view.y = constrainedY.toFloat()
                    }
                }
                true
            }


        }
    }

    private var profilePicHideVisible = true

    override fun profilePicHideShowBtnClicked(p1: Boolean) {
        if(!isEmptyFrameSelected()){
            if (p1) {
                userPic?.visibility = View.VISIBLE
                profilePicHideVisible = true
            } else {
                userPic?.visibility = View.GONE
                profilePicHideVisible = false
            }
        }else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }

    }

    override fun profilePicSizeSliderClicked(value: Float) {
        if(!isEmptyFrameSelected()){
        changeUserPicSize(value)}
        else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun profilePicChangeClicked() {
        if(!isEmptyFrameSelected()) {
            profilePicsContract.launch("image/*")
        }else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun profilePicResetClicked() {
        if(!isEmptyFrameSelected()) {
            Glide
                .with(this)
                .load(intent.getStringExtra("profileLogoUrl"))
                .centerCrop()
                .into(userPic!!)
            changedBitmap = null
            changedURI = ""

        }else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun profilePicBgRemove() {
        val bm = userPic!!.drawToBitmap()

        BackgroundRemover.bitmapForProcessing(
            bm,false,
            object: OnBackgroundChangeListener {
                override fun onSuccess(bitmap: Bitmap) {
                   userPic?.setImageBitmap(bitmap)
                    changedBitmap = bitmap
                    changedURI = ""
                }

                override fun onFailed(exception: Exception) {
                    Utils.showToast(this@EditPostActivity,"failed")
                }
            }
        )
    }
    private var mInterstitialAd: InterstitialAd? = null

    private fun showAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-4135756483743089/2318643861", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                Log.d("ADVERT", adError.toString())
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                Log.d("ADVERT", "Shown")
            }
        })
    }

    override fun logoPicBgRemove() {
        val bm = binding.iconIv.drawToBitmap()

        BackgroundRemover.bitmapForProcessing(
            bm,false,
            object: OnBackgroundChangeListener {
                override fun onSuccess(bitmap: Bitmap) {
                    binding.iconIv.setImageBitmap(bitmap)
                }

                override fun onFailed(exception: Exception) {
                    Utils.showToast(this@EditPostActivity,"failed")
                }
            }
        )
    }

    private var logoHideVisible = true
    override fun logoHideShowBtnClicked(p1: Boolean) {
        if(!isEmptyFrameSelected()) {
            if (p1) {
                binding.iconIv.visibility = View.VISIBLE
                logoHideVisible = true
            } else {
                binding.iconIv.visibility = View.GONE
                logoHideVisible = false
            }
        }else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun logoSizeSliderClicked(value: Float) {
        if(!isEmptyFrameSelected()) {
            changeIconSize(value)
        }else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun logoChangeClicked() {
        if(!isEmptyFrameSelected()) {
            iconPicsContract.launch("image/*")
        }else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun logoResetClicked() {
        if(!isEmptyFrameSelected()) {
            Glide
                .with(this)
                .load(intent.getStringExtra("profileLogoUrl"))
                .centerCrop()
                .into(binding.iconIv)
        }else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    private var nameHideVisible = true

    override fun nameHideShowBtnClicked(p1: Boolean) {
        if(!isEmptyFrameSelected()) {
            if (p1) {
                userName!!.visibility = View.VISIBLE
                nameHideVisible = true
            } else {
                userName!!.visibility = View.GONE
                nameHideVisible = false
            }
        }else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun nameSizeSliderClicked(value: Float) {
        if(!isEmptyFrameSelected()){
        userName?.textSize = value}else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun nameFontChanged(typeface: Typeface?) {
        if(!isEmptyFrameSelected()){
        userName?.typeface = typeface
        }else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun nameFontColorChanged(toColorInt: Int) {
        if(!isEmptyFrameSelected()){
        userName?.setTextColor(toColorInt)}else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }
    private var addressHideVisible = true
    override fun addressHideShowBtnClicked(p1: Boolean) {
        if(!isEmptyFrameSelected()) {
            if (p1) {
                userAddress?.visibility = View.VISIBLE
                addressHideVisible = true
            } else {
                userAddress?.visibility = View.GONE
                addressHideVisible = false
            }
        }else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun addressSizeSliderClicked(value: Float) {
        if(!isEmptyFrameSelected()){
        userAddress?.textSize = value}else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun addressFontChanged(typeface: Typeface?) {
        if(!isEmptyFrameSelected()){
        userAddress?.typeface = typeface}else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun addressFontColorChanged(toColorInt: Int) {
        if(!isEmptyFrameSelected()){
        userAddress?.setTextColor(toColorInt)}else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }


    private var monoHideVisible = true
    override fun moNoHideShowBtnClicked(p1: Boolean) {
        if(!isEmptyFrameSelected()){
        if (p1) {
            userDes?.visibility = View.VISIBLE
            monoHideVisible = true
        } else {
            userDes?.visibility = View.GONE
            monoHideVisible = false
        }}else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun moNoSizeSliderClicked(value: Float) {
        if(!isEmptyFrameSelected()){
        userDes?.textSize = value}else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun moNoFontChanged(typeface: Typeface?) {
        if(!isEmptyFrameSelected()) {
            userDes?.typeface = typeface
        }else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }

    override fun moNoFontColorChanged(toColorInt: Int) {
        if(!isEmptyFrameSelected()) {
            userDes?.setTextColor(toColorInt)
        }else{
            Utils.showToast(this,getString(R.string.select_a_frame_first))
        }
    }


}