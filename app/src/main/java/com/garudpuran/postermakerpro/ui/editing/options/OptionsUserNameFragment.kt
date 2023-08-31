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
import com.garudpuran.postermakerpro.databinding.FragmentOptionsUserNameBinding
import com.garudpuran.postermakerpro.models.FontItem
import com.garudpuran.postermakerpro.ui.commonui.HomeResources


class OptionsUserNameFragment(private val mListener:OptionsNameListener) : Fragment() {
    private var _binding: FragmentOptionsUserNameBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOptionsUserNameBinding.inflate(inflater, container, false)
        initNameFonts()

        binding.editUserNameSizeSlider.addOnChangeListener { slider, value, fromUser ->
mListener.nameSizeSliderClicked(value)
        }

        val nameColorSelectorGroup = PickerGroup<IntegerHSLColor>().also {
            it.registerPickers(
                binding.nameHueSeekBar,
                binding.nameLightnessSeekBar
            )
        }

        binding.editFragOptionsNameHideShowBtn.setOnCheckedChangeListener { p0, p1 ->
            mListener.nameHideShowBtnClicked(p1)

        }

        nameColorSelectorGroup.addListener(object :
            ColorSeekBar.OnColorPickListener<ColorSeekBar<IntegerHSLColor>, IntegerHSLColor> {
            override fun onColorChanged(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int
            ) {
                mListener.nameFontColorChanged(color.toColorInt())
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

   /* private fun initNameFonts() {
        val fontNames = HomeResources.fonts()
        val fontItems = fontNames.map { FontItem("Poster maker pro", getFontResourceId(it)) }
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

                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return getView(position, convertView, parent)
            }
        }
        binding.fontSpinner.adapter = adapter
        binding.fontSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFontName = fontNames[position]
                val typeface = ResourcesCompat.getFont(
                    requireActivity(),
                    getFontResourceId(selectedFontName)
                )

                mListener.nameFontChanged(typeface)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }*/

    private fun initNameFonts() {
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
        binding.fontSpinner.adapter = adapter
        binding.fontSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFontName = fontNames[position]
                val typeface = ResourcesCompat.getFont(
                    requireActivity(),
                    getFontResourceId(selectedFontName)
                )

                mListener.nameFontChanged(typeface)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun getFontResourceId(fontName: String): Int {
        return resources.getIdentifier(fontName, "font", requireActivity().packageName)
    }

    interface OptionsNameListener{
        fun nameHideShowBtnClicked(p1: Boolean)
        fun nameSizeSliderClicked(value: Float)
        fun nameFontChanged(typeface: Typeface?)
        fun nameFontColorChanged(toColorInt: Int)
    }

}