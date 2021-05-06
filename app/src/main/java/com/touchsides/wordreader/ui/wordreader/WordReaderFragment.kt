package com.touchsides.wordreader.ui.wordreader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.touchsides.wordreader.databinding.WordReaderFragmentBinding

import com.touchsides.wordreader.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import android.content.res.AssetManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WordReaderFragment : Fragment(), FileClickListener {
    private var binding: WordReaderFragmentBinding by autoCleared()
    private val viewModel: WordsReaderViewModel by viewModels()
    private lateinit var adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = CustomAdapter(requireContext(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WordReaderFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.visibility = View.GONE
        binding.statsCl.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        setupObservers()
    }

    private fun getTxtFiles(){
        val assetManager: AssetManager? = context?.assets
        adapter.submitList(assetManager?.list("")?.filter { it.endsWith("txt") })
        binding.progressBar.visibility = View.GONE
    }

    private fun setupObservers() {
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (itemCount == 1) binding.recyclerView.scrollToPosition(0)
                binding.recyclerView.visibility = View.VISIBLE
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            getTxtFiles()
        }

        viewModel.doneReading.observe(viewLifecycleOwner, Observer {
            if(it) {
                viewModel.computeMostFrequentWord()
                viewModel.computeMostFrequent7CharacterWord()
                viewModel.computeHighestScoringWordInScrabble()
            }
        })

        viewModel.doneComputing.observe(viewLifecycleOwner, Observer {
            if(it) {
                bindStats()
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.statsCl.visibility = View.VISIBLE
            }
        })
    }

    private fun bindStats() {
        binding.mostFrequentWord.text = String.format(
            "Most frequent word: %S occurred %d times",
            viewModel.fileStats.mostFrequentWord,
            viewModel.fileStats.mostFrequentWordCount
        )
        binding.mostFrequent7CharacterWord.text = String.format(
            "Most frequent 7-character word: %S occurred %d times",
            viewModel.fileStats.mostFrequent7CharacterWord,
            viewModel.fileStats.mostFrequent7CharacterWordCount
        )
        binding.highestScoringWordInScrabble.text = String.format(
            "Highest scoring word(s) (according to Scrabble): %S occurred %d times",
            viewModel.fileStats.highestScoringWordInScrabble,
            viewModel.fileStats.highestScoringWordInScrabbleCount
        )
    }

    override fun invoke(fileName: String) {
        binding.recyclerView.visibility = View.GONE
        binding.statsCl.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        context?.let { viewModel.readFromFile(it, fileName) }
    }
}
