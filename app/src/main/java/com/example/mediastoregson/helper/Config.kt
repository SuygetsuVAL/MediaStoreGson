package com.example.mediastoregson.helper

import android.os.Environment

class Config {
    companion object {
        const val FILE_NAME = "user.json"
        val RELATIVE_PATH = "${Environment.DIRECTORY_DOCUMENTS}/MyApp/"
        const val MIME_TYPE = "application/json"
        const val VOLUME_NAME = "external"
    }
}

