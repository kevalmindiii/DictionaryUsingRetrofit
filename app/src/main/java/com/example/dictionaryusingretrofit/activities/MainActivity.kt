package com.example.dictionaryusingretrofit.activities

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.example.dictionaryusingretrofit.databinding.ActivityMainBinding
import com.example.dictionaryusingretrofit.modals.DictionaryModelItem
import com.example.dictionaryusingretrofit.services.DictionaryService
import com.example.dictionaryusingretrofit.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

private const val REQUEST_CODE_SPEECH_INPUT = 1

class MainActivity : AppCompatActivity() {
    var audioUrl = ""
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnGoogleMic.setOnClickListener {
            mic()
        }
        binding.btnSpeaker.setOnClickListener {
            playAudio()
        }
        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            val handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchData = binding.etSearch.text.toString()
                if (searchData.isNotEmpty()) {
                    loadData(searchData)
                } else {
                    Toast.makeText(this, "Please enter any word", Toast.LENGTH_SHORT).show()
                }
            }
            handled
        }
    }

    private fun playAudio() {
        // if(mediaPlayer.isPlaying) return
        val audioUrll = "https:$audioUrl"
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer.setDataSource(audioUrll)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun mic() {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        }.also {
            try {
                startActivityForResult(it, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                Toast
                    .makeText(
                        this@MainActivity, e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                val str = Objects.requireNonNull(result)?.get(0)
                if (str != null) loadData(str)
                binding.etSearch.setText(
                    Objects.requireNonNull(result)?.get(0)
                )
                binding.etSearch.setSelection(binding.etSearch.text.toString().length)
            }
        }
    }

    private fun loadData(name: String) {


        val dictionaryService: DictionaryService =
            ServiceBuilder.buildService(DictionaryService::class.java)

        val requestCall: Call<List<DictionaryModelItem>> = dictionaryService.getName(name)
        requestCall.enqueue(object : Callback<List<DictionaryModelItem>> {
            override fun onResponse(
                call: Call<List<DictionaryModelItem>>,
                response: Response<List<DictionaryModelItem>>
            ) {
                if (response.isSuccessful) {
                    val dictionaryData: List<DictionaryModelItem> = response.body()!!
                    binding.tvWord.text = dictionaryData[0].word
                    binding.tvPhonics.text = dictionaryData[0].phonetic
                    binding.tvDefinition.text =
                        dictionaryData[0].meanings[0].definitions[0].definition
                    binding.linearforVisible.visibility = View.VISIBLE
                    audioUrl = dictionaryData[0].phonetics[0].audio


                }
            }

            override fun onFailure(call: Call<List<DictionaryModelItem>>, t: Throwable) {

            }

        })

    }
}