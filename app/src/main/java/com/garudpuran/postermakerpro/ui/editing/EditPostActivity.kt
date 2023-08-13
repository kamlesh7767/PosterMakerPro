package com.garudpuran.postermakerpro.ui.editing

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.TypefaceCompat
import androidx.lifecycle.lifecycleScope
import codes.side.andcolorpicker.converter.toColorInt
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.ActivityEditPostBinding
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.ui.commonui.HomeResources
import com.garudpuran.postermakerpro.ui.commonui.models.EditFragOptionsModel
import com.garudpuran.postermakerpro.ui.editing.adapter.EditFragOptionsAdapter
import com.garudpuran.postermakerpro.ui.editing.adapter.OptionFramesRcAdapter
import com.garudpuran.postermakerpro.utils.Status
import com.garudpuran.postermakerpro.utils.Utils
import com.garudpuran.postermakerpro.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class EditPostActivity : AppCompatActivity(),
    EditFragOptionsAdapter.EditOptionsListener,
    OptionFramesRcAdapter.OptionFramesRcAdapterListener {
    private lateinit var binding:ActivityEditPostBinding

    private val userViewModel: UserViewModel by viewModels()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var userData = UserPersonalProfileModel()
    private var selectedFramePosition = 0

    private lateinit var userPic: CircleImageView
    private lateinit var userName: TextView
    private lateinit var userDes: TextView

    private val TAG = "EditPostActivity"

    fun isEmptyFrameSelected():Boolean{
        return selectedFramePosition == 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeUserData()
        binding.titlePostTv.text = intent.getStringExtra("engTitle")
        Glide
            .with(this)
            .load(intent.getStringExtra("imageUrl"))
            .centerCrop()
            .into(binding.imageView)

        binding.downloadBtn.setOnClickListener {
            val combinedBitmap = viewToBitmap(binding.fullPostLayout)
            saveImageToGallery(combinedBitmap!!, "combined_image.jpg")
        }

        binding.optionProfileImage.editProfileImageSizeSlider.addOnChangeListener { slider, value, fromUser ->

            changeUserPicSize(value)
        }

        binding.optionName.editUserNameSizeSlider.addOnChangeListener { slider, value, fromUser ->

           changeUserNameSize(value)
        }

        binding.optionContacts.editUserMobileSizeSlider.addOnChangeListener { slider, value, fromUser ->

            changeUserMobileSize(value)
        }

        val nameColorSelectorGroup = PickerGroup<IntegerHSLColor>().also {
            it.registerPickers(
                binding.optionName.nameHueSeekBar,
                binding.optionName.nameLightnessSeekBar
            )
        }

        val mobileColorSelectorGroup = PickerGroup<IntegerHSLColor>().also {
            it.registerPickers(
                binding.optionContacts.mobileHueSeekBar,
                binding.optionContacts.mobileLightnessSeekBar
            )
        }

        nameColorSelectorGroup.addListener(object : ColorSeekBar.OnColorPickListener<ColorSeekBar<IntegerHSLColor>, IntegerHSLColor> {
            override fun onColorChanged(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int
            ) {
                userName.setTextColor(color.toColorInt())
            }

            override fun onColorPicked(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int,
                fromUser: Boolean
            ) {

            }

           override fun onColorPicking(
               picker: ColorSeekBar<IntegerHSLColor>,
               color: IntegerHSLColor,
               value: Int,
               fromUser: Boolean
           ) {

           }


       })

        mobileColorSelectorGroup.addListener(object : ColorSeekBar.OnColorPickListener<ColorSeekBar<IntegerHSLColor>, IntegerHSLColor> {
            override fun onColorChanged(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int
            ) {
                userDes.setTextColor(color.toColorInt())
            }

            override fun onColorPicked(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int,
                fromUser: Boolean
            ) {

            }

            override fun onColorPicking(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int,
                fromUser: Boolean
            ) {

            }


        })

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

    private fun changeUserNameSize(size: Float) {

        userName.textSize = size
    }

    private fun changeUserMobileSize(size: Float) {
        userDes.textSize = size
    }



    private fun observeUserData() {
        val userProfilesCache = userViewModel.getUserProfileCache()
        if (userProfilesCache.value == null) {
            fetchData()
        }else{
            setUi(userProfilesCache.value!!)
        }
    }

    private fun setUi(value: UserPersonalProfileModel) {
        setOptionFrames()
        initFrameOptions()
        val adapterOptions = EditFragOptionsAdapter(this,this)
        binding.editFragOptionsList.adapter = adapterOptions
    }

    private fun fetchData() {
        this.lifecycleScope.launch {
            try {
                val trendingStoriesDeferred4 =
                    async { userViewModel.getUserProfileAsync(auth.uid!!) }

                val userDataResults = awaitAll(
                    trendingStoriesDeferred4
                )

                // Check results and proceed
                val allUserDataSuccess = userDataResults.all { it.status == Status.SUCCESS }
                if (allUserDataSuccess) {
                    userData = userDataResults[0].data!!
                    setUi(userData)
                } else {
                    // Handle errors
                }
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    private fun initNameFonts(){
        val fontNames = HomeResources.fonts()

        // Create an ArrayAdapter using the font names and a default spinner layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fontNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        binding.optionName.fontSpinner.adapter = adapter

        // Set an item selected listener for the spinner
        binding.optionName.fontSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Get the selected font name
                val selectedFontName = fontNames[position]

                // Load the selected font
                val typeface = ResourcesCompat.getFont(this@EditPostActivity, getFontResourceId(selectedFontName))

                // Apply the selected font to the TextView
                userName.typeface = typeface
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun initMobileFonts(){
        val fontNames = HomeResources.fonts()

        // Create an ArrayAdapter using the font names and a default spinner layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fontNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        binding.optionContacts.mobileFontSpinner.adapter = adapter

        // Set an item selected listener for the spinner
        binding.optionContacts.mobileFontSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Get the selected font name
                val selectedFontName = fontNames[position]

                // Load the selected font
                val typeface = ResourcesCompat.getFont(this@EditPostActivity, getFontResourceId(selectedFontName))

                // Apply the selected font to the TextView
                userDes.typeface = typeface
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
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
        Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show()
    }

    override fun onEditOptionsClicked(item: EditFragOptionsModel) {
        if(selectedFramePosition == 0){
            Utils.showToast(this,"Select a frame first!")
        }else{
            setOptionsUi(item.id)
        }

    }



    private fun setOptionsUi(id: Int) {
        when(id){
            0->{
                setOptionFrames()
            }
            1->{
                setOptionProfileImage()
            }
            2->{
                setOptionIcon()
            }
            3->{
                setOptionName()
            }
            4->{
                setOptionAddress()
            }
            5->{
                setOptionContacts()
            }


        }

    }

    private fun setOptionProfileImage() {
        binding.optionProfileImage.root.visibility = View.VISIBLE
        binding.optionFrames.root.visibility = View.GONE
        binding.optionIcon.root.visibility = View.GONE
        binding.optionName.root.visibility = View.GONE
        binding.optionAddress.root.visibility = View.GONE
        binding.optionContacts.root.visibility = View.GONE
    }

    private fun setOptionContacts() {
        binding.optionProfileImage.root.visibility = View.GONE
        binding.optionFrames.root.visibility = View.GONE
        binding.optionIcon.root.visibility = View.GONE
        binding.optionName.root.visibility = View.GONE
        binding.optionAddress.root.visibility = View.GONE
        binding.optionContacts.root.visibility = View.VISIBLE
        initMobileFonts()
    }

    private fun setOptionAddress() {
        binding.optionProfileImage.root.visibility = View.GONE
        binding.optionFrames.root.visibility = View.GONE
        binding.optionIcon.root.visibility = View.GONE
        binding.optionName.root.visibility = View.GONE
        binding.optionAddress.root.visibility = View.VISIBLE
        binding.optionContacts.root.visibility = View.GONE
    }

    private fun setOptionName() {
        binding.optionProfileImage.root.visibility = View.GONE
        binding.optionFrames.root.visibility = View.GONE
        binding.optionIcon.root.visibility = View.GONE
        binding.optionName.root.visibility = View.VISIBLE
        binding.optionAddress.root.visibility = View.GONE
        binding.optionContacts.root.visibility = View.GONE
        initNameFonts()
    }

    private fun setOptionIcon() {
        binding.optionProfileImage.root.visibility = View.GONE
        binding.optionFrames.root.visibility = View.GONE
        binding.optionIcon.root.visibility = View.VISIBLE
        binding.optionName.root.visibility = View.GONE
        binding.optionAddress.root.visibility = View.GONE
        binding.optionContacts.root.visibility = View.GONE
    }

    private fun setOptionFrames() {
        binding.optionProfileImage.root.visibility = View.GONE
        binding.optionFrames.root.visibility = View.VISIBLE
        binding.optionIcon.root.visibility = View.GONE
        binding.optionName.root.visibility = View.GONE
        binding.optionAddress.root.visibility = View.GONE
        binding.optionContacts.root.visibility = View.GONE
        //initFrameOptions()
    }

    private fun initFrameOptions() {
val ady = OptionFramesRcAdapter(this,this)
        binding.optionFrames.editOptionsFramesRcView.adapter = ady
    }

    override fun onOptionFramesClicked(position: Int) {
        selectedFramePosition = position
        binding.fullFrameLayout.removeAllViews()
        if(position!=0){
            val view = LayoutInflater.from(this).inflate( HomeResources.fullFrames()[position], null)
            userPic = view.findViewById<CircleImageView>(R.id.story_user_profile_pic)
            userName = view.findViewById<TextView>(R.id.story_user_name_tv)
            userDes = view.findViewById<TextView>(R.id.story_user_desp_tv)
//  downloadAndSetFont("font_text_one.ttf")
            userDes.text = userData.mobile_number
            userName.text = userData.name
            Glide.with(this).load(userData.profile_image_url).into(userPic)

            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            view.layoutParams = layoutParams
            binding.fullFrameLayout.addView(view)
        }

    }




}