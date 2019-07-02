package com.lts.dirclean.glide

import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideType
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions.decodeTypeOf
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import android.annotation.SuppressLint


@GlideExtension
public final class MyAppExtension {

    private constructor()

    companion object {
       private val DECODE_TYPE_GIF = decodeTypeOf(GifDrawable::class.java).lock()

        @SuppressLint("CheckResult")
        @GlideType(GifDrawable::class)
        public fun asGif(requestBuilder : RequestBuilder<GifDrawable>) {
            requestBuilder
                .transition(DrawableTransitionOptions())
                .apply(DECODE_TYPE_GIF)
        }
    }


}