# directline-chatbot-sdk

A first attempt to create a simple SDK to connect directly a Microsoft Azure Web App Bot into an Android App.
This SDK does **not** give any UI or chatbot interface. The SDK connects to the Web App Bot and handles message reception and sending.

## Prerequisites

You **must** create a Web App Bot using the Microsoft Azure services.

<https://docs.microsoft.com/en-us/bot-framework/bot-service-quickstart>

Then you need to open the Direct Line channel API and get the associated secret

<https://docs.microsoft.com/en-us/bot-framework/bot-service-channel-connect-directline>

Direct Line is the REST API given by Microsoft to communicate _via_ HTTPS to a Web App Bot server.

## How it works

The SDK gets a URL to connect _via_ `WebSocket` with your Web App Bot. It then opens this `WebSocket` and lets you receive and send text messages from your Web App Bot.

## Initialization

Instantiate a new `DirectLineChatbot` using your Direct Line API secret

```java
final DirectLineChatbot chatbot = new DirectLineChatbot("YOUR_DIRECTLINE_SECRET");
```

You can then start your chatbot by implementing the `DirectLineChatbot.Callback`. Once the Web Socket is opened, the `onStarted()` method gets called. Everytime you receive a text message from your Web App Bot, the `onMessageReceived(String)` method gets called. Feel free to add this message into your UI.

```java
chatbot.start(new DirectLineChatbot.Callback()
{
  @Override
  public void onStarted()
  {
    Log.d("CHATBOT", "Started);
  }

  @Override
  public void onMessageReceived(@NotNull String message)
  {
    Log.d("CHATBOT", message);
  }
});
```

## Talk with the Web App Bot

Once the `onStarted()` method has been called, you can send messages to your Web App Bot.

```java
chatbot.send("Hello, Bot!");
```

Your Web App Bot answers will be sent back to your `onMessageReceived(String)` implementation.

## Parameters

To change the name of yourself (default is `Me`), use the `setUser(String)` method

```java
chatbot.setUser("John Doe");
```

If you want debug logs, use the `setDebug(boolean) method`

```java
chatbot.setDebug(true);
```

## Further improvements

For now, the SDK only handles text messages. Further improvements will include other types of messages to communicate with the Web App Bot.

## Third party libraries

The SDK uses [Retrofit](http://square.github.io/retrofit/) to call the REST API and [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket) to manage the WebSocket.

## License

This SDK is under the MIT license.

## Author

This SDK has been made by David Fournier @ [Smart and Soft](https://smartnsoft.com/)
