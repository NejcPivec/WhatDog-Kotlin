package si.uni_lj.fe.tnuv.whatdog


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_display_message2.*
import si.uni_lj.fe.tnuv.whatdog.util.ImageClassifier
import si.uni_lj.fe.tnuv.whatdog.util.Keys

import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class DisplayMessageActivity2 : AppCompatActivity() {

    private lateinit var classifier: ImageClassifier
    private var message1 = ""
    private var message2= ""
    private var message3= ""
    private var index1 = 0
    private var index2 = 0
    private var index3 = 0
    private var predict=""
    @SuppressLint("CheckResult", "SetTextI18n")

    var database: Database? = null

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message2)
        classifier = ImageClassifier(getAssets())

        val extras = intent.extras
        val myUri = Uri.parse(extras!!.getString("image"))
        slika.setImageURI(myUri)

        database = Database(this)
        database!!.open()




        // Pretvorba v bitmap
        val bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), myUri)

        val selectedImagePath = File((myUri.getPath())).toString()

        val photoImage = Bitmap.createScaledBitmap(bitmap, Keys.INPUT_SIZE, Keys.INPUT_SIZE, true)


        classifier.recognizeImage(photoImage).subscribeBy(
            onSuccess = {
                txtResult1.text = it.get(0).toString()+'%'
                txtResult2.text = it.get(1).toString()+'%'
                txtResult3.text = it.get(2).toString()+'%'

                predict = it.get(0).toString()+'%'
                message1 = it.get(0).toString()
                message2 = it.get(1).toString()
                message3 = it.get(2).toString()
                index1 = message1.indexOf(":")
                index2 = message2.indexOf(":")
                index3 = message3.indexOf(":")
                if (index1 != -1)
                {
                    message1= message1.substring(0 , index1)
                }
                if (index2 != -1)
                {
                    message2= message2.substring(0 , index2)
                }
                if (index3 != -1)
                {
                    message3= message3.substring(0 , index3)
                }
            }
        )

        izid_1.setOnClickListener{
            val intent1 = Intent(this, DisplayMessageActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, message1)
            }
            startActivity(intent1)
        }
        txtResult2.setOnClickListener{
            val intent2 = Intent(this, DisplayMessageActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, message2)
            }
            startActivity(intent2)
        }
        txtResult3.setOnClickListener{
            val intent3 = Intent(this, DisplayMessageActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, message3)
            }
            startActivity(intent3)
        }
        val dateTime = getCurrentTime()

        database!!.saveImagePath(imagePath = selectedImagePath,date =  dateTime, prediction =  predict)

    }

    fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }


}
