package com.smartnsoft.directlinechatbotapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartnsoft.directlinechatbot.DirectLineChatbot;
import org.jetbrains.annotations.NotNull;

/**
 * A minimal Java Activity example to run DirectLineChatbot
 *
 * @author David Fournier
 * @since 2018.03.06
 */

public class MainJavaActivity
    extends AppCompatActivity
{

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final DirectLineChatbot chatbot = new DirectLineChatbot("YOUR_DIRECTLINE_SECRET");
    chatbot.setDebug(true);
    chatbot.setUser("David Fournier");

    chatbot.start(new DirectLineChatbot.Callback()
    {
      @Override
      public void onStarted()
      {
        chatbot.send("Bonjour !");
      }

      @Override
      public void onMessageReceived(@NotNull String message)
      {
        Log.d("CHATBOT", message);
      }
    });
  }
}
