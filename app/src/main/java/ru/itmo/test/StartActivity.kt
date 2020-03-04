package ru.itmo.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        button_go.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            })
        }
    }
}
