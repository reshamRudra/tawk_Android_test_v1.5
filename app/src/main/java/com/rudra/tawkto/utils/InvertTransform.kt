package com.rudra.tawkto.utils

import android.graphics.*
import com.squareup.picasso.Transformation

class InvertTransform(val index: Int) : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        if (index % 4 == 4-1) {
            val size = Math.min(source.width, source.height)
            val x = (source.width - size) / 2
            val y = (source.height - size) / 2
            val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
            if (squaredBitmap != source) {
                source.recycle()
            }
            // color info
            var A: Int
            var R: Int
            var G: Int
            var B: Int
            var pixelColor: Int

            // scan through every pixel
            for (y in 0 until size) {
                for (x in 0 until size) {
                    // get one pixel
                    pixelColor = squaredBitmap.getPixel(x, y)
                    // saving alpha channel
                    A = Color.alpha(pixelColor)
                    // inverting byte for each R/G/B channel
                    R = 255 - Color.red(pixelColor)
                    G = 255 - Color.green(pixelColor)
                    B = 255 - Color.blue(pixelColor)
                    // set newly-inverted pixel to output image
                    squaredBitmap.setPixel(x, y, Color.argb(A, R, G, B))
                }
            }
            return squaredBitmap
        } else {
            return source
        }
    }

    override fun key(): String {
        return "invert"
    }
}