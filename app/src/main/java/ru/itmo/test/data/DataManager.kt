package ru.itmo.test.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.tasks.await
import ru.itmo.test.data.DataManager.DB
import kotlin.coroutines.CoroutineContext


object DataManager: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    val DB = newFixedThreadPoolContext(1, "DB")

    fun insertEntry(entry: UserData): Deferred<String> = GlobalScope.async(DB) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("rating").add(entry).await().id
    }

    fun getLastUserCode(): Deferred<Int> = GlobalScope.async(DB) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val query = db.collection("rating").orderBy("user_code", Query.Direction.DESCENDING).limit(1)
        val queryResult = query.get().await()
        if (!queryResult.isEmpty) {
            return@async queryResult.documents[0].toObject(UserData::class.java)!!.user_code
        }else{
            return@async -1
        }
    }

    suspend fun getAllUsersList(channel: SendChannel<ArrayList<UserData>>) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val query = db.collection("rating")
        query.addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
            GlobalScope.launch(DB) {
                val users = ArrayList<UserData>()
                for (user in querySnapshot!!.documents) {
                    user?.toObject(UserData::class.java).let { users.add(it!!) }
                }
                channel.send(users)
            }
        }
    }
}