package com.nieldeokar.hurumessenger.ui.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.nieldeokar.hurumessenger.HuruApp
import com.nieldeokar.hurumessenger.R
import com.nieldeokar.hurumessenger.packets.MePacket
import com.nieldeokar.hurumessenger.services.LocalTransport
import timber.log.Timber
import com.nieldeokar.hurumessenger.models.User
import com.nieldeokar.hurumessenger.packets.MessagePacket
import com.nieldeokar.hurumessenger.ui.MyDividerItemDecoration
import com.nieldeokar.hurumessenger.ui.RecyclerTouchListener
import com.nieldeokar.hurumessenger.ui.chat.ChatActivity
import com.nieldeokar.hurumessenger.ui.main.di.DaggerMainActivityComponent
import com.nieldeokar.hurumessenger.ui.main.di.MainActivityModule
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList
import javax.inject.Inject


class MainActivity : AppCompatActivity(), LocalTransport.OnPacketReceivedListener {
    @Inject
    lateinit var localTransport: LocalTransport

    val list = ArrayList<User>()

    val adapter = UsersAdapter(list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DaggerMainActivityComponent.builder()
                .applicationComponent((application as HuruApp).getComponent())
                .mainActivityModule(MainActivityModule())
                .build()
                .inject(this)


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16))
        recyclerView.itemAnimator = DefaultItemAnimator()


        recyclerView.adapter = adapter


        recyclerView.addOnItemTouchListener(RecyclerTouchListener(applicationContext, recyclerView, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View?, position: Int) {
                moveToChat(position)

            }

            override fun onLongClick(view: View?, position: Int) {

            }
        }))

    }

    private fun moveToChat(position: Int) {
        val intent = Intent(this,ChatActivity::class.java)
        intent.putExtra("USER", list[position])
        startActivity(intent)
    }


    fun send(view: View) {
        val usrName = etUserName.text.toString()
        if (usrName.isNotBlank() && usrName.isNotEmpty()) {
            (application as HuruApp).getAccount()?.name =usrName

            localTransport.sendMePacketNow()
        }else{
            Toast.makeText(this,"Please enter username ",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMessagePacketReceived(messagePacket: MessagePacket) {
        val str = "msgPacketReceived from : ${messagePacket.senderId} \n"
        Timber.d(str)


    }

    override fun onMePacketReceived(mePacket: MePacket) {
        val str = "mePacketReceived from : ${mePacket.name} \n"
        Timber.d(str)

        var user = (application as HuruApp).getAccount()?.findUserByDeviceId(mePacket.deviceId)

        if(user == null){
            user = User()
        }
        user.localAddressCard = mePacket.localAddressCard.toBytes()
        user.name = mePacket.name
        user.deviceId = mePacket.deviceId


        runOnUiThread({
            for ((i, usr) in adapter.userList.withIndex()){
                if(usr.deviceId == user.deviceId){
                    adapter.userList[i] = user
                    adapter.notifyItemChanged(i)
                    return@runOnUiThread
                }
            }
            (application as HuruApp).getAccount()?.usersList?.add(user)
            adapter.addUser(user)
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        localTransport.stop()
    }

    override fun onStart() {
        super.onStart()
        localTransport.setOnPacketReceivedListener(this)
    }

    override fun onStop() {
        super.onStop()
//        localTransport.setOnPacketReceivedListener(null)
    }
}
