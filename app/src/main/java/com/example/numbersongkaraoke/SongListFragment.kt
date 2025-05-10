package com.example.numbersongkaraoke

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.numbersongkaraoke.Prefs.unlockedLevel
import com.example.numbersongkaraoke.databinding.FragmentSongListBinding
import kotlinx.coroutines.*
import java.util.Locale

class SongListFragment : Fragment(), TextToSpeech.OnInitListener {
    private var _binding: FragmentSongListBinding? = null
    private val binding get() = _binding!!

    // TTS & coroutine scope
    private lateinit var tts: TextToSpeech
    private lateinit var countImage: ImageView
    private lateinit var dropTarget: TextView
    private lateinit var tilesContainer: LinearLayout



    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Levels
    private val songs = listOf(
        Song("Level 1 - Count to 5", 5, 1, 1),
        Song("Level 2 - Count to 10", 10, 1, 2),
        Song("Level 3 - Count to 15", 15, 1, 3),
        Song("Level 4 - Count to 20", 20, 1, 4),
        Song("Level 5 - Count by 2s to 10", 10, 2, 5),
        Song("Level 6 - Count by 2s to 20", 20, 2, 6),
        Song("Level 7 - Count by 2s to 50", 50, 2, 7),
        Song("Level 8 - Count by 2s to 100", 100, 2, 8),
        Song("Level 9 - Count by 5s to 25", 25, 5, 9),
        Song("Level 10 - Count by 5s to 50", 50, 5, 10)
        // add more as needed
    )

    // Playback sequence state
    private var sequence: List<Int> = emptyList()
    private var currentIndex = 0
    private var currentSong: Song? = null

    // Launcher for speech recognition UI
    private val speechLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        val data = result.data
        if (result.resultCode == android.app.Activity.RESULT_OK && data != null) {
            val spoken = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            checkChildRepeat(spoken?.firstOrNull())
        } else {
            showFallback()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(requireContext(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshList()
        setupFallbackUI()
    }

    private fun refreshList() {
        val unlocked = requireContext().unlockedLevel
        val list = songs.map { it to (it.level <= unlocked) }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = SongAdapter(list) { song, unlockedFlag ->
            if (unlockedFlag) checkAudioPermissionAndStart(song)
            else Toast.makeText(requireContext(), "Complete level ${song.level - 1} first!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAudioPermissionAndStart(song: Song) {
        currentSong = song
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED) startRound(song)
        else requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 123)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, results: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (requestCode == 123 && results.firstOrNull() == PackageManager.PERMISSION_GRANTED)
            currentSong?.let { startRound(it) }
        else Toast.makeText(requireContext(), "Permission needed", Toast.LENGTH_SHORT).show()
    }

    private fun startRound(song: Song) {
        // build sequence
        sequence = mutableListOf<Int>().apply {
            var value = song.startFrom ?: song.step
            val pattern = song.pattern ?: listOf(song.step)
            var idx = 0
            while (if (song.step > 0) value <= song.countTo else value >= 0) {
                add(value)
                value += pattern[idx++ % pattern.size]
            }
        }
        currentIndex = 0
        playAndPrompt()
    }

    private fun playAndPrompt() {
        hideFallback()
        val num = sequence.getOrNull(currentIndex) ?: return
        tts.speak(num.toString(), TextToSpeech.QUEUE_FLUSH, null, "NUM")
        scope.launch {
            delay(800L)
            tts.speak("Your turn!", TextToSpeech.QUEUE_ADD, null, "PROMPT")
            delay(800L)
            speechLauncher.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
            })
        }
    }

    private fun checkChildRepeat(spoken: String?) {
        hideFallback()
        val expected = sequence.getOrNull(currentIndex) ?: return
        val said = parseNumber(spoken)
        if (said == expected) {
            currentIndex++
            if (currentIndex < sequence.size) playAndPrompt() else onLevelComplete(currentSong!!)
        } else showFallback()
    }

    private fun parseNumber(str: String?): Int? {
        val s = str?.trim()?.lowercase() ?: return null
        return s.toIntOrNull() ?: when (s) {
            "one"->1;"two"->2;"three"->3;"four"->4;"five"->5
            else->null
        }
    }

    private fun onLevelComplete(song: Song) {
        val unlocked = requireContext().unlockedLevel
        if (song.level == unlocked) requireContext().unlockedLevel = unlocked + 1
        Toast.makeText(requireContext(), "🎉 Level ${song.level} complete!", Toast.LENGTH_LONG).show()
        refreshList()
    }

    // Fallback UI: allow typing or pressing a button
    private fun setupFallbackUI() {
        binding.fallbackLayout.visibility = View.GONE
        binding.fallbackButton.setOnClickListener {
            playAndPrompt()
        }
    }

    private fun showFallback() {
        binding.fallbackLayout.visibility = View.VISIBLE
    }
    private fun hideFallback() {
        binding.fallbackLayout.visibility = View.GONE
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
        scope.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }
}
