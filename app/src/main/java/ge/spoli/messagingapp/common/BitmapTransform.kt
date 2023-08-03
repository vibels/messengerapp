package ge.spoli.messagingapp.common

import android.graphics.Bitmap
import com.squareup.picasso.Transformation

// Class copied from https://stackoverflow.com/questions/23740307/load-large-images-with-picasso-and-custom-transform-object/23741267#23741267 , for image compression
class BitmapTransform (private val maxWidth: Int, private val maxHeight: Int) : Transformation {

    override fun transform(source: Bitmap): Bitmap? {
        val targetWidth: Int
        val targetHeight: Int
        val aspectRatio: Double
        if (source.width > source.height) {
            targetWidth = maxWidth
            aspectRatio = source.height.toDouble() / source.width.toDouble()
            targetHeight = (targetWidth * aspectRatio).toInt()
        } else {
            targetHeight = maxHeight
            aspectRatio = source.width.toDouble() / source.height.toDouble()
            targetWidth = (targetHeight * aspectRatio).toInt()
        }
        val result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false)
        if (result != source) {
            source.recycle()
        }
        return result
    }

    override fun key(): String? {
        return maxWidth.toString() + "x" + maxHeight
    }
}