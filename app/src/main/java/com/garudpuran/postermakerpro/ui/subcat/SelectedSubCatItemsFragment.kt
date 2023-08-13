package com.garudpuran.postermakerpro.ui.subcat



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
        val adapter = AllSubCategoryPostsAdapter(this)
        adapter.setData(data)
        binding.allPostsRc .adapter = adapter
    }

    override fun onAllSubCategoryPostsAdapterListItemClicked(item: PostItem) {
        val intent = Intent(requireActivity(), EditPostActivity::class.java)
        intent.putExtra("imageUrl",item.image_url)
        intent.putExtra("engTitle",item.title_eng)
        intent.putExtra("marTitle",item.title_mar)
        intent.putExtra("hinTitle",item.title_hin)
        startActivity(intent)
    }


}