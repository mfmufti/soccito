package com.team9.soccermanager.model.accessor

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team9.soccermanager.model.GS

object ChatAccessor: ChatDao {
    private val chatSorter = { a: Chat, b: Chat ->
        if (a.type == b.type) {
            a.userRef.id.compareTo(b.userRef.id)
        } else {
            val order = listOf("Admin", "Coach", "Player")
            order.indexOf(a.type).compareTo(order.indexOf(b.type))
        }
    }

    override fun loadChats(onLoad: (List<Chat>) -> Unit, onError: () -> Unit) {
        val chats = mutableStateListOf<Chat>()
        var waiting = 0
        var error = false
        val db = Firebase.firestore
        val myType = GS.user!!.type
        val myId = GS.user!!.id

        val decrementAndCheck = {
            waiting--
            if (waiting == 0) {
                chats.sortWith(chatSorter)
                onLoad(chats)
            }
        }
        val sendError = {
            if (!error) {
                error = true
                onError()
            }
        }
        val iterateAndAdd = { doc: DocumentSnapshot, field: String, type: String ->
            for (user in doc.data!![field] as List<*>) {
                waiting++
                db.collection("users").document(user as String).get().addOnSuccessListener {
                    if (it.data != null && it.id != myId) {
                        chats.add(
                            Chat(it.data!!["fullname"] as String, type, it.reference)
                        )
                    }
                    decrementAndCheck()
                }.addOnFailureListener({ sendError() })
            }
        }

        waiting++

        if (myType != "admin") {
            waiting++
            db.collection("teams").document(GS.user!!.teamID).get().addOnSuccessListener {
                if (it.data == null) return@addOnSuccessListener
                iterateAndAdd(it, "coachIds", "Coach")
                iterateAndAdd(it, "playerIds", "Player")
                decrementAndCheck()
            }.addOnFailureListener({ sendError() })
        }

        if (myType != "player") {
            waiting++
            db.collection("leagues").document(GS.user!!.leagueID).get().addOnSuccessListener {
                if (it.data == null) return@addOnSuccessListener
                iterateAndAdd(it, "adminIds", "Admin")
                for (team in it.data!!["teamIds"] as List<*>) {
                    waiting++
                    db.collection("teams").document(team as String).get().addOnSuccessListener {
                        if (it.data != null) {
                            iterateAndAdd(it, "coachIds", "Coach")
                            decrementAndCheck()
                        }
                    }.addOnFailureListener({ sendError() })
                }
                decrementAndCheck()
            }.addOnFailureListener({ sendError() })
        }
        decrementAndCheck()
    }

    override fun getChatID(userRef1: DocumentReference, userRef2: DocumentReference, onSuccess: (String) -> Unit, onError: () -> Unit) {
        val db = Firebase.firestore
        var error = false
        val sendError = {
            if (!error) {
                error = true
                onError()
            }
        }

        db.collection("chats").whereArrayContains("users", userRef2).get().addOnSuccessListener {
            if (it.metadata.isFromCache) {
                sendError()
                return@addOnSuccessListener
            }
            var found = ""

            for (d in it.documents) {
                val users = d.data!!["users"] as List<*>
                if (users.contains(userRef1)) {
                    found = d.id
                }
            }

            if (found.isEmpty()) {
                val data = mapOf(
                    "messages" to listOf<Any>(),
                    "users" to listOf(userRef1, userRef2)
                )
                db.collection("chats").add(data).addOnSuccessListener {
                    onSuccess(it.id)
                }.addOnFailureListener { sendError() }
            } else {
                onSuccess(found)
            }
        }.addOnFailureListener { sendError() }
    }

    override fun loadAndListenChat(chatID: String, onLoad: (List<Message>) -> Unit) {
        val messages = mutableStateListOf<Message>()

        fun processData(data: Map<String, Any>?) {
            val tmp = data?.get("messages")
            if (tmp != null) {
                messages.clear()
                val newMessages: List<*> = (tmp as List<*>)
                for (message in newMessages) {
                    val m = message as Map<*, *>
                    val isFromCurrent = (m["from"] as DocumentReference).id == GS.user?.id
                    messages.add(Message(isFromCurrent, m["text"] as String))
                }
            }
        }

        val document = Firebase.firestore.collection("chats").document(chatID)
        document.get().addOnSuccessListener{
            processData(it.data)
            onLoad(messages)
        }
        document.addSnapshotListener({ snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                processData(snapshot.data)
                onLoad(messages)
            }
        })
    }

    override fun sendMessage(chatID: String, text: String) {
        val db = Firebase.firestore
        val toAdd = hashMapOf(
            "from" to db.collection("users").document(GS.user!!.id),
            "text" to text
        )
        db.collection("chats").document(chatID).get().addOnSuccessListener {
            val messages = it.data!!["messages"] as List<*>
            val messagesUpdated : ArrayList<Any?> = ArrayList(messages)
            messagesUpdated.add(toAdd)
            db.collection("chats").document(chatID).update("messages", messagesUpdated)
        }
    }
}