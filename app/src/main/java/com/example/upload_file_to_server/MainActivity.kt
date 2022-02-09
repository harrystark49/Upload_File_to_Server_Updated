package com.example.upload_file_to_server

import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import java.io.File
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import kotlin.math.PI


class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView

    private val mMediaUri:Uri?=null
    private val fileUri:Uri?=null
    private val mediaPath:Uri?=null
    lateinit var retrofit:api
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var baseurl="https://jsonplaceholder.typicode.com/"

        retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseurl)
            .client(OkHttpClient())
            .build()
            .create(api::class.java)

        imageView=findViewById(R.id.preview)
        pickImage.setOnClickListener {
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    permission_code)
                }else{
                var intent=Intent(Intent.ACTION_PICK)
                intent.type="image/*"
                startActivityForResult(intent, IMAGE_REQUEST_CODE)
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== IMAGE_REQUEST_CODE){
            var uri=data?.data
            var path=getRealPathFromUri(uri)
            Glide.with(this)
                .load(data?.data)
                .circleCrop()
                .into(imageView)

            uploadimg(path)
        }
    }
    companion object{
        val IMAGE_REQUEST_CODE=100
        val permission_code=1001
    }

    fun getRealPathFromUri(contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
            cursor =
                contentUri?.let {
                    getContentResolver()?.query(it, proj, null, null, null)
                }
            val column_index: Int = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)!!
            cursor?.moveToFirst()
            cursor?.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }

    }

    private fun uploadimg(img: String?){
        
        if (!img.isNullOrEmpty()){
            var imageBody: MultipartBody.Part? = null
            val imgFile = File(img)
            Log.d("urii","path is $imgFile")

            if (imgFile.exists()) {
                val imgRequestFile: RequestBody =
                    imgFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                imageBody = MultipartBody.Part.createFormData(
                    "vidFile",
                    imgFile.name,
                    imgRequestFile
                )
            }
            if (imageBody != null) {
                var s=retrofit.postData(imageBody)
                if(s.isSuccessful){
                    Toast.makeText(this, "yooo", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "noo", Toast.LENGTH_SHORT).show()

                }
            }

}}}