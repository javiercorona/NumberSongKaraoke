package com.example.numbersongkaraoke

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.numbersongkaraoke.databinding.FragmentVoiceBinding
import java.util.Locale

class VoiceGridFragment : Fragment(), TextToSpeech.OnInitListener {
    private var _binding: FragmentVoiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var tts: TextToSpeech

    // first 10 levels
    private val levels = listOf(
        Song("Level 1", 5, 1, level = 1),
        Song("Level 2", 10, 1, level = 2),
        Song("Level 3", 15, 1, level = 3),
        Song("Level 4", 20, 1, level = 4),
        Song("Level 5", 10, 2, level = 5),
        Song("Level 6", 20, 2, level = 6),
        Song("Level 7", 50, 2, level = 7),
        Song("Level 8", 100, 2, level = 8),
        Song("Level 9", 25, 5, level = 9),
        Song("Level 10", 50, 5, level = 10)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(requireContext(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val grid: GridLayout = binding.gridLevels

        levels.forEach { song ->
            val btn = Button(requireContext()).apply {
                text = song.title
                textSize = 18f
                setPadding(16,16,16,16)
                backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(), android.R.color.holo_blue_dark)
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                setOnClickListener {
                    // speak out the counting sequence
                    var n = song.step
                    while (n <= song.countTo) {
                        tts.speak(n.toString(), TextToSpeech.QUEUE_ADD, null, "NUM_$n")
                        n += song.step
                    }
                }
            }
            // each button spans 1 column with equal weight
            val spec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            val params = GridLayout.LayoutParams(spec, spec).apply {
                width = 0
                setMargins(8, 8, 8, 8)
            }
            grid.addView(btn, params)
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
