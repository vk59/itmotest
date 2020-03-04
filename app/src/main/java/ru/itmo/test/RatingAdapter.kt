package ru.itmo.test

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.itmo.test.data.UserData


class RatingAdapter(private var users: ArrayList<UserData>, private val user_code: Int) : RecyclerView.Adapter<RatingAdapter.UserViewHolder>() {
    
    override fun onBindViewHolder(userViewHolder: UserViewHolder, i: Int) {
        val pos = userViewHolder.adapterPosition
        val item = users[pos]

        userViewHolder.nameView.text = "${pos+1}. ${item.name}"
        userViewHolder.scoreView.text = item.test_score.toString()
        userViewHolder.timeView.text = Utils().getFormattedTimeStamp(item.test_time)

        if (item.user_code == user_code){
            userViewHolder.arrowView.visibility = View.VISIBLE
            userViewHolder.pointingView.visibility = View.VISIBLE
        }else{
            userViewHolder.arrowView.visibility = View.GONE
            userViewHolder.pointingView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }
    
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): UserViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.rating_item, viewGroup, false)
        return UserViewHolder(itemView, i)
    }

    override fun getItemId(position: Int): Long {
        return users[position].user_code.toLong()
    }

    fun setWorkspaceList(users: ArrayList<UserData>) {
        this.users = users
        notifyDataSetChanged()
    }

    class UserViewHolder(v: View, pos: Int) : RecyclerView.ViewHolder(v) {
        var nameView: TextView = v.findViewById<View>(R.id.nameView) as TextView
        var scoreView: TextView = v.findViewById<View>(R.id.scoreView) as TextView
        var timeView: TextView = v.findViewById<View>(R.id.timeView) as TextView

        var arrowView: ImageView = v.findViewById<View>(R.id.arrowView) as ImageView
        var pointingView: TextView = v.findViewById<View>(R.id.pointingView) as TextView
    }
}