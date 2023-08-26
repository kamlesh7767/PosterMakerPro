package com.garudpuran.postermakerpro.ui.editing

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import codes.side.andcolorpicker.converter.toColorInt
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.ActivityEditPostBinding
import com.garudpuran.postermakerpro.ui.commonui.ErrorDialogFrag
import com.garudpuran.postermakerpro.ui.commonui.HomeResources
import com.garudpuran.postermakerpro.ui.commonui.models.EditFragOptionsModel
import com.garudpuran.postermakerpro.ui.editing.adapter.EditFragOptionsAdapter
import com.garudpuran.postermakerpro.ui.editing.adapter.OptionFramesRcAdapter
import com.garudpuran.postermakerpro.utils.Utils
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

@AndroidEntryPoint
class EditPostActivity : AppCompatActivity(),
    OptionFramesRcAdapter.OptionFramesRcAdapterListener, ErrorDialogFrag.ErrorDialogListener {
    private lateinit var binding: ActivityEditPostBinding
    private var selectedFramePosition = 0
    private val PROFILE_CROP_REQUEST_CODE = 101
    private val ICON_CROP_REQUEST_CODE = 202

    private lateinit var userPic: CircleImageView
    private lateinit var userName: TextView
    private lateinit var userDes: TextView
    private lateinit var userAddress: TextView

    private var offsetX = 0
    private var offsetY = 0

    private val TAG = "EditPostActivity"

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
        }

        binding.shareBtn.setOnClickListener {
            val combinedBitmap = viewToBitmap(binding.fullPostLayout)
            saveAndShareImage(combinedBitmap!!, System.currentTimeMillis().toString())
        }

        binding.backBtn.setOnClickListener {
            finish()
        }







        // options icon
















    }

    private fun getFontResourceId(fontName: String): Int {
        return resources.getIdentifier(fontName, "font", packageName)
    }

    private fun changeUserPicSize(size: Float) {
        val layoutParams = userPic.layoutParams
        val density = this.resources.displayMetrics.density
        val pxValue = (size * density + 0.5f).toInt()
        layoutParams.width = pxValue
        layoutParams.height = pxValue
        userPic.layoutParams = layoutParams
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
            userPic.setImageURI(croppedUri)
        } else if (requestCode == ICON_CROP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val croppedUri = UCrop.getOutput(data!!)
            binding.iconIv.setImageURI(croppedUri)
        }
    }








    override fun onOptionFramesClicked(position: Int) {
        selectedFramePosition = position
        binding.fullFrameLayout.removeAllViews()
        if (position != 0) {
            val view = LayoutInflater.from(this).inflate(HomeResources.fullFrames()[position], null)
            userPic = view.findViewById<CircleImageView>(R.id.user_profile_pic_iv)
            userName = view.findViewById<TextView>(R.id.user_name_tv)
            userDes = view.findViewById<TextView>(R.id.user_mobile_tv)
            userAddress = view.findViewById<TextView>(R.id.user_address_tv)
//  downloadAndSetFont("font_text_one.ttf")
            userDes.text = "Mob No: " + intent.getStringExtra("profileMobileNumber")
            userName.text = intent.getStringExtra("profileName")
            userAddress.text = intent.getStringExtra("profileAddress")
            Glide.with(this).load(intent.getStringExtra("profileLogoUrl")).into(userPic)

            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            view.layoutParams = layoutParams
            binding.fullFrameLayout.addView(view)
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


    private fun setErrorDialog() {
        binding.progress.root.visibility = View.GONE
        val errorD = ErrorDialogFrag(this)
        errorD.show(supportFragmentManager, "ErrorDialogFrag")

    }

    override fun onDialogDismissed() {
        finish()
    }


}