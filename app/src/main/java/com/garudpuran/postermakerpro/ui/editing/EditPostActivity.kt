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
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    private var selectedFramePosition = 0
    private var isNameFontAdded = false
    private var isAddressFontAdded = false
    private var isMobileFontAdded = false

    private lateinit var userPic: CircleImageView
    private lateinit var userName: TextView
    private lateinit var userDes: TextView
    private lateinit var userAddress: TextView

    private val TAG = "EditPostActivity"

    fun isEmptyFrameSelected():Boolean{
        return selectedFramePosition == 0
    }

    private val profilePicsContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
               userPic.setImageURI(it)
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
       setUi()
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

        binding.optionProfileImage.editFragOptionsProfileChangeImageBtn.setOnClickListener {
            profilePicsContract.launch("image/*")
        }

        binding.optionProfileImage.editFragOptionsProfileResetImageBtn.setOnClickListener {
            Glide
                .with(this)
                .load(intent.getStringExtra("imageUrl"))
                .centerCrop()
                .into(userPic)
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

        binding.optionAddress.editUserAddressSizeSlider.addOnChangeListener { slider, value, fromUser ->

            changeUserAddressSize(value)
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

        val addressColorSelectorGroup = PickerGroup<IntegerHSLColor>().also {
            it.registerPickers(
                binding.optionAddress.addressHueSeekBar,
                binding.optionAddress.addressLightnessSeekBar
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

        addressColorSelectorGroup.addListener(object : ColorSeekBar.OnColorPickListener<ColorSeekBar<IntegerHSLColor>, IntegerHSLColor> {
            override fun onColorChanged(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int
            ) {
                userAddress.setTextColor(color.toColorInt())
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


        binding.optionProfileImage.editFragOptionsProfileHideShowImageBtn.setOnCheckedChangeListener { p0, p1 ->
            if(p1){
                userPic.visibility = View.VISIBLE
            }else{
                userPic.visibility = View.GONE
            }

        }

        binding.optionAddress.editFragOptionsAddressHideShowBtn.setOnCheckedChangeListener { p0, p1 ->
            if(p1){
                userAddress.visibility = View.VISIBLE
            }else{
                userAddress.visibility = View.GONE
            }

        }

        binding.optionContacts.editFragOptionsMobileHideShowBtn.setOnCheckedChangeListener { p0, p1 ->
            if(p1){
                 userDes.visibility = View.VISIBLE
            }else{
                userDes.visibility = View.GONE
            }

        }

        binding.optionName.editFragOptionsNameHideShowBtn.setOnCheckedChangeListener { p0, p1 ->
            if(p1){
                userName.visibility = View.VISIBLE
            }else{
                userName.visibility = View.GONE
            }

        }

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

    private fun changeUserAddressSize(size: Float) {
        userAddress.textSize = size
    }





    private fun setUi() {
        setOptionFrames()
        initFrameOptions()
        val adapterOptions = EditFragOptionsAdapter(this,this)
        binding.editFragOptionsList.adapter = adapterOptions
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


                userName.typeface = typeface
                isNameFontAdded = true
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
                isMobileFontAdded = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun initAddressFonts(){
        val fontNames = HomeResources.fonts()

        // Create an ArrayAdapter using the font names and a default spinner layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fontNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        binding.optionAddress.fontSpinner.adapter = adapter

        // Set an item selected listener for the spinner
        binding.optionAddress.fontSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Get the selected font name
                val selectedFontName = fontNames[position]

                // Load the selected font
                val typeface = ResourcesCompat.getFont(this@EditPostActivity, getFontResourceId(selectedFontName))

                // Apply the selected font to the TextView
                userAddress.typeface = typeface
                isAddressFontAdded = true
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
        if(!isMobileFontAdded){
            initMobileFonts()
        }

    }

    private fun setOptionAddress() {
        binding.optionProfileImage.root.visibility = View.GONE
        binding.optionFrames.root.visibility = View.GONE
        binding.optionIcon.root.visibility = View.GONE
        binding.optionName.root.visibility = View.GONE
        binding.optionAddress.root.visibility = View.VISIBLE
        binding.optionContacts.root.visibility = View.GONE
        if(!isAddressFontAdded){
            initAddressFonts()
        }
    }

    private fun setOptionName() {
        binding.optionProfileImage.root.visibility = View.GONE
        binding.optionFrames.root.visibility = View.GONE
        binding.optionIcon.root.visibility = View.GONE
        binding.optionName.root.visibility = View.VISIBLE
        binding.optionAddress.root.visibility = View.GONE
        binding.optionContacts.root.visibility = View.GONE
        if(!isNameFontAdded){
            initNameFonts()
        }

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
            userPic = view.findViewById<CircleImageView>(R.id.user_profile_pic_iv)
            userName = view.findViewById<TextView>(R.id.user_name_tv)
            userDes = view.findViewById<TextView>(R.id.user_mobile_tv)
            userAddress = view.findViewById<TextView>(R.id.user_address_tv)
//  downloadAndSetFont("font_text_one.ttf")
            userDes.text = "Mob No: "+intent.getStringExtra("profileMobileNumber")
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




}