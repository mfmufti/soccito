package com.team9.soccermanager.model.accessor

import androidx.compose.runtime.mutableStateListOf
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
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
                db.collection("users").document(user as String).get().addOnSuccessListener { usr ->
                    if (usr.data != null && usr.id != myId) {
                        waiting++
                        Firebase.firestore.collection("users").document(GS.user!!.id).get().addOnSuccessListener { it ->
                            waiting++
                            getChatID(it.reference, usr.reference, { chatId ->
                                waiting++
                                hasFullyRead(chatId) { read ->
                                    chats.add(
                                        Chat(usr.data!!["fullname"] as String, type, usr.reference, read)
                                    )
                                    decrementAndCheck()
                                }
                                decrementAndCheck()
                            }, {})
                            decrementAndCheck()
                        }.addOnFailureListener {
                        }
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
                    "users" to listOf(userRef1, userRef2),
                    userRef1.id to 0,
                    userRef2.id to 0
                )
                db.collection("chats").add(data).addOnSuccessListener {
                    onSuccess(it.id)
                }.addOnFailureListener { sendError() }
            } else {
                onSuccess(found)
            }
        }.addOnFailureListener { sendError() }
    }

    override fun loadAndListenChat(chatID: String, onLoad: (List<Message>) -> Unit): ListenerRegistration {
        val messages = mutableStateListOf<Message>()

        fun processData(data: Map<String, Any>?) {
            val tmp = data?.get("messages")
            if (tmp != null) {
                messages.clear()
                val newMessages: List<*> = (tmp as List<*>)
                for (message in newMessages) {
                    val m = message as Map<*, *>
                    val isFromCurrent = (m["from"] as DocumentReference).id == GS.user?.id
                    messages.add(Message(isFromCurrent, m["text"] as String, m["time"] as Long))
                }
            }
        }

        val document = Firebase.firestore.collection("chats").document(chatID)
        document.get().addOnSuccessListener{
            processData(it.data)
            onLoad(messages)
        }
        return document.addSnapshotListener({ snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                processData(snapshot.data)
                onLoad(messages)
            }
        })
    }

    override fun getLastSeen(chatID: String, onSuccess: (Long) -> Unit) {
        Firebase.firestore.collection("chats").document(chatID).get().addOnSuccessListener {
            if (it.data != null) {
                onSuccess(it.data!![GS.user!!.id] as Long)
            }
        }
    }

    override fun sendMessage(chatID: String, text: String) {
        val db = Firebase.firestore
        val toAdd = ChatMessage(db.collection("users").document(GS.user!!.id), text, System.currentTimeMillis())
        db.collection("chats").document(chatID).get().addOnSuccessListener {
            val messages = it.data!!["messages"] as List<*>
            val messagesUpdated : ArrayList<Any?> = ArrayList(messages)
            messagesUpdated.add(toAdd)
            db.collection("chats").document(chatID).update("messages", messagesUpdated).addOnSuccessListener {
                db.collection("chats").document(chatID).update(GS.user!!.id, System.currentTimeMillis()).addOnSuccessListener {
                    db.collection("chats").document(chatID).get().addOnSuccessListener {
                        val users = it.data!!["users"] as List<*>
                        var sendToId: String = ""
                        sendToId = if ((users[0] as DocumentReference).id != GS.user!!.id) {
                            (users[0] as DocumentReference).id
                        } else {
                            (users[1] as DocumentReference).id
                        }
                        db.collection("users").document(sendToId).get().addOnSuccessListener { it1 ->
                            if (it1.data!!["notificationToken"] != null) {
                                sendChatNotification(it1.data!!["notificationToken"] as String)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun updateUserReadTime(chatID: String, onSuccess: () -> Unit) {
        Firebase.firestore.collection("chats").document(chatID).update(GS.user!!.id, System.currentTimeMillis()).addOnSuccessListener {
            onSuccess()
        }
    }

    override fun checkChatStatus(onResult: (Boolean) -> Unit) {
        if (GS.user != null) {
            var allSeen = true
            Firebase.firestore.collection("users").document(GS.user!!.id).get().addOnSuccessListener {
                Firebase.firestore.collection("chats").whereArrayContains("users", it.reference).get().addOnSuccessListener { it1 ->
                    for (d in it1.documents) {
                        val msgList = d.data!!["messages"] as List<*>
                        if (msgList.isNotEmpty()) {
                            val lastMsg = msgList[msgList.size - 1] as Map<*, *>
                            val lastMsgTime = lastMsg["time"] as Long
                            val lastChatSeen = d.data!![GS.user!!.id] as Long
                            if (lastChatSeen < lastMsgTime) {
                                allSeen = false
                            }
                        }
                    }
                    onResult(allSeen)
                }
            }
        }
    }


    private fun hasFullyRead(chatID: String, onSuccess: (Boolean) -> Unit) {
        Firebase.firestore.collection("chats").document(chatID).get().addOnSuccessListener {
            val msgList = it.data!!["messages"] as List<*>
            if (msgList.isNotEmpty()) {
                val lastMsg = msgList[msgList.size - 1] as Map<*, *>
                val lastMsgTime = lastMsg["time"] as Long
                val lastChatSeen = it.data!![GS.user!!.id] as Long
                if (lastChatSeen < lastMsgTime) {
                    onSuccess(false)
                } else {
                    onSuccess(true)
                }
            } else {
                onSuccess(true)
            }
        }
    }

    private fun sendChatNotification(token: String): Task<String> {
        // Create the arguments to the callable function.
        val data = mapOf(
            "title" to "New Chat",
            "body" to "${GS.user?.fullname} sent a chat",
            "token" to token
        )

        return Firebase.functions
            .getHttpsCallable("sendNotification")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result.getData()
                result.toString()
                // result
            }
    }

}