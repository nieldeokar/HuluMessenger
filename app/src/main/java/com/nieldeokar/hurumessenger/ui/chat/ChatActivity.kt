package com.nieldeokar.hurumessenger.ui.chat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.nieldeokar.hurumessenger.HuruApp
import com.nieldeokar.hurumessenger.R
import com.nieldeokar.hurumessenger.models.Message
import com.nieldeokar.hurumessenger.packets.MePacket
import com.nieldeokar.hurumessenger.services.LocalTransport
import timber.log.Timber
import com.nieldeokar.hurumessenger.models.User
import com.nieldeokar.hurumessenger.packets.MessagePacket
import com.nieldeokar.hurumessenger.ui.MyDividerItemDecoration
import com.nieldeokar.hurumessenger.ui.RecyclerTouchListener
import com.nieldeokar.hurumessenger.ui.chat.di.ChatActivityModule
import com.nieldeokar.hurumessenger.ui.chat.di.DaggerChatActivityComponent
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.editor.*
import java.util.ArrayList
import javax.inject.Inject


class ChatActivity : AppCompatActivity(), LocalTransport.OnPacketReceivedListener {

    @Inject
    lateinit var localTransport: LocalTransport

    var list  : ArrayList<Message>? = ArrayList<Message>()

    val adapter = ChatAdapter(list)
    var currentUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        DaggerChatActivityComponent.builder()
                .applicationComponent((application as HuruApp).getComponent())
                .chatActivityModule(ChatActivityModule())
                .build()
                .inject(this)



        chatRecycler.layoutManager = LinearLayoutManager(this)
        chatRecycler.addItemDecoration(MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16))
        chatRecycler.itemAnimator = DefaultItemAnimator()

        currentUser = intent.getParcelableExtra("USER")
        list = currentUser?.messages
        adapter.messageList = list
        chatRecycler.adapter = adapter


        chatRecycler.addOnItemTouchListener(RecyclerTouchListener(applicationContext, recyclerView, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View?, position: Int) {

            }

            override fun onLongClick(view: View?, position: Int) {

            }
        }))

    }

    fun sendMessage(view: View) {
        val strMessage = edit_message.text.toString()
        if (strMessage.isNotBlank() && strMessage.isNotEmpty() && currentUser != null) {

            localTransport.sendMessagePacket(currentUser!!,strMessage)
        }else{
            Toast.makeText(this,"Please enter message ",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMePacketReceived(mePacket: MePacket) {
        val str = "mePacketReceived from : ${mePacket.name} \n"
        Timber.d(str)

    }

    override fun onMessagePacketReceived(messagePacket: MessagePacket) {
        val str = "msgPacketReceived from : ${messagePacket.senderId} \n"
        Timber.d(str)

        if(currentUser?.deviceId == messagePacket.senderId) {

            var message = currentUser?.findMessageById(messagePacket.messageId)

            if (message == null) {
                message = Message()
            }

            message.textBody = messagePacket.msgBody
            message.timeOfCreation = messagePacket.timeOfCreation
            message.senderId = messagePacket.senderId
            message.msgId = messagePacket.messageId

            runOnUiThread {
                    for ((i, msg) in adapter.messageList.withIndex()){
                        if(msg.msgId == message.msgId){
                            adapter.messageList[i] = message
                            adapter.notifyItemChanged(i)
                            return@runOnUiThread
                        }
                    }
                    adapter.addMessage(message)
            }

        }else {
            Timber.d("Received msg from another user")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        localTransport.setOnPacketReceivedListener(this)
    }

    override fun onStop() {
        super.onStop()
        localTransport.setOnPacketReceivedListener(null)
    }
}
