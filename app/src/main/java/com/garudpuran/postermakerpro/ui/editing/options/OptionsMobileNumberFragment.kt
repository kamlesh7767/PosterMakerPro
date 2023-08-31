package com.garudpuran.postermakerpro.ui.editing.options

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import codes.side.andcolorpicker.converter.toColorInt
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.databinding.FragmentOptionsFramesBinding
import com.garudpuran.postermakerpro.databinding.FragmentOptionsMobileNumberBinding
import com.garudpuran.postermakerpro.models.FontItem
import com.garudpuran.postermakerpro.ui.commonui.HomeResources


class OptionsMobileNumberFragment(private val mListener:OptionsMoNoListener) : Fragment() {
    private var _binding: FragmentOptionsMobileNumberBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOptionsMobileNumberBinding.inflate(inflater, container, false)
initFonts()
        binding.editUserMobileSizeSlider.addOnChangeListener { slider, value, fromUser ->
mListener.moNoSizeSliderClicked(value)
            //changeUserAddressSize(value)
        }

        val addressColorSelectorGroup = PickerGroup<IntegerHSLColor>().also {
            it.registerPickers(
                binding.mobileHueSeekBar,
                binding.mobileLightnessSeekBar
            )
        }

        binding.editFragOptionsMobileHideShowBtn.setOnCheckedChangeListener { p0, p1 ->
           mListener.moNoHideShowBtnClicked(p1)

        }

        addressColorSelectorGroup.addListener(object :
            ColorSeekBar.OnColorPickListener<ColorSeekBar<IntegerHSLColor>, IntegerHSLColor> {
            override fun onColorChanged(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int
            ) {
                mListener.moNoFontColorChanged(color.toColorInt())
                //userName.setTextColor(color.toColorInt())
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


        return binding.root
    }

    /*private fun initFonts() {
        val fontNames = HomeResources.fonts()

        // Create an ArrayAdapter using the font names and a default spinner layout
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, fontNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        binding.mobileFontSpinner.adapter = adapter

        // Set an item selected listener for the spinner
        binding.mobileFontSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Get the selected font name
                    val selectedFontName = fontNames[position]

                    // Load the selected font
                    val typeface = ResourcesCompat.getFont(
                        requireActivity(),
                        getFontResourceId(selectedFontName)
                    )
mListener.moNoFontChanged(typeface)

                    //userName.typeface = typeface
                    //isNameFontAdded = true
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }*/

    private fun initFonts() {
        val fontNames = HomeResources.fonts()
        val fontItems = fontNames.map { FontItem(it, getFontResourceId(it)) }
        val adapter = object : ArrayAdapter<FontItem>(
            requireActivity(),
            0,
            fontItems
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(
                    R.layout.spinner_dropdown_item_layout,
                    parent,
                    false
                )

                val fontItem = getItem(position)
                val textView = view.findViewById<TextView>(R.id.spinnerTextView)
                val typeface = ResourcesCompat.getFont(context, fontItem?.fontResourceId ?: 0)
                textView.typeface = typeface
                textView.text = fontItem?.fontName

                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return getView(position, convertView, parent)
            }
        }
        binding.mobileFontSpinner.adapter = adapter
        binding.mobileFontSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFontName = fontNames[position]
                val typeface = ResourcesCompat.getFont(
                    requireActivity(),
                    getFontResourceId(selectedFontName)
                )

                mListener.moNoFontChanged(typeface)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun getFontResourceId(fontName: String): Int {
        return resources.getIdentifier(fontName, "font", requireActivity().packageName)
    }

    interface OptionsMoNoListener{
        fun moNoHideShowBtnClicked(p1: Boolean)
        fun moNoSizeSliderClicked(value: Float)
        fun moNoFontChanged(typeface: Typeface?)
        fun moNoFontColorChanged(toColorInt: Int)
    }
}