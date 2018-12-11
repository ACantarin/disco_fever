package com.example.andre.discofever.view

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.ClipDescription
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.andre.discofever.R
import com.example.andre.discofever.db.Disco
import kotlinx.android.synthetic.main.activity_novo_disco.*
import java.lang.Exception

class NovoDiscoActivity : AppCompatActivity() {

    lateinit var disco: Disco
    private var image_uri : Uri? = null
    private var mCurrentPhotoPath: String = ""
    var menu: Menu? = null

    private var notificationManager: NotificationManager? = null

    companion object {
        private val REQUEST_IMAGE_GALLERY = 1000
        private val REQUEST_IMAGE_CAPTURE = 2000
        const val EXTRA_REPLY = "view.REPLY"
        const val EXTRA_DELETE = "view.DELETE"
    }

    override fun onCreate (savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_disco)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(
            "com.example.andre.discofever.news",
            "NotifyDemo News",
            "Example News Channel"
        )

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imgAddImgAlbum?.setOnClickListener{
            imgAddImgAlbum.setOnCreateContextMenuListener { menu, v, menuInfo ->
                menu.add(Menu.NONE, 1, Menu.NONE, "Tirar foto")
                menu.add(Menu.NONE, 2, Menu.NONE, "Escolher da galeria")
            }
        }



        val intent = intent
        try {
            disco = intent.getSerializableExtra(EXTRA_REPLY) as Disco
            disco.let {
                etAlbum.setText(disco.etAlbum)
                etArtista.setText(disco.etArtista)
            }
            val menuItem = menu?.findItem(R.id.menuExcluir)
            menuItem?.isVisible = true
        }catch (e: Exception){
            val menuItem = menu?.findItem(R.id.menuExcluir)
            menuItem?.isVisible = false
        }
    }

    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    private fun takePicture(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "nova imagem")
        values.put(MediaStore.Images.Media.DESCRIPTION, "imagem da camera")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")

        image_uri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if(intent.resolveActivity(packageManager) != null){
            mCurrentPhotoPath = image_uri.toString()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun getPermissionImageFromGallery(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
                // permission denied
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permission, REQUEST_IMAGE_GALLERY)
            } else {
                // permission granted
                pickImageFromGallery()
            }
        }
        else{
            // system < M
            pickImageFromGallery()
        }
    }

    private fun getPermissionTakePicture(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
                // permission denied
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, REQUEST_IMAGE_CAPTURE)
            } else {
                // permission granted
                takePicture()
            }
        }
        else{
            // system < M
            //takePicture()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_IMAGE_GALLERY -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) pickImageFromGallery()
                else Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
            }
            REQUEST_IMAGE_CAPTURE ->{
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) takePicture()
                else Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_GALLERY) imgNewDisco.setImageURI(data?.data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) imgNewDisco.setImageURI(image_uri)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            1 -> getPermissionImageFromGallery()
            2 -> getPermissionTakePicture()
        }
        return super.onContextItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.item_menu, menu)
        try {
            disco.let{
                val menuItem = menu?.findItem(R.id.menuExcluir)
                menuItem?.isVisible = true
            }
        }catch (e: Exception){

        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?):Boolean{
        return if (item?.itemId == android.R.id.home){
            finish()
            true
        }else if(item?.itemId == R.id.menuSalvar){
            val replyIntent = Intent()

            if(etAlbum.text.isNullOrEmpty()) {
                Toast.makeText(this, "Insira ao menos o álbum", Toast.LENGTH_SHORT).show()
                etAlbum.requestFocus()
            }else if((::disco.isInitialized) && (disco.id > 0)){
                disco.etAlbum = etAlbum.text.toString()
                disco.etArtista = etArtista.text.toString()
            }else{
                disco = Disco(
                    etAlbum = etAlbum.text.toString(),
                    etArtista = etArtista.text.toString()
                )
            }

            replyIntent.putExtra(EXTRA_REPLY, disco)
            setResult(Activity.RESULT_OK, replyIntent)

            finish()
            true
        }else{
            super.onOptionsItemSelected(item)
        }
    }

    private fun createNotificationChannel(id: String, name: String, description: String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(id, name, importance)

            channel.description = description
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern =
                    longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}