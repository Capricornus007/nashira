package io.github.capricornus007.nashira

import android.content.Intent
import android.net.Uri

actual fun openLink(url: String) {
    val context = appContext ?: return
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}

/** 由 MainActivity 注入的應用級 Context */
var appContext: android.content.Context? = null
