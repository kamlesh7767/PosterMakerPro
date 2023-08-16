package com.garudpuran.postermakerpro.ui.subcat



import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.garudpuran.postermakerpro.databinding.FragmentSelectedSubCatItemsBinding
import com.garudpuran.postermakerpro.models.PostItem
import com.garudpuran.postermakerpro.ui.editing.EditPostActivity
import com.garudpuran.postermakerpro.ui.home.HomeViewModel
import com.garudpuran.postermakerpro.ui.profile.SelectProfessionalProfileBottomSheetFrag
import com.garudpuran.postermakerpro.utils.AppPrefConstants
import com.garudpuran.postermakerpro.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectedSubCatItemsFragment : Fragment() ,AllSubCategoryPostsAdapter.AllSubCategoryPostsAdapterListener{
    private lateinit var _binding: FragmentSelectedSubCatItemsBinding
    private val binding get() = _binding
    private val tag = "AllSubCategoriesPostsFragment"
    private val viewModel: HomeViewModel by viewModels()
    private  val args: SelectedSubCatItemsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectedSubCatItemsBinding.inflate(inflater, container, false)
        observeGetAllPosts()
        getAllPosts()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun getAllPosts() {
        viewModel.getAllPosts(args.catId,args.subCatId)
    }

    private fun observeGetAllPosts() {
        viewModel.onObserveGetAllPostsResponseData().observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {
                    binding.progress.root.visibility = View.VISIBLE
                }

                Status.ERROR -> {
                    binding.progress.root.visibility = View.GONE
                }

                Status.SUCCESS -> {
                    if (it.data!!.isNotEmpty()) {
                        binding.progress.root.visibility = View.GONE
                        initRcView(it.data)
                        Log.d(tag,"Got the Posts.")
                    }
                }

                Status.SESSION_EXPIRE -> {

                }
            }
        }
    }

    private fun initRcView(data: List<PostItem>) {
        val adapter = AllSubCategoryPostsAdapter(this,getSelectedLanguage())
        adapter.setData(data)
        binding.allPostsRc .adapter = adapter
    }

    private fun getSelectedLanguage(): String {
        val authPref = requireContext().getSharedPreferences(AppPrefConstants.LANGUAGE_PREF, Context.MODE_PRIVATE)
        return authPref.getString("language", "")!!
    }

    override fun onAllSubCategoryPostsAdapterListItemClicked(item: PostItem) {
        val frag = SelectProfessionalProfileBottomSheetFrag(item.image_url,item.title_eng,item.title_mar,item.title_hin)
        frag.show(childFragmentManager,"SelectProfessionalProfileBottomSheetFrag")
    }


}