package ru.itmo.test.questions

import android.widget.RadioGroup
import ru.itmo.test.R

class Question10Fragment : AbstractQuestionFragment(R.layout.fragment_question10) {
    override fun checkAnswer(prevScore: Int): Int {
        return when (rootView.findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId){
            R.id.answer1 -> prevScore
            R.id.answer2 -> prevScore
            R.id.answer3 -> prevScore
            R.id.answer4 -> prevScore + 1
            else -> prevScore
        }
    }

    override fun getAnswerHint(): Int {
        return when (rootView.findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId){
            R.id.answer1 -> R.string.question_10_answer1_hint
            R.id.answer2 -> R.string.question_10_answer2_hint
            R.id.answer3 -> R.string.question_10_answer3_hint
            R.id.answer4 -> R.string.question_10_answer4_hint
            else -> -1 // nothing selected
        }
    }
}
