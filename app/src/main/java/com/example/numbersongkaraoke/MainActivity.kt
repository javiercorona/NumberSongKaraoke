package com.example.numbersongkaraoke

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.numbersongkaraoke.databinding.ActivityMainBinding
import com.example.numbersongkaraoke.VoiceGridFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Voice Mode button opens SongListFragment
        binding.btnVoiceMode.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SongListFragment())
                .addToBackStack(null)
                .commit()
        }

        class MainActivity : AppCompatActivity() {
            private lateinit var binding: ActivityMainBinding

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                binding = ActivityMainBinding.inflate(layoutInflater)
                setContentView(binding.root)

                // Launch the grid of voice‐levels instead of auto‐counting
                binding.btnVoiceMode.setOnClickListener {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, VoiceGridFragment())
                        .addToBackStack(null)
                        .commit()
                }

                // Touch mode stays as is
                binding.btnTouchMode.setOnClickListener {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, TouchFragment.newInstance(countTo = 50, step = 1))
                        .addToBackStack(null)
                        .commit()
                }
            }
        }


        // Touch Mode button opens TouchFragment starting at 50
        binding.btnTouchMode.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    TouchFragment.newInstance(countTo = 50, step = 1)
                )
                .addToBackStack(null)
                .commit()
        }
    }
}