package ru.list.rb.s;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    private Callback onMessageReceivedCallback;

    public ClientHandler(Callback onMessageReceivedCallback) {
        this.onMessageReceivedCallback = onMessageReceivedCallback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        if (onMessageReceivedCallback != null) { // При создании соединения Network, если передан Callback, то при получении msg бубет вызывать метод callback
            onMessageReceivedCallback.callback(msg);
        }
    }
}