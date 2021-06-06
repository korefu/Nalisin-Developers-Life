package com.korefu.developerslife

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.korefu.developerslife.databinding.FragmentEntryBinding
import kotlinx.coroutines.launch

class EntryFragment : Fragment() {
    private val viewModel: EntryViewModel by viewModels()
    private var index = 0
    private lateinit var progressBar: ProgressBar
    private lateinit var imageView: ImageView
    private lateinit var networkErrorLinearLayout: LinearLayout
    private lateinit var textViewDescription: TextView
    private lateinit var buttonNext: FloatingActionButton
    private lateinit var networkTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEntryBinding.inflate(inflater, container, false)
        if (arguments != null) {
            val index = requireArguments().getInt("index")
            this.index = index
        }
        viewModel.viewModelScope.launch {
            viewModel.loadEntries(index)
            loadImage()
        }
        networkErrorLinearLayout = binding.networkErrorLinearLayout
        progressBar = binding.progressBar
        imageView = binding.photoView
        textViewDescription = binding.textViewDescription
        buttonNext = binding.buttonNext
        networkTextView = binding.networkTextView
        binding.buttonBack.setOnClickListener {
            viewModel.pos.value = viewModel.pos.value?.minus(1)
            loadImage()
        }
        buttonNext.setOnClickListener {
            viewModel.pos.value = viewModel.pos.value?.plus(1)
            loadImage()
        }
        binding.buttonRetry.setOnClickListener {
            if (viewModel.pos.value!! >= viewModel.entryList.size)
                viewModel.viewModelScope.launch {
                    viewModel.loadEntries(index)
                    buttonNext.isEnabled = true
                    loadImage()
                } else
                loadImage()

        }

        viewModel.pos.observe(viewLifecycleOwner, { pos ->
            when (pos) {
                0 -> binding.buttonBack.isEnabled = false
                1 -> binding.buttonBack.isEnabled = true
                (viewModel.entryList.size - 2) -> viewModel.viewModelScope.launch {
                    viewModel.loadEntries(index)
                    buttonNext.isEnabled = true
                }
                (viewModel.entryList.size - 1) -> buttonNext.isEnabled = false
            }
        })
        viewModel.entriesNetworkState.observe(viewLifecycleOwner, { text ->
            when (text) {
                "error" -> {
                    if (viewModel.entryList.size == 0) {
                        progressBar.visibility = View.GONE
                        networkErrorLinearLayout.visibility = View.VISIBLE
                        imageView.visibility = View.GONE

                    } else Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_network_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        viewModel.imageNetworkState.observe(viewLifecycleOwner, { text ->
            when (text) {
                "success" -> {
                    progressBar.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    networkErrorLinearLayout.visibility = View.GONE
                }
                "loading" -> {
                    progressBar.visibility = View.VISIBLE
                    imageView.visibility = View.GONE
                    networkErrorLinearLayout.visibility = View.GONE
                }
                "failure" -> {
                    progressBar.visibility = View.GONE
                    networkErrorLinearLayout.visibility = View.VISIBLE
                    networkTextView.text = getString(R.string.networkErrorMessage)
                }
            }
        })

        return binding.root
    }

    private fun loadImage() {
        val pos = viewModel.pos.value
        if (pos == null || pos >= viewModel.entryList.size) {
            progressBar.visibility = View.GONE
            networkErrorLinearLayout.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            buttonNext.isEnabled = false
            if (viewModel.entriesNetworkState.value == "success") {
                networkTextView.text = getString(R.string.noMoreGifs)
            }
            return
        }
        viewModel.imageNetworkState.value = "loading"
        Glide.with(this)
            .asGif()
            .load(viewModel.entryList[pos].gifURL)
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    viewModel.imageNetworkState.value = "failure"
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    viewModel.imageNetworkState.value = "success"
                    return false
                }

            }).into(imageView)
        textViewDescription.text = viewModel.entryList[viewModel.pos.value!!].description
    }
}