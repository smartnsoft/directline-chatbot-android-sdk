// The MIT License (MIT)
//
// Copyright (c) 2018 Smart&Soft
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.smartnsoft.directlinechatbot

import android.util.Log
import com.google.gson.Gson
import com.smartnsoft.directlinechatbot.bo.Id
import com.smartnsoft.directlinechatbot.bo.Message
import com.smartnsoft.directlinechatbot.bo.MessageReceived
import com.smartnsoft.directlinechatbot.bo.StartConversation
import com.smartnsoft.directlinechatbot.ws.WebService
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import java.net.URI

/**
 * A utility class that creates a bridge between Android SDK and Microsoft DirectLine SDK.
 * Initialize the DirectLineChatbot using the DirectLine secret you can find in your Wab App Bot project channels on Microsoft Azure.
 * Start the DirectLineChatbot and get updated with the start of the connection and each time you receive a text message from the chatbot.
 * You can send text messages to the chatbot using the method send(String)
 *
 * The DirectLineChatbot opens a WebSocket with your Web App Bot
 *
 * @author David Fournier
 * @since 2018.03.06
 */

class DirectLineChatbot(val secret: String)
{

  interface Callback
  {

    /**
     * Gets called when a successful connection has been made with the chatbot
     */
    fun onStarted()

    /**
     * Gets called when a not-successful connection has been made with the chatbot
     */
    fun onClosed()

    /**
     * Gets called every time a message *from the bot* has been received
     * @message: the MessageActivity from the chatbot
     */
    fun onMessageReceived(message: MessageActivity)

    /**
     * Gets called every time a message *from the bot* has been received
     * @error: the text message from error exception
     */
    fun onErrorReceived(error: String)

  }

   companion object
   {
     private const val TAG = "WEB SOCKET"

     private val GSON = Gson()
   }

  /**
   * The user name as sent to the chatbot
   */
  var user: String = "Me"

  /**
   * If turned to true, will display verbose logs
   */
  var debug: Boolean = false

  private var webSocket: WebSocketClient? = null

  private var conversationId: String? = null

  private var callback: Callback? = null

  private var header: String = "Bearer ${secret}"

  private var id = Id(user)

  /**
   * Sends asynchronously a text message to the chatbot.
   */
  fun send(message: String)
  {
    conversationId?.let {
      val messageObj = Message("message", id, message)
      WebService.api.send(messageObj, it, header).enqueue(object : retrofit2.Callback<Id>
      {
        override fun onResponse(call: Call<Id>?, response: Response<Id>?)
        {
          response?.body()?.let { _ ->
            log("MESSAGE \"${message}\" SENT SUCCESSFULLY")
          }
        }

        override fun onFailure(call: Call<Id>?, t: Throwable?)
        {
          t?.printStackTrace()
        }
      })
    }
        ?: throw IllegalStateException("The DirectLineChatbot must be initialized first. Call start().")
  }

  /**
   * Starts asynchronously a WebSocket connection with the Microsoft Web App Bot.
   * When started, the onStarted() method from the @callback will be called.
   */
  fun start(callback: Callback)
  {
    this.callback = callback
    WebService.api.startConversation(header).enqueue(object : retrofit2.Callback<StartConversation>
    {
      override fun onResponse(call: Call<StartConversation>?, response: Response<StartConversation>?)
      {
        response?.body()?.let { body ->
          body.streamUrl.let { streamUrl ->
            log(streamUrl)
            startWebSocket(streamUrl)
          }
          conversationId = body.conversationId
        }
      }

      override fun onFailure(call: Call<StartConversation>?, t: Throwable?)
      {
        t?.printStackTrace()
      }
    })
  }

  /**
   * Closes the WebSocket of the Web App Bot.
   * Any further call to send() method will throw an exception
   */
  fun stop()
  {
    conversationId = null;
    webSocket?.close();
  }

  private fun log(log: String)
  {
    if (debug)
    {
      Log.d(TAG, log)
    }
  }

  private fun startWebSocket(streamUrl: String)
  {
    webSocket = object : WebSocketClient(URI.create(streamUrl))
    {
      override fun onOpen(handshakedata: ServerHandshake?)
      {
        log("OPEN")
        callback?.onStarted()
      }

      override fun onClose(code: Int, reason: String?, remote: Boolean)
      {
        log("CLOSE")
        callback.onClosed()
      }

      override fun onMessage(message: String?)
      {
        log("MESSAGE RECEIVED : ${message}")
        val messageReceived = GSON.fromJson(message, MessageReceived::class.java)
        messageReceived?.watermark?.let {
          callback?.onMessageReceived(messageReceived.activities[0])
        }
      }

      override fun onError(ex: Exception?)
      {
        Log.e(TAG, ex?.message?)
        callback.onErrorReceived(ex.message?: "ERROR")
      }
    }
    webSocket?.connect()
  }
}