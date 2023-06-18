package com.example.blinkanime.misc

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.*
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.blinkanime.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import com.squareup.picasso.Target
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.*
import android.widget.ImageSwitcher
import kotlin.math.abs

class MiscFunc() {

    fun loadAndTransformImage(imageUrl: String, imageView: ImageView, type: String = "normal", width:Int = 1200, height:Int = 400) {
        when(type) {
            "normal" -> {
                Picasso.get()
                    .load(imageUrl)
                    .resize(width, height)
                    .centerCrop()
                    .transform(RoundedCornersTransformation(20f))
                    .into(imageView)
            }
            "circle" -> {
                Picasso.get()
                    .load(imageUrl)
                    .resize(width, height)
                    .transform(CircleTransform())
                    .into(imageView)
            }
            "noRound" -> {
                Picasso.get()
                    .load(imageUrl)
                    .resize(width, height)
                    .centerCrop()
                    .into(imageView)
            }
            else -> {
                Picasso.get()
                    .load(imageUrl)
                    .into(imageView)
            }
        }
    }

    private fun CircleTransform(): Transformation {
        return object : Transformation {
            override fun transform(source: Bitmap): Bitmap {
                val size = Math.min(source.width, source.height)

                val x = (source.width - size) / 2
                val y = (source.height - size) / 2

                val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
                if (squaredBitmap != source) {
                    source.recycle()
                }

                val bitmap = Bitmap.createBitmap(size, size, source.config)

                val canvas = Canvas(bitmap)
                val paint = Paint()
                val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                paint.shader = shader
                paint.isAntiAlias = true

                val r = size / 2f
                canvas.drawCircle(r, r, r, paint)

                squaredBitmap.recycle()
                return bitmap
            }

            override fun key(): String {
                return "circle"
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun showImageDialog(context: Context, imageUrl: List<String>, position: Int) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.image_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        //val imageView = dialog.findViewById<ImageView>(R.id.fullImageView)
        val imageSwitcherID = dialog.findViewById<ImageSwitcher>(R.id.imageSwitcher)
        val relativeLayout = dialog.findViewById<LinearLayout>(R.id.relativeLayoutDialog)
        val imageCount = dialog.findViewById<TextView>(R.id.imageCount)
//        val preButton = dialog.findViewById<ImageButton>(R.id.previousImage)
//        val nextButton = dialog.findViewById<ImageButton>(R.id.nextImage)

        ImageSwitcher(context, imageSwitcherID, imageCount, imageUrl, position)

//        if (imageUrl.size == 1) {
//            preButton.visibility = View.GONE
//            nextButton.visibility = View.GONE
//        } else {
//            preButton.visibility = View.VISIBLE
//            nextButton.visibility = View.VISIBLE
//        }

        // dismiss dialog on relative layout click
        relativeLayout.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun ExpPlayerLoadVideo(context: Context, playerView: PlayerView, videoUrl: String) : ExoPlayer {
        val player = ExoPlayer.Builder(context).build()
        playerView.player = player
        // Build the media item.
        val mediaItem = MediaItem.fromUri(videoUrl)
        // Set the media item to be played.
        player.setMediaItem(mediaItem)
        // don't close screen
        playerView.keepScreenOn = true
        player.prepare()

        playerView.findViewById<ImageButton>(androidx.media3.ui.R.id.exo_play)

        return player
    }

    // Function to open video in any video player
    fun openVideoPlayer(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.setDataAndType(Uri.parse(url), "video/*")
        context.startActivity(intent)
    }

    fun openWebPage(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}

class ImageSwitcher(
    context: Context,
    private val imageSwitcher: ImageSwitcher?,
    private val imageCount: TextView?,
    private val images: List<String>,
    position: Int = 0
) : ViewSwitcher.ViewFactory, GestureDetector.SimpleOnGestureListener(), View.OnTouchListener {

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    private val gestureDetector = GestureDetector(context, this)
    private var imageIndex = position
    private val imageSwitcherPicasso = imageSwitcher?.let { ImageSwitcherPicasso(context, it) }

    init {
        imageSwitcher?.setFactory(this)
        imageSwitcher?.setOnTouchListener(this)
        //imageSwitcher?.setImageResource(images[imageIndex])
        imageCount?.text = "${imageIndex + 1}/${images.size}"
        loadImage()
    }

    private fun loadImage() {
        if (imageSwitcherPicasso != null) {
            Picasso.get()
                .load(images[imageIndex])
                .into(imageSwitcherPicasso)
        }
    }

    override fun makeView(): View {
        val imageView = ImageView(imageSwitcher?.context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        // center image in view
        imageView.adjustViewBounds = true
        imageView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        return imageView
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        val diffX = e2.x - e1.x
        if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            updateImageIndex(diffX)
            updateImageResource()
        }
        return true
    }

    private fun updateImageIndex(diffX: Float) {
        imageIndex += if (diffX > 0) -1 else 1
        if (imageIndex < 0) imageIndex = images.size - 1
        else if (imageIndex >= images.size) imageIndex = 0
        imageCount?.text = "${imageIndex + 1}/${images.size}"
    }

    private fun updateImageResource() {
        loadImage()
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }
}

class ImageSwitcherPicasso(
    private val context: Context,
    private val imageSwitcher: ImageSwitcher
) : Target {

    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
        imageSwitcher.setImageDrawable(BitmapDrawable(context.resources, bitmap))
    }

    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
}





class RoundedCornersTransformation(private val radius: Float) : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(source.width, source.height, source.config)
        val canvas = Canvas(output)
        val paint = Paint()
        paint.isAntiAlias = true
        val rect = Rect(0, 0, source.width, source.height)
        val rectF = RectF(rect)
        canvas.drawRoundRect(rectF, radius, radius, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(source, rect, rect, paint)
        source.recycle()
        return output
    }

    override fun key(): String {
        return "rounded_corners(radius=$radius)"
    }
}