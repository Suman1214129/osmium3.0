package com.osmiumai.app

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.osmiumai.app.databinding.ActivityUploadPapersBinding
import com.osmiumai.app.databinding.DialogGeneratingTestBinding

class UploadPapersActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityUploadPapersBinding
    private val uploadedFiles = mutableListOf<UploadedFile>()
    private lateinit var adapter: UploadedFilesAdapter
    
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                if (data.clipData != null) {
                    // Multiple files selected
                    for (i in 0 until data.clipData!!.itemCount) {
                        val uri = data.clipData!!.getItemAt(i).uri
                        addFile(uri)
                    }
                } else {
                    // Single file selected
                    data.data?.let { uri -> addFile(uri) }
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPapersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Hide action bar
        supportActionBar?.hide()
        
        setupUI()
        setupRecyclerView()
    }
    
    private fun setupUI() {
        binding.backButton.setOnClickListener {
            finish()
        }
        
        binding.uploadArea.setOnClickListener {
            openFilePicker()
        }
        
        binding.chooseFileText.setOnClickListener {
            openFilePicker()
        }
        
        binding.generateTestButton.setOnClickListener {
            if (uploadedFiles.size >= 5) {
                showGeneratingDialog()
            } else {
                Toast.makeText(this, "Please upload at least 5 files", Toast.LENGTH_SHORT).show()
            }
        }
        
        updateGenerateButton()
    }
    
    private fun setupRecyclerView() {
        adapter = UploadedFilesAdapter(uploadedFiles) { position ->
            uploadedFiles.removeAt(position)
            adapter.notifyItemRemoved(position)
            updateGenerateButton()
        }
        binding.filesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.filesRecyclerView.adapter = adapter
    }
    
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "image/*"))
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        filePickerLauncher.launch(Intent.createChooser(intent, "Select Files"))
    }
    
    private fun addFile(uri: Uri) {
        val fileName = getFileName(uri)
        val fileSize = getFileSize(uri)
        uploadedFiles.add(UploadedFile(fileName, fileSize, uri))
        adapter.notifyItemInserted(uploadedFiles.size - 1)
        updateGenerateButton()
    }
    
    private fun getFileName(uri: Uri): String {
        var fileName = "Unknown"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName
    }
    
    private fun getFileSize(uri: Uri): String {
        var size = 0L
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
            if (cursor.moveToFirst() && sizeIndex != -1) {
                size = cursor.getLong(sizeIndex)
            }
        }
        return "${(size / (1024 * 1024.0)).let { "%.1f".format(it) }} MB"
    }
    
    private fun updateGenerateButton() {
        binding.generateTestButton.isEnabled = uploadedFiles.size >= 5
        binding.generateTestButton.alpha = if (uploadedFiles.size >= 5) 1.0f else 0.5f
    }
    
    private fun showGeneratingDialog() {
        val dialog = Dialog(this)
        val dialogBinding = DialogGeneratingTestBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Start rotation animation on the ring only
        val rotateAnimation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.rotate_loading)
        dialogBinding.loadingRing.startAnimation(rotateAnimation)
        
        // Initially disable save button
        dialogBinding.saveButton.isEnabled = false
        dialogBinding.saveButton.alpha = 0.5f
        
        // Enable save button after 5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            dialogBinding.saveButton.isEnabled = true
            dialogBinding.saveButton.alpha = 1.0f
        }, 5000)
        
        dialogBinding.cancelButton.setOnClickListener {
            dialogBinding.loadingRing.clearAnimation()
            dialog.dismiss()
        }
        
        dialogBinding.saveButton.setOnClickListener {
            if (dialogBinding.saveButton.isEnabled) {
                dialogBinding.loadingRing.clearAnimation()
                dialog.dismiss()
                val intent = Intent(this, MockTestActivity::class.java)
                startActivity(intent)
            }
        }
        
        dialog.show()
    }
}

data class UploadedFile(
    val name: String,
    val size: String,
    val uri: Uri
)
