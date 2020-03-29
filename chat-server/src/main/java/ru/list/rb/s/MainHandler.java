package ru.list.rb.s;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

public class MainHandler extends SimpleChannelInboundHandler<String> { // Инбаунд - на вход работаем
    private static final List<Channel> channels = new ArrayList<>(); // Общий список каналов
    private static int clientIndex = 1;
    private String clientName; // Имя клиента. Для каждого клиента хэндлер свой

    @Override // При подключении клиента срабатывает ChannelActive
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился: " + ctx);
        channels.add(ctx.channel());
        clientName = "Клиент № " + clientIndex;
        clientIndex++;
        broadcastMessage("SERVER", "Подключился новый клиент: " + clientName);
    }

//    // Метод не требуется. Остался при наследовании от ChannelInboundHandlerAdapter
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        // Чтение из канала от клиента (то что приходит из сети в нетти попадает в ByteBuffer).
//        // Так как данный хэндлер первый от сети, то получаем ByteBuffer
//        ByteBuf buf = (ByteBuf) msg;
//        while (buf.readableBytes() > 0) {
//            System.out.print((char) buf.readByte()); // Читаем по одному байту и преобразовываем в char
//        }
//        buf.release();
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        System.out.println("Получено сообщение " + s);
        // и служебные (настроечные сообщения)
        if(s.startsWith("/")) {
            if(s.startsWith("/changename ")) { // /changename myname1
                String newNickName = s.split("\\s", 2)[1]; // разбиваем по пробелу на две части и берем вторую часть как имя
                broadcastMessage("SERVER", clientName + " поменял ник на " + newNickName);
                clientName = newNickName;
            }
            return;
        }
        broadcastMessage(clientName, s);
        // На выходе будут не строки, а байтбуф
    }

    public void broadcastMessage(String clientName, String message) {
        String out = String.format("[%s]: %s\n", clientName, message); // Шлем всем сообщение с именем клиента
        for (Channel c : channels) { // всем шлем
            c.writeAndFlush(out);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Клиент " + clientName + " отвалился");
        channels.remove(ctx.channel());
        broadcastMessage("SERVER", "Клиент " + clientName + " вышел из сети");
        cause.printStackTrace();
        ctx.close(); // Распечатали исключение и закрываем соединение с этим клиентом
    }
}
