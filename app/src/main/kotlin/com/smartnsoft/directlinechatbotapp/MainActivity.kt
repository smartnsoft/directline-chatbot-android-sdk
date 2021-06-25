package com.smartnsoft.directlinechatbotapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.smartnsoft.directlinechatbot.DirectLineChatbot

/**
 * A minimal Kotlin Activity example to run DirectLineChatbot
 *
 * @author David Fournier
 * @since 2018.03.06
 */

class MainActivity :
    AppCompatActivity()
{

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val chatbot = DirectLineChatbot(secret = "YOUR_DIRECTLINE_SECRET")
    chatbot.debug = true
    chatbot.user = "David Fournier"

    chatbot.start(callback = object : DirectLineChatbot.Callback
    {
      override fun onStarted()
      {
        chatbot.send("Bonjour !")
      }

      override fun onClosed() {
        Log.d("CHATBOT", "Socket Closed")
      }

      override fun onMessageReceived(message: MessageActivity)
      {
        Log.d("CHATBOT", message)
      }

      override fun onErrorReceived(error: String) {
        Log.e("CHATBOT", error)
      }
    })
  }

}
