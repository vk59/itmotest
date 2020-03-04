package ru.itmo.test

import android.R.attr.fragment
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_main.*
import ru.itmo.test.questions.*
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.collections.ArrayList


private var time = 0L
private val mainHandler = Handler(Looper.getMainLooper())
private lateinit var timeRunnable: Runnable
private var varient = ArrayList<AbstractQuestionFragment>()
private var curPos = 0
private val questions = arrayListOf(
        Question1Fragment(),
        Question2Fragment(),
        Question3Fragment(),
        Question4Fragment(),
        Question5Fragment(),
        Question6Fragment(),
        Question7Fragment(),
        Question8Fragment(),
        Question9Fragment(),
        Question10Fragment(),
        Question11Fragment(),
        Question12Fragment(),
        Question13Fragment(),
        Question14Fragment(),
        Question15Fragment(),
        Question16Fragment(),
        Question17Fragment(),
        Question18Fragment(),
        Question19Fragment())

class MainActivity : AppCompatActivity(),
    AbstractQuestionFragment.OnTimePauseListener, AbstractQuestionFragment.OnTimeResumeListener, AbstractQuestionFragment.OnNavigateNextListener {

    var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            time = 0L // means it's not a screen rotation
        }

        FirebaseApp.initializeApp(this@MainActivity)

        button.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container) as AbstractQuestionFragment
            currentFragment.onNextButtonPressed()
        }

        button.post {
            timeRunnable = object : Runnable {
                override fun run() {
                    time += 10
                    timerText.text = Utils().getFormattedTimeStamp(time)
                    mainHandler.postDelayed(this, 10)
                }
            }
            mainHandler.post(timeRunnable)
        }

        /*val myNavHostFragment: NavHostFragment = nav_host_fragment as NavHostFragment
        val inflater = myNavHostFragment.navController.navInflater
        val graph = if (Math.random() < 0.5) inflater.inflate(R.navigation.nav_graph1) else inflater.inflate(R.navigation.nav_graph2)
        myNavHostFragment.navController.graph = graph*/

        val copy = ArrayList<AbstractQuestionFragment>()
        copy.addAll(questions.filterNotNull())
        varient = getRandomElement(copy, 10)

        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(R.id.container, varient[curPos])
        transaction.commit()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_title)
            .setMessage(R.string.exit_confirm)
            .setPositiveButton(android.R.string.yes) { _, _ -> finish() }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        //remove timer callback to prevent running at the same time as the new one created by onCreate()
        mainHandler.removeCallbacks(timeRunnable)
    }

    override fun onTimePauseListener() {
        mainHandler.removeCallbacks(timeRunnable)
    }

    override fun onTimeResumeListener() {
        mainHandler.post(timeRunnable)
    }

    override fun onNavigateNextListener() {
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()

        curPos++
        try {
            //replace your current container being most of the time as FrameLayout
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
            transaction.replace(R.id.container, varient[curPos])
            transaction.commit()
        }catch (e: IndexOutOfBoundsException){
            startActivity(Intent(this, RegistrationActivity::class.java).apply {
                this.putExtra("score", score)
                this.putExtra("time", time)
                this.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            })
        }
    }


    // Function select an element base on index and return
    // an element
    private fun getRandomElement(list: ArrayList<AbstractQuestionFragment>,
                         totalItems: Int): ArrayList<AbstractQuestionFragment> {
        val rand = Random()
        // create a temporary list for storing
        // selected element
        val newList: ArrayList<AbstractQuestionFragment> = ArrayList()
        for (i in 0 until totalItems) { // take a random index between 0 to size of given List
            val randomIndex: Int = rand.nextInt(list.size)
            // add element in temporary list
            newList.add(list[randomIndex])
            // Remove selected element from original list
            list.removeAt(randomIndex)
        }
        return newList
    }
}
