package com.garudpuran.postermakerpro.ui.editing

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.databinding.FragmentEditPostBinding
import com.garudpuran.postermakerpro.ui.commonui.models.EditFragOptionsModel
import com.garudpuran.postermakerpro.ui.editing.adapter.EditFragOptionsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class EditPostFragment : Fragment(), EditFragOptionsAdapter.EditOptionsListener {
    private lateinit var _binding:FragmentEditPostBinding

    private val TAG = "PosterSubCategoryFragment"
    private val binding get() = _binding
    private val args:EditPostFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide
            .with(requireActivity())
            .load(args.imageUrl)
            .centerCrop()
            .into(binding.imageView)

        binding.downloadBtn.setOnClickListener {
            val combinedBitmap = viewToBitmap(binding.fulView)
            saveImageToGallery(combinedBitmap!!, "combined_image.jpg")
        }
        setUi()

    }


    private fun setUi() {
val adapter = EditFragOptionsAdapter(requireActivity(),this)
        binding.editFragOptionsList.adapter = adapter

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
        val resolver = requireActivity().contentResolver
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
        Toast.makeText(requireActivity(),"Saved",Toast.LENGTH_SHORT).show()
    }

    override fun onEditOptionsClicked(item: EditFragOptionsModel) {
        setOptionsUi(item.id)
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
    }


}