package com.garudpuran.postermakerpro.ui.editing

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.ActivityEditStoryBinding
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.TrendingStoriesItemModel
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.ui.commonui.DownloadAndShareCustomDialog
import com.garudpuran.postermakerpro.ui.editing.adapter.ViewPagerAdapter
import com.garudpuran.postermakerpro.ui.home.HomeViewModel
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import com.garudpuran.postermakerpro.utils.ResponseStrings
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.utils.Utils
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class EditStoryActivity : AppCompatActivity()
    {
    private lateinit var binding: ActivityEditStoryBinding

    private val userViewModel: UserViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var userData = UserPersonalProfileModel()
    private var trendingStories = listOf<TrendingStoriesItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeUserData()

        //binding.titlePostTv.text = intent.getStringExtra("engTitle")

        binding.downloadBtn.setOnClickListener {
            val combinedBitmap = viewToBitmap(binding.completeStoryLayout)
            saveImageToGallery(combinedBitmap!!, System.currentTimeMillis().toString())

        }
        binding.shareBtn.setOnClickListener {
            val combinedBitmap = viewToBitmap(binding.completeStoryLayout)
            saveAndShareImage(combinedBitmap!!, System.currentTimeMillis().toString())

        }

        binding.backBtn.setOnClickListener {
            finish()
        }


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
        Toast.makeText(this, getString(R.string.downloaded), Toast.LENGTH_SHORT).show()
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

        val frameList = listOf(R.layout.frame_1,R.layout.frame_2,R.layout.frame_3,R.layout.frame_4,R.layout.frame_5,R.layout.frame_6)
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




}