package com.example.andre.discofever.view

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.example.andre.discofever.R
import com.example.andre.discofever.R.*
import com.example.andre.discofever.db.Disco
import com.example.andre.discofever.adapter.DiscoListAdapter
import com.example.andre.discofever.viewmodel.DiscoViewModel
import kotlinx.android.synthetic.main.activity_lista_disco.*
import java.lang.Exception

class ListaDiscoActivity : AppCompatActivity() {

    private lateinit var discoViewModel: DiscoViewModel
    private val requestCodeDisco = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_lista_disco)

        fbAddDisco.setOnClickListener {
            startActivity(Intent(this, NovoDiscoActivity::class.java))
        }

        val recyclerView = rvListaDisco
        val adapter = DiscoListAdapter(this)

        adapter.onItemClick = {
            val intent = Intent(this@ListaDiscoActivity,
                NovoDiscoActivity::class.java)
            intent.putExtra(NovoDiscoActivity.EXTRA_REPLY, it)
            startActivityForResult(intent, requestCodeDisco)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?

        discoViewModel = ViewModelProviders.of(this).get(DiscoViewModel::class.java)

        discoViewModel.allDiscos.observe(this, Observer { discos ->
            discos?.let{adapter.setDiscoList(it)}
        })

        fbAddDisco.setOnClickListener{
            val intent = Intent(this@ListaDiscoActivity, NovoDiscoActivity::class.java)
            startActivityForResult(intent, requestCodeDisco)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == requestCodeDisco && requestCode == Activity.RESULT_OK){
            data?.let { data ->
                try {
                    val disco: Disco?
                    disco = data.getSerializableExtra(NovoDiscoActivity.EXTRA_REPLY) as Disco
                    disco.let {
                        if(disco.id > 0) discoViewModel.update(disco)
                        else discoViewModel.insert(disco)
                    }
                }catch (e: Exception){
                    val disco: Disco?  = data.getSerializableExtra(NovoDiscoActivity.EXTRA_DELETE) as Disco
                    disco.let {
                        discoViewModel.delete(disco!!)
                    }
                }
            }
        }else{
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
