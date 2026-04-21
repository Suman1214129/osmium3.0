package com.osmiumai.app.ui.ai_mentor

import android.net.Uri

data class AttachmentItem(
    val uri: Uri,
    val type: AttachmentType,
    val name: String
)

enum class AttachmentType { IMAGE, FILE }
