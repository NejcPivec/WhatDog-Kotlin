package si.uni_lj.fe.tnuv.whatdog

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import android.R.attr.name
import android.service.autofill.UserData



class Database// constructor
    (private val context:Context) {
    private var dbhelper:DBHelper? = null
    private var database:SQLiteDatabase? = null



     private class DBHelper @SuppressLint("NewApi")
     constructor(context:Context):SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
         override fun onCreate(db:SQLiteDatabase) {
 // create table to store msgs
             db.execSQL(
                " CREATE TABLE " + DATABASE_TABLE_IMAGE + " ("
                        + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + KEY_IMAGE_PATH + " TEXT, "
                        + KEY_DATE + " TEXT," + KEY_PRED + " TEXT );"
             )
         }
         override fun onUpgrade(db:SQLiteDatabase, oldVersion:Int, newVersion:Int) {
             db.execSQL("DROP TABLE IF EXISTS $DATABASE_TABLE_IMAGE")
             onCreate(db)
         }

}
 // open db
     fun open():Database {
         dbhelper = DBHelper(context)
         database = dbhelper!!.writableDatabase
         return this
     }
 // close db
     fun close() {
     dbhelper!!.close()
 }
    fun fetch_data(): Cursor? {
        val columns = arrayOf<String>(KEY_ROWID, KEY_IMAGE_PATH,KEY_DATE,KEY_PRED)
        val cursor = database?.query(DATABASE_TABLE_IMAGE, columns, null, null, null, null, null)
        cursor?.moveToFirst()
        return cursor
    }

     fun saveImagePath(imagePath:String, date:String, prediction:String):Long {
         val cv = ContentValues()
         cv.put(KEY_IMAGE_PATH, imagePath)
         cv.put(KEY_DATE, date)
         cv.put(KEY_PRED, prediction)
         dbInsert = database!!.insert(DATABASE_TABLE_IMAGE, null, cv)


         if (dbInsert != (-1).toLong())
         {
             Toast.makeText(context, "New row added , row id: $dbInsert", Toast.LENGTH_SHORT).show()
         }

         else
         {
             Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show()
         }
         return dbInsert
     }
    fun getImagePath():String{
            var imagePath = ""

            val select_query = "SELECT  image_path FROM " + DATABASE_TABLE_IMAGE +
                    " WHERE " + KEY_ROWID + " = " + dbInsert.toString()
            val cursor = database!!.rawQuery(select_query, null)
            val iPicPath = cursor.getColumnIndex(KEY_IMAGE_PATH)

            cursor.moveToNext()
            while (!cursor.isBeforeFirst)
            {
                imagePath = cursor.getString(iPicPath)
                cursor.moveToPrevious()
            }
            return imagePath
        }


     companion object {
 // db version
         private val DATABASE_VERSION = 1
         private val DATABASE_NAME = "image_save"
         private val DATABASE_TABLE_IMAGE = "image"
 //table row
         val KEY_ROWID = "id"
         val KEY_IMAGE_PATH = "image_path"
         val KEY_DATE = "date"
         val KEY_PRED = "prediction"
         internal var dbInsert:Long = 0
     }

}