package com.garudpuran.postermakerpro.ui.commonui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import com.garudpuran.postermakerpro.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment() {
    private lateinit var _binding:FragmentWebViewBinding
    private val binding get() = _binding
    private var initialUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWebViewBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progress.visibility = View.VISIBLE
        binding.fullWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // Load clicked URL in the same WebView
                loadUrl(url)
                return true // Indicates the URL click is handled
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progress.visibility = View.GONE
            }
        }


        binding.fullWebView.settings.useWideViewPort = false
        binding.fullWebView.settings.setSupportZoom(false)
        binding.fullWebView.settings.builtInZoomControls = false
        binding.fullWebView.settings.displayZoomControls = false
        binding.fullWebView.settings.layoutAlgorithm =
            WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        val url = "https://www.freeprivacypolicy.com/live/5016bee9-5098-4564-8b7f-f150c85b87cd"
        initialUrl = url
        loadUrl(url)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.fullWebView.canGoBack()) {
                    binding.fullWebView.goBack()
                } else if (binding.fullWebView.url == initialUrl) {
                    // Remove the fragment if it's displaying the initial URL
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    // Fragment will handle back press, allow activity to handle it as well
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        })



    }

    fun loadUrl(url: String) {
        binding.fullWebView.loadUrl(url)

        binding.fullWebView
    }


}