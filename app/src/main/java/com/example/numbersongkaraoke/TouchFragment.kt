// MainActivity.kt
package com.example.numbersongkaraoke

import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.numbersongkaraoke.databinding.FragmentTouchBinding
import java.util.Locale

class TouchFragment : Fragment(), TextToSpeech.OnInitListener {
    companion object {
        private const val ARG_COUNT_TO = "count_to"
        private const val ARG_STEP = "step"
        fun newInstance(countTo: Int, step: Int) = TouchFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_COUNT_TO, countTo)
                putInt(ARG_STEP, step)
            }
        }
    }

    // Parameters
    private var countTo: Int = 5
    private var step: Int = 1

    private var _binding: FragmentTouchBinding? = null
    private val binding get() = _binding!!
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(requireContext(), this)
        arguments?.let {
            countTo = it.getInt(ARG_COUNT_TO, countTo)
            step = it.getInt(ARG_STEP, step)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTouchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TouchFragment", "onViewCreated: countTo=$countTo, step=$step")

        // Build sequence
        val numbers = mutableListOf<Int>()
        var n = step
        while (n <= countTo) {
            numbers += n
            n += step
        }

        // Parent container
        val parent = binding.buttonContainer
        parent.removeAllViews()

        // Chunk into rows of 10
        numbers.chunked(10).forEach { rowList ->
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 8, 0, 8) }
            }
            rowList.forEach { num ->
                val btn = Button(requireContext()).apply {
                    text = num.toString()
                    textSize = 20f
                    backgroundTintList = ContextCompat.getColorStateList(
                        requireContext(), android.R.color.holo_orange_light
                    )
                    setTextColor(Color.WHITE)
                    setOnClickListener {
                        tts.speak(num.toString(), TextToSpeech.QUEUE_FLUSH, null, "NUM_$num")
                    }
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    ).apply { setMargins(4, 4, 4, 4) }
                }
                row.addView(btn)
            }
            parent.addView(row)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            tts.setSpeechRate(1.0f)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }
}
