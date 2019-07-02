package com.lts.dirclean.utils

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.text.TextUtils

class MediaUtil private constructor() {

    companion object {
        val instances: MediaUtil by lazy { MediaUtil() }
    }

    fun getVideoThumbail(path: String, kind: Int): Bitmap? {

        val bitmap = ThumbnailUtils.createVideoThumbnail(path, kind)

        return bitmap
    }

    fun getVideoDuration(path: String): Int {
        val retriever = MediaMetadataRetriever()
        try {

            retriever.setDataSource(path)
            val metadata = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

            return if (TextUtils.isEmpty(metadata)) 0 else metadata.toInt()
        } catch (e: IllegalArgumentException) {

        } finally {
            retriever.release()

        }

        return 0


    }


}