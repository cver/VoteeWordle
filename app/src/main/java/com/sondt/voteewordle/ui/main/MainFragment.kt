package com.sondt.voteewordle.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sondt.voteewordle.R
import com.sondt.voteewordle.databinding.FragmentMainBinding
import com.sondt.voteewordle.domain.entity.GuessResultType
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.random.Random

class MainFragment : Fragment() {
    private val defaultSeed = "12345"
    private val defaultStartWord = "about"

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
            val colorAlpha = if (!it) 1f else 0.5f
            listOf(binding.buttonSync, binding.buttonGuess, binding.buttonRandom).forEach { btn ->
                btn.isEnabled = !it
                btn.alpha = colorAlpha
            }
        }

        viewModel.wordDictLength.observe(viewLifecycleOwner) {
            binding.textviewFirst.text = "$it 5-letter words in Database"
        }

        viewModel.guessResult.observe(viewLifecycleOwner) {
            val boxes = getTextViewBox()
            boxes.forEachIndexed { index, list ->
                list.forEachIndexed { index1, textView ->
                    val value = it.data.getOrNull(index)?.getOrNull(index1)
                    textView.text = value?.guess ?: ""
                    when (value?.result) {
                        GuessResultType.ABSENT -> textView.setBackgroundResource(R.drawable.bg_word_box_absent)
                        GuessResultType.PRESENT -> textView.setBackgroundResource(R.drawable.bg_word_box_present)
                        GuessResultType.CORRECT -> textView.setBackgroundResource(R.drawable.bg_word_box_correct)
                         else -> textView.setBackgroundResource(R.drawable.bg_word_box)
                    }
                }
            }

            when (it) {
                is MainViewModel.GuessResultsEvent.NotMatch -> "There aren't any matching words in the database"
                is MainViewModel.GuessResultsEvent.InvalidStartWord -> "Invalid start-word"
                is MainViewModel.GuessResultsEvent.Failed -> "Request failed"
                else -> null
            }?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonSync.setOnClickListener {
            viewModel.sync()
        }

        binding.editTextStartWord.setText(defaultStartWord)
        binding.editTextSeed.setText(defaultSeed)

        binding.buttonRandom.setOnClickListener {
            viewModel.clear()
            val random = Random.nextInt(1, 99999)
            binding.editTextSeed.setText(random.toString())
        }

        binding.buttonGuess.setOnClickListener {
            val seed = binding.editTextSeed.text?.toString()?.toIntOrNull()
            if (seed == null) {
                Toast.makeText(requireContext(), "Seed is not valid, reset to default", Toast.LENGTH_SHORT).show()
                binding.editTextSeed.setText(defaultSeed)
                return@setOnClickListener
            }

            val startWord = binding.editTextStartWord.text?.toString()
            if (startWord.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Start-word is not valid", Toast.LENGTH_SHORT).show()
                binding.editTextStartWord.setText(this.defaultStartWord)
                return@setOnClickListener
            }

            viewModel.guess(seed, startWord)
        }

        viewModel.syncIfNeed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getTextViewBox() = listOf(
        listOf(
            binding.layoutLine1.textView1,
            binding.layoutLine1.textView2,
            binding.layoutLine1.textView3,
            binding.layoutLine1.textView4,
            binding.layoutLine1.textView5,
        ),
        listOf(
            binding.layoutLine2.textView1,
            binding.layoutLine2.textView2,
            binding.layoutLine2.textView3,
            binding.layoutLine2.textView4,
            binding.layoutLine2.textView5,
        ),
        listOf(
            binding.layoutLine3.textView1,
            binding.layoutLine3.textView2,
            binding.layoutLine3.textView3,
            binding.layoutLine3.textView4,
            binding.layoutLine3.textView5,
        ),
        listOf(
            binding.layoutLine4.textView1,
            binding.layoutLine4.textView2,
            binding.layoutLine4.textView3,
            binding.layoutLine4.textView4,
            binding.layoutLine4.textView5,
        ),
        listOf(
            binding.layoutLine5.textView1,
            binding.layoutLine5.textView2,
            binding.layoutLine5.textView3,
            binding.layoutLine5.textView4,
            binding.layoutLine5.textView5,
        ),
        listOf(
            binding.layoutLine6.textView1,
            binding.layoutLine6.textView2,
            binding.layoutLine6.textView3,
            binding.layoutLine6.textView4,
            binding.layoutLine6.textView5,
        ),
    )
}
