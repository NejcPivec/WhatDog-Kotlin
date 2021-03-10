package si.uni_lj.fe.tnuv.whatdog

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView




class ListAdapter(private val list: List<Napovedi>)
    : RecyclerView.Adapter<MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dog: Napovedi = list[position]
        holder.bind(dog)
    }

    override fun getItemCount(): Int {
        return list.size}

}

class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false)), View.OnClickListener,
    View.OnLongClickListener,View.OnCreateContextMenuListener {
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        TODO()

    }

    override fun onClick(v: View?) {
        var index1 =0
        var message =""
        message = mTitleView?.text as String
        index1 = message.indexOf(":")
        if (index1 != -1)
        {
            message= message.substring(0 , index1)
        }
        val context = v?.context
        val intent = Intent(context, DisplayMessageActivity::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        context?.startActivity(intent)
    }
    override fun onLongClick(view: View): Boolean {
        System.out.println(adapterPosition)
        return true
    }

    private var mTitleView: TextView? = null
    private var mYearView: TextView? = null
    private var mTitleSlika: ImageView? = null


    init {
        itemView.setOnLongClickListener(this)
        itemView.setOnClickListener(this)
        mTitleView = itemView.findViewById(R.id.list_title)
        mYearView = itemView.findViewById(R.id.list_description)
        mTitleSlika = itemView.findViewById(R.id.slika)
        itemView.setOnClickListener(this)
    }

    fun bind(dog: Napovedi) {
        mTitleView?.text = dog.image
        mYearView?.text = dog.year
        mTitleSlika?.setImageURI(Uri.parse(dog.title))
    }

}