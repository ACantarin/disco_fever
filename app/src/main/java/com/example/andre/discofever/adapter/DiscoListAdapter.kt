package com.example.andre.discofever.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.andre.discofever.db.Disco
import kotlinx.android.synthetic.main.item_lista_disco.view.*
import com.example.andre.discofever.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_novo_disco.view.*

class DiscoListAdapter internal constructor (context: Context):
        RecyclerView.Adapter<DiscoListAdapter.ViewHolder>(){

    //usado para add o método onclick no item da lista na "ListaDiscoActivity"
    var onItemClick : ((Disco) -> Unit) ? = null
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var discos = emptyList<Disco>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val fotoDisco: CircleImageView = itemView.imgDiscoListPhoto
        val nomeAlbum: TextView = itemView.txtAlbumListName
        val cardView: CardView = itemView.cardDisco
        init {
            itemView.setOnClickListener{
                onItemClick?.invoke(discos[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(holder: ViewGroup, position: Int): ViewHolder{
        val view = inflater.inflate(R.layout.item_lista_disco,holder,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = discos.size



    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val current = discos[position]
        holder.nomeAlbum.text = current.etAlbum
    }

    fun setDiscoList(discoList: List<Disco>){
        this.discos = discoList
        //notifyDataSetChanged()
    }
}