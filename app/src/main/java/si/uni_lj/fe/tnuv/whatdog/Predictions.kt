package si.uni_lj.fe.tnuv.whatdog

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.*


class Predictions : AppCompatActivity() {
    private var database: Database? = null
    private var rezultati: MutableList<Napovedi> = mutableListOf<Napovedi>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_item)


        database = Database(this)
        database!!.open()
        var neki = database!!.fetch_data()
        if (neki!!.moveToLast()) {
            do {
                rezultati.add(
                    Napovedi(
                        neki.getString(1),
                        neki.getString(2),
                        neki.getString(3))
                )
            } while (neki.moveToPrevious())
            database!!.close()}

        recyclerView.adapter = ListAdapter(rezultati)
        recyclerView.layoutManager = LinearLayoutManager(this)


    }

}