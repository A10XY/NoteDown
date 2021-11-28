package com.ahmed.notedown.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahmed.notedown.R
import com.ahmed.notedown.databinding.ActivitySettingsBinding
import com.ahmed.notedown.databinding.ChangeLanguageBottomSheetBinding
import com.ahmed.notedown.utils.ContextUtils
import com.ahmed.notedown.utils.Preferencer
import com.google.android.material.bottomsheet.BottomSheetDialog

class SettingsActivity : AppCompatActivity() {
    private var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setLanguageText()
        setListeners()
    }

    private fun setListeners(){
        binding.LanguageLayout.setOnClickListener {
            showChangeLanguageBottomSheet()
        }
        binding.BackImageView.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(it)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        Intent(this, MainActivity::class.java).also {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(it)
            finish()
        }
        super.onBackPressed()
    }

    private fun showChangeLanguageBottomSheet(){
        val dialogBinding = ChangeLanguageBottomSheetBinding.inflate(layoutInflater)
        val view = dialogBinding.root as View
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.show()

        when{
            Preferencer(this).getString("LANGUAGE") == "en" -> {
                dialogBinding.RadioGroup.check(R.id.English_RadioButton)
            }
            Preferencer(this).getString("LANGUAGE") == "ar" -> {
                dialogBinding.RadioGroup.check(R.id.Arabic_RadioButton)
            }
        }

        dialogBinding.RadioGroup.setOnCheckedChangeListener { _, i ->
            when(i){
                R.id.English_RadioButton -> {
                    binding.LanguageTextView.text = getString(R.string.ENGLISH)
                    if (Preferencer(this).getString("LANGUAGE") != "en"){
                        Preferencer(this).putString("LANGUAGE", "en")
                        Toast.makeText(this, "English", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        Intent(this, SettingsActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    } else {
                        dialog.dismiss()
                    }
                }
                R.id.Arabic_RadioButton -> {
                    binding.LanguageTextView.text = getString(R.string.ARABIC)
                    if (Preferencer(this).getString("LANGUAGE") != "ar"){
                        Preferencer(this).putString("LANGUAGE", "ar")
                        Toast.makeText(this, "Arabic", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        Intent(this, SettingsActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    } else {
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val language = Preferencer(newBase!!).getString("LANGUAGE")
        super.attachBaseContext(ContextUtils.wrap(newBase, language))
    }

    private fun setLanguageText(){
        when{
            Preferencer(this).getString("LANGUAGE") == "en" -> {
                binding.LanguageTextView.text = getString(R.string.ENGLISH)
            }
            Preferencer(this).getString("LANGUAGE") == "ar" -> {
                binding.LanguageTextView.text = getString(R.string.ARABIC)
            }
        }
    }
}