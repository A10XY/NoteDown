package com.ahmed.notedown.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.ahmed.notedown.R
import com.ahmed.notedown.databinding.ActivityCreateNoteBinding
import com.ahmed.notedown.databinding.VoiceRecordingLayoutBinding
import com.ahmed.notedown.models.entities.Note
import com.ahmed.notedown.ui.activities.viewmodels.CreateNoteActivityViewModel
import com.ahmed.notedown.ui.activities.viewmodels.factories.CreateNoteActivityViewModelFactory
import com.ahmed.notedown.utils.*
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Exception
import java.lang.Runnable

class CreateNoteActivity : AppCompatActivity() {
    private var _binding: ActivityCreateNoteBinding? = null
    private val  binding get() = _binding!!

    private var mediaRecorder: MediaRecorder? = null
    private var recordedVoicePath: String? = null

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = MutableLiveData(false)

    private var selectedImage: String? = null
    private var isNoteFavourite = false

    private lateinit var createNoteActivityViewModel: CreateNoteActivityViewModel

    private val pickImageFromGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK && it.data != null){
            val imageUri = it.data!!.data!!
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.NoteImageImageView.setImageBitmap(bitmap)
                selectedImage = encodeImage(bitmap)
            } catch (e: FileNotFoundException){
                e.printStackTrace()
            }
        } else {
            shortToast("No Image Selected!")
        }
    }

    private val requestReadAndWritePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        if (it[Manifest.permission.READ_EXTERNAL_STORAGE] == true){
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { intent
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            pickImageFromGallery.launch(intent)
        } else {
            shortToast("Permission Denied!")
        }
    }

    private val requestRecordAudioPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            showVoiceRecordingDialog()
        } else {
            shortToast("Permission Denied!")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val createNoteActivityViewModelFactory = CreateNoteActivityViewModelFactory(this.application)
        createNoteActivityViewModel = ViewModelProvider(this, createNoteActivityViewModelFactory)[CreateNoteActivityViewModel::class.java]
        binding.NoteDateTimeTextView.text = simpleDateTime()
        setListeners()
    }

    private fun setListeners(){
        binding.AddImageImageView.setOnClickListener {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestReadAndWritePermissions.launch(permissions)
        }
        binding.AddVoiceImageView.setOnClickListener {
            requestRecordAudioPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
        binding.FavouriteImageView.setOnClickListener {
            isNoteFavourite = if (isNoteFavourite){
                binding.FavouriteImageView.setImageResource(R.drawable.ic_un_favourite)
                false
            } else {
                binding.FavouriteImageView.setImageResource(R.drawable.ic_favourite)
                true
            }
        }
        binding.BackImageView.setOnClickListener {
            onBackPressed()
        }
        binding.SaveNoteImageView.setOnClickListener {
            saveNote()
        }
        handlePlayAndPauseAudio()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showVoiceRecordingDialog(){
        val dialog = Dialog(this)
        val dialogBinding = VoiceRecordingLayoutBinding.inflate(layoutInflater)
        dialog.addContentView(dialogBinding.root as View, dialogBinding.DialogLayout.layoutParams)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        dialogBinding.MicrophoneBackgroundView.setOnTouchListener { view, event ->
            view.setOnClickListener {
                Log.d(TAG, "One Click")
            }
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(TAG, "Clicked")
                    startVoiceRecording()
                    dialogBinding.TimerChronometer.start()
                    dialogBinding.VoiceWaveLottieView.isVisible = true
                    dialogBinding.VoiceWaveLottieView.playAnimation()
                }
                MotionEvent.ACTION_UP -> {
                    Log.d(TAG, "Released")
                    stopVoiceRecording()
                    dialogBinding.TimerChronometer.stop()
                    dialogBinding.VoiceWaveLottieView.cancelAnimation()
                    dialogBinding.VoiceWaveLottieView.isVisible = false
                    dialog.dismiss()
                }
            }
            false
        }

        dialogBinding.CancelTextView.setOnClickListener {
            dialog.dismiss()
        }
    }

    @Suppress("DEPRECATION")
    private fun startVoiceRecording(){
        mediaRecorder = if (apiLevelIsHigherOrEqualTo(31)){
            MediaRecorder(this)
        } else {
            MediaRecorder()
        }
        val path = this.getExternalFilesDir("/Voice Recordings")?.absolutePath
        recordedVoicePath = "$path/${voiceNoteFileName()}.3gp"
        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(recordedVoicePath)
        }
        try {
            mediaRecorder?.prepare()
        } catch (e: IOException){
            e.printStackTrace()
        } catch (e: IllegalStateException){
            e.printStackTrace()
        }

        mediaRecorder?.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun stopVoiceRecording(){
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
        binding.VoiceNoteLayout.isVisible = true
        recordedVoicePath?.let { binding.VoiceWaveSeekBar.setSampleFrom(it) }
        binding.VoiceWaveSeekBar.setOnTouchListener { view, motionEvent -> true }
    }

    private fun handlePlayAndPauseAudio(){
        isPlaying.observe(this, {
            when(it){
                true -> {
                    binding.PauseAndResumeVoiceImageView.setImageResource(R.drawable.ic_pause)
                    binding.PauseAndResumeVoiceImageView.setOnClickListener {
                        stopNoteAudio()
                    }
                }
                false -> {
                    binding.PauseAndResumeVoiceImageView.setImageResource(R.drawable.ic_resume)
                    binding.PauseAndResumeVoiceImageView.setOnClickListener {
                        playNoteAudio()
                    }
                }
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    @Suppress("DEPRECATION")
    private fun playNoteAudio(){
        mediaPlayer = MediaPlayer()

        val voiceNotePath = "file://" + recordedVoicePath!!
        Log.d(TAG, "Recorded Voice Path: $voiceNotePath")

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        mediaPlayer?.setAudioAttributes(audioAttributes)

        try {
            mediaPlayer?.setDataSource(voiceNotePath)
            mediaPlayer?.prepare()
        } catch (e: IOException){
            e.printStackTrace()
        }

        mediaPlayer?.setOnPreparedListener {
            it.start()
            isPlaying.postValue(true)
            initializeSeekBar()
            Log.d(TAG, "Start Playing..")
        }

        binding.VoiceWaveSeekBar.setOnTouchListener { view, motionEvent ->  view.onTouchEvent(motionEvent)}

        binding.VoiceWaveSeekBar.onProgressChanged = object : SeekBarOnProgressChanged{
            override fun onProgressChanged(
                waveformSeekBar: WaveformSeekBar,
                progress: Float,
                fromUser: Boolean
            ) {
                if (fromUser){
                    mediaPlayer?.seekTo(progress.toInt())
                }
            }
        }
        mediaPlayer?.setOnCompletionListener {
            stopNoteAudio()
            shortToast("Audio Finished!")
        }
    }

    private fun initializeSeekBar(){
        binding.VoiceWaveSeekBar.maxProgress = mediaPlayer!!.duration.toFloat()

        val handler = Handler()
        handler.postDelayed(object : Runnable{
            override fun run() {
                try {
                    binding.VoiceWaveSeekBar.progress = mediaPlayer!!.currentPosition.toFloat()
                    handler.postDelayed(this, 1000)
                } catch (e: Exception){
                    binding.VoiceWaveSeekBar.progress = 0f
                }
            }
        }, 0)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun stopNoteAudio(){
        mediaPlayer?.pause()
        binding.VoiceWaveSeekBar.setOnTouchListener { view, motionEvent -> true}
        Log.d(TAG, "Stop Playing")
        isPlaying.postValue(false)
    }

    private fun saveNote() {
        if (noteInputsAreValid()){
            val note = Note(
                id = 0,
                title = binding.NoteTitleEditText.text.toString(),
                dateTime = binding.NoteDateTimeTextView.text.toString(),
                text = binding.NoteTextEditText.text.toString(),
                imagePath = selectedImage,
                audioPath = recordedVoicePath,
                favourite = isNoteFavourite
            )
            createNoteActivityViewModel.addNote(note)
            shortToast("Note Saved Successfully!")
            finish()
        }
    }

    private fun noteInputsAreValid(): Boolean{
        return if (!binding.NoteTitleEditText.text.isNullOrEmpty() && !binding.NoteTextEditText.text.isNullOrEmpty()){
            true
        } else {
            when{
                binding.NoteTitleEditText.text.isNullOrEmpty() -> {
                    shortToast("Enter Note Title..")
                }
                binding.NoteTextEditText.text.isNullOrEmpty() -> {
                    shortToast("Enter Note Text..")
                }
            }
            false
        }
    }

    private fun encodeImage(bitmap: Bitmap): String{
        val byteArray = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, byteArray)
        return Base64.encodeToString(byteArray.toByteArray(), Base64.DEFAULT)
    }

    override fun attachBaseContext(newBase: Context?) {
        val language = Preferencer(newBase!!).getString("LANGUAGE")
        super.attachBaseContext(ContextUtils.wrap(newBase, language))
    }

    companion object{
        private const val TAG = "LONG_CLICKED"
    }
}