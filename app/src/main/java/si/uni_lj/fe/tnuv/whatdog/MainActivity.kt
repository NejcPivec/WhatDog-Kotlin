package si.uni_lj.fe.tnuv.whatdog

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import si.uni_lj.fe.tnuv.whatdog.util.Keys.LABEL_PATH
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.*
import java.util.*
import android.content.res.AssetManager
import android.support.v4.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import java.nio.charset.Charset
import kotlin.collections.ArrayList

data class Napovedi(val title: String, val year: String, val image: String)

const val EXTRA_MESSAGE = "si.uni_lj.fe.tnuv.MESSAGE"
private const val DEBUG_TAG = "Gestures"


class MainActivity : AppCompatActivity(),GestureDetector.OnGestureListener {

    val dogs = arrayListOf<String>()
    val neki: List<String> = ArrayList()
    private lateinit var mDetector: GestureDetectorCompat


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDetector = GestureDetectorCompat(this,this)

        glavni_gumb.setOnClickListener{
            ImagePicker.with(this)
                .crop(1f, 1f)
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        imageView4.setOnClickListener {
            // Handler code here.
            val intent5 = Intent(this, Predictions::class.java)
            startActivity(intent5)
        }

        try {
            val br = BufferedReader(InputStreamReader(assets.open(LABEL_PATH)))
            while (true) {
                val line = br.readLine() ?: break
                dogs.add(line)
            }
            br.close()
        } catch (e: IOException) {
            throw RuntimeException("Problem reading label file!", e)
        }



        // Ta del kode ti pomaga z autocompletom besed katere vneseš v iskanje za wikipedijo
        //val dogs = resources.getStringArray(R.array.dogs)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dogs)
        search.threshold=0
        search.setAdapter(adapter)
        // animiramo gibanje gumba:
        animate()
        // zagon baze

    }




    //animaicja gumba
    private fun animate() {
        val pulse = findViewById<ImageView>(R.id.glavni_gumb)
        val anim = AnimationUtils.loadAnimation(this, R.anim.animation)
        anim.repeatCount = Animation.INFINITE
        pulse.startAnimation(anim)
    }

    fun AssetManager.fileAsString( filename: String): String {
        return open("$filename").use {
            it.readBytes().toString(Charset.defaultCharset())
        }
    }

    // pošljemo kar je uporabnik vnesel in pošljemo v aktiviti za wikipedijo
    fun sendMessage(view: View) {
        // neki nared, ko se klikne gumb
        val editText = findViewById<AutoCompleteTextView>(R.id.search)
        val message = editText.text.toString()
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            //img_show.setImageURI(fileUri)

            // Pretvorba v bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri)

            // kličemo fukncijo za shranjevanje slike:
            saveImageToExternalStorage(bitmap)

            // nov intent da sliko spravmo v novo okno
            val intent2= Intent(this, DisplayMessageActivity2::class.java)
            intent2.putExtra("image", fileUri.toString())
            startActivity(intent2)


            val file: File? = ImagePicker.getFile(data)


            val filePath: String? = ImagePicker.getFilePath(data)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    // funkcija za shranjevanje slike v določen folder
    private fun saveImageToExternalStorage(finalBitmap: Bitmap) {
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "Image-$n.jpg"
        val file = File(myDir, fname)
        if (file.exists())
            file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(
            this, arrayOf(file.toString()), null
        ) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        var x: Boolean = false
        val diffY = e2!!.y - e1!!.y
        val diffX = e2.x - e1.x
        val SWIPE_THRESHOLD = 100
        val SWIPE_VELOCITY_THRESHOLD = 100
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX < 0) {
                    x = true
                    val intent5 = Intent(this, Predictions::class.java)
                    startActivity(intent5)
                }
            }
        }
        return x
    }

    override fun onLongPress(event: MotionEvent) {

    }

    override fun onScroll(
        event1: MotionEvent,
        event2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {

        return true
    }

    override fun onShowPress(event: MotionEvent) {

    }

    override fun onSingleTapUp(event: MotionEvent): Boolean {

        return true
    }
    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }


}




