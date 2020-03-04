package ru.itmo.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import ru.itmo.test.data.DataManager
import ru.itmo.test.data.UserData
import java.util.*
import kotlin.collections.ArrayList

class ResultActivity : AppCompatActivity() {
    private var score = 0
    private var time = 0L
    private var code = 0
    private var name = ""

    private var users: ArrayList<UserData> = arrayListOf()
    private var ratingJob: Job? = null
    private lateinit var adapter: RatingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        intent.extras?.also {
            score = it.getInt("score", 0)
            time = it.getLong("time", 0)
            code = it.getInt("code", 0)
            name = it.getString("name", "аноним")
        }

        congratView.text = getString(R.string.congrat) + " $name"
        codeView.text = getString(R.string.code) + " $code"
        scoreValue.text = score.toString()
        timeValue.text = Utils().getFormattedTimeStamp(time)

        button_repeat.setOnClickListener { finish() }

        adapter = RatingAdapter(users, code)
        adapter.setHasStableIds(true)
        recList.layoutManager = LinearLayoutManager(this)
        recList.adapter = adapter

        startObservingRating()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopObservingRating()
    }


    private fun startObservingRating() {
        ratingJob = GlobalScope.launch(Dispatchers.Main) {
            val channel = Channel<ArrayList<UserData>>()
            DataManager.getAllUsersList(channel)

            for (users in channel) {
                users.sortDescending()
                this@ResultActivity.users = users
                adapter.setWorkspaceList(users)
            }
        }
    }

    private fun stopObservingRating() {
        ratingJob?.cancel()
    }
}
