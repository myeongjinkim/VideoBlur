package com.example.videoblur


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat.*
import androidx.core.content.ContextCompat
import com.example.videoblur.ui.VideoBlurTheme

private val PermissionsCode = 1001
private val VIDEOFILE_REQUEST = 101

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPermission()
        setContent {
            MyApp(){
                CreateButton("동영상 불러오기")
            }
        }
    }
    private fun setPermission() {
        when{
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED -> {
                requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PermissionsCode)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PermissionsCode -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한을 수락해 주세요", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == VIDEOFILE_REQUEST){

                var source:Uri? = data?.data
                println("hello $source");

                val intent = Intent(this, VideoActivity::class.java)
                intent.putExtra("uri", source)
                startActivity(intent)

            }
        }else{
            Toast.makeText(this, "동영상을 선택해주세요", Toast.LENGTH_LONG).show()
        }
    }

}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    VideoBlurTheme {
        // A surface container using the 'background' color from the theme
        Surface() {
            content()

        }
    }
}


@Composable
fun CreateButton(name: String) {
    val context = AmbientContext.current
    Column(modifier = Modifier.fillMaxSize().background(color = Color.White),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { /*TODO*/
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("video/*")
            startActivityForResult(context as Activity, intent, VIDEOFILE_REQUEST, null)

        }

        ) {
            Text(
                text = "$name!",
                style = MaterialTheme.typography.h6.copy(Color.White)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp{
        CreateButton("동영상 불러오기")
    }
}