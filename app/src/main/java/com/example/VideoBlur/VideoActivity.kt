package com.example.VideoBlur

import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.example.VideoBlur.databinding.PlayerBinding
import com.google.android.exoplayer2.ui.PlayerView


private lateinit var playerControl: PlayerControl
private lateinit var myPlayerView:PlayerView

class VideoActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val source = intent.getParcelableExtra<Uri>("uri")
        playerControl = PlayerControl(this)
        setContent {
            MyApp() {
                PlayerSurface(source)

            }

        }
    }

    override fun onResume() {
        super.onResume()
        playerControl.startPlayer()
    }

    override fun onPause() {
        super.onPause()
        playerControl.stopPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerControl.setPlayerRelease()
    }



}

@Composable
fun PlayerSurface(source:Uri?){
    val context = AmbientContext.current
    playerControl.prepare(source)

    Column(
            modifier = Modifier.fillMaxSize().background(color = Color.White),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {



        AndroidViewBinding(PlayerBinding::inflate) {
            myPlayerView = playerView
            if (myPlayerView.getParent() != null) {
                (myPlayerView.getParent() as ViewGroup).removeView(myPlayerView) // <- fix
            }

        }
        playerControl.setPlayer(myPlayerView)
        AndroidView(
                viewBlock = { myPlayerView},
                modifier = Modifier.background(color = Color.Black)){

            myPlayerView.findViewById<SeekBar>(R.id.exo_blur).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    // TODO Auto-generated method stub
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // TODO Auto-generated method stub
                }

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    // TODO Auto-generated method stub
                    myPlayerView.findViewById<TextView>(R.id.exo_blur_text).setText("$progress")
                    //glPlayerView.setBlur(progress)

                }
            })
        }

    }
}