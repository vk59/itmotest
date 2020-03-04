package ru.itmo.test.questions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import ru.itmo.test.MainActivity
import ru.itmo.test.R

private const val ARG_SCORE = "arg_score"

/**
 * An abstract class for a question fragment, which provides basic methods for getting score data, editing and passing it forward
 * All questions should extend [AbstractQuestionFragment] class
 */
abstract class AbstractQuestionFragment(private val layoutResId: Int) : Fragment() {
    private var score: Int = 0
    protected lateinit var rootView: View
    private var listenerTimePause: OnTimePauseListener? = null
    private var listenerTimeResume: OnTimeResumeListener? = null
    private var listenerNavigateNext: OnNavigateNextListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        score = (activity as MainActivity).score
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(layoutResId, container, false)
        return rootView
    }

    fun onNextButtonPressed() {
        val answerHint = getAnswerHint()
        if (answerHint != -1) { // something is selected
            if (getString(answerHint) != ""){
                listenerTimePause!!.onTimePauseListener()
                score = checkAnswer(score)
                AlertDialog.Builder(this.requireContext())
                    .setMessage(getAnswerHint())
                    .setPositiveButton(android.R.string.ok) { dialog, _ ->
                        listenerTimeResume!!.onTimeResumeListener()
                        dialog.dismiss()

                        (activity as MainActivity).score = score
                        listenerNavigateNext?.onNavigateNextListener()
                    }
                    .setCancelable(false)
                    .show()
            }else{
                score = checkAnswer(score)
                (activity as MainActivity).score = score
                listenerNavigateNext?.onNavigateNextListener()
            }
        }else{ // nothing selected
            Toast.makeText(requireContext(), R.string.nothing_selected, Toast.LENGTH_LONG).show()
        }
    }

    protected abstract fun checkAnswer(prevScore: Int): Int

    protected abstract fun getAnswerHint(): Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTimePauseListener) {
            listenerTimePause = context
        } else {
            throw RuntimeException("$context must implement OnTimePauseListener")
        }
        if (context is OnTimeResumeListener) {
            listenerTimeResume = context
        } else {
            throw RuntimeException("$context must implement OnTimeResumeListener")
        }
        if (context is OnNavigateNextListener) {
            listenerNavigateNext = context
        } else {
            throw RuntimeException("$context must implement OnNavigateNextListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerTimePause = null
        listenerTimeResume = null
        listenerNavigateNext = null
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to handle time pause request
     */
    interface OnTimePauseListener {
        fun onTimePauseListener()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to handle time resume request
     */
    interface OnTimeResumeListener {
        fun onTimeResumeListener()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to handle next fragment action intent
     */
    interface OnNavigateNextListener {
        fun onNavigateNextListener()
    }
}
