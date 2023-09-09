package com.garudpuran.postermakerpro.ui.editing

import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.ActivityEditStoryBinding
import com.garudpuran.postermakerpro.models.TrendingStoriesItemModel
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.ui.commonui.HomeResources
import com.garudpuran.postermakerpro.ui.editing.adapter.ViewPagerAdapter
import com.garudpuran.postermakerpro.ui.home.HomeViewModel
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@AndroidEntryPoint
class EditStoryActivity : AppCompatActivity()
    {
    private lateinit var binding: ActivityEditStoryBinding

    private val userViewModel: UserViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var userData = UserPersonalProfileModel()
    private var trendingStories = listOf<TrendingStoriesItemModel>()
        private var mInterstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeUserData()




        //binding.titlePostTv.text = intent.getStringExtra("engTitle")

        binding.downloadBtn.setOnClickListener {
         askForStoragePermissions(1)

        }
        binding.shareBtn.setOnClickListener {
          askForStoragePermissions(2)

        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        showAd()
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

        private fun askForStoragePermissions(value:Int){
            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.Q){
               accordingToTheBTNPOS(value,true)
            }else{
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),333)
                }else{
                    accordingToTheBTNPOS(value,false)
                }
            }
        }


        private fun accordingToTheBTNPOS(value:Int,newOrOld:Boolean){
            if(newOrOld){
                if(value == 1){
                    val combinedBitmap = viewToBitmap(binding.completeStoryLayout)
                    saveImageToGallery(combinedBitmap!!, System.currentTimeMillis().toString())
                }else{
                    val combinedBitmap = viewToBitmap(binding.completeStoryLayout)
                    saveAndShareImage(combinedBitmap!!, System.currentTimeMillis().toString())
                }
            }else{
                if(value == 1){
                    saveToOldGallery()
                }else{
                    saveAndShareOld()
                }
            }

        }
private  fun saveToOldGallery(){
    val bitmap = viewToBitmap(binding.completeStoryLayout)
    val imageFileName = System.currentTimeMillis().toString() + ".jpg"

    val cw = ContextWrapper(applicationContext)
    // path to /data/data/yourapp/app_data/imageDir
    // path to /data/data/yourapp/app_data/imageDir
    val directory = cw.getDir("imageDir", MODE_PRIVATE)
    // Create imageDir
    // Create imageDir
    val myPath = File(directory, "${imageFileName}.jpg")

    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(myPath)
        // Use the compress method on the BitMap object to write image to the OutputStream
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    } finally {
        try {
            fos!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    //return directory.absolutePath

// Now the image will be visible in the gallery

}

        private fun saveAndShareOld(){
            val bitmap = viewToBitmap(binding.completeStoryLayout)
            val imageFileName = System.currentTimeMillis().toString() + ".jpg"

            val imageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Pictures")
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }

            val imageFile = File(imageDir, imageFileName)

            val outputStream = FileOutputStream(imageFile)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()

            val fileUri = FileProvider.getUriForFile(
                this,
                "com.garudpuran.postermakerpro.fileprovider",
                imageFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
            startActivity(Intent.createChooser(shareIntent, "Share Image"))

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
        Toast.makeText(this, getString(R.string.downloaded), Toast.LENGTH_SHORT).show()
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        }
    }

        private fun showAd() {
            var adRequest = AdRequest.Builder().build()

            InterstitialAd.load(this,"ca-app-pub-4135756483743089/1376225141", adRequest, object : InterstitialAdLoadCallback() {
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


        private fun saveAndShareImage(bitmap: Bitmap, fileName: String){
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

            // Specify the package name of WhatsApp to ensure sharing through WhatsApp
           // shareIntent.setPackage("com.whatsapp")

            // You can add more conditions for other platforms here if needed

            startActivity(Intent.createChooser(shareIntent, "Share Image"))
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            }
        }

    private fun observeUserData() {
        val userProfilesCache = userViewModel.getUserProfileCache()
        val trendingStoriesCache = homeViewModel.getTrendingStoriesCache()
        if (userProfilesCache.value == null && trendingStoriesCache.value == null) {
            fetchData()
        } else {
            setUi(userProfilesCache.value!!)
            trendingStories = trendingStoriesCache.value!!
            initImageAdapter(trendingStoriesCache.value)
        }
    }

        private fun initImageAdapter(value: List<TrendingStoriesItemModel>?) {
            val adapter = ImagePagerAdapter(value!!.map { it.image_url })
            binding.imgViewPager.adapter = adapter
            binding.imgViewPager.currentItem = value.indexOfFirst { it.Id == intent.getStringExtra("trending_story_id")!! }
Log.d("SCROLL_POSITION",value.indexOfFirst { it.Id == intent.getStringExtra("trending_story_id")!! }.toString())
            binding.imgViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    val item = trendingStories[position]
                    when(getSelectedLanguage()){
                        "en"->    binding.titlePostTv.text = item.title_eng
                        "mr"->    binding.titlePostTv.text = item.title_mar
                        "hi"->    binding.titlePostTv.text = item.title_hin
                    }
                }

                override fun onPageSelected(position: Int) {

                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })
        }

        private fun getSelectedLanguage(): String {
            val authPref = this.getSharedPreferences(
                AppPrefConstants.LANGUAGE_PREF,
                Context.MODE_PRIVATE
            )
            return authPref.getString("language", "")!!
        }

        private fun setUi(value: UserPersonalProfileModel) {
        val frameList = listOf(R.layout.frame_1,R.layout.frame_2,R.layout.frame_3,R.layout.frame_4,R.layout.frame_5,R.layout.frame_6,R.layout.frame_7,R.layout.frame_8_topless,R.layout.frame_9,R.layout.frame_10,R.layout.frame_11,R.layout.frame_12,R.layout.frame_13,R.layout.frame_14,R.layout.frame_15,R.layout.frame_16_topless,R.layout.frame_17,R.layout.frame_18,R.layout.frame_19)
        val adapter = ViewPagerAdapter(value, frameList)
        binding.viewpager.adapter = adapter

    }

    private fun fetchData() {
        this.lifecycleScope.launch {
            try {
                val trendingStoriesDeferred4 = async { userViewModel.getUserProfileAsync(auth.uid!!) }
                val trendingStoriesDeferred = async { homeViewModel.getAllTrendingStoriesAsync() }

                val userDataResults = awaitAll(
                    trendingStoriesDeferred4
                )
                val trendingResults = awaitAll(trendingStoriesDeferred)

                // Check results and proceed
                val allUserDataSuccess = userDataResults.all { it.status == Status.SUCCESS }
                val trendingDataSuccess = trendingResults[0].status == Status.SUCCESS
                if (allUserDataSuccess) {
                    userData = userDataResults[0].data!!
                    setUi(userData)
                } else {
                    // Handle errors
                }

                if (trendingDataSuccess) {
                    val trendingStoriesData =
                        trendingResults[0].data
                    val visibleTrendingStoriesList = trendingStoriesData!!.filter { it.visibility!! }
                    if (visibleTrendingStoriesList.isNotEmpty()) {
                        trendingStories = visibleTrendingStoriesList
                        initImageAdapter(visibleTrendingStoriesList)
                        homeViewModel.setTrendingStoriesCache(visibleTrendingStoriesList)

                    } else {
                        // no trending stories

                    }
                }


            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }


        private inner class ImagePagerAdapter(private val list:List<String>) : PagerAdapter() {
            override fun getCount(): Int {
                return list.size
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val imageView = ImageView(container.context)
               Glide.with(container.context).load(list[position]).into(imageView)
                container.addView(imageView)
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                return imageView
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view === `object`
            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }




}