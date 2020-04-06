package ru.list.rb.s;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Network {

    private static Logger logger = LogManager.getRootLogger();
    private SocketChannel channel;

    public Network(Callback onMessageReceivedCallback) {
        AppConfig appConfig = AppConfig.getInstance();
        String host = appConfig.getHost();
        int port = appConfig.getPort();
        logger.info("Получен конфиг клиента : {host:" + appConfig.getHost() + ", port:" + appConfig.getPort() + "}");
        // При создании в паралелльном потоке запускается клиент
        // В отдельном потоке запускается, чтобы closeFuture().sync() не заблокировал основной поток (запус интерфейса и пр)
        Thread t = new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup(); // Пул потоков
            logger.info("Создан пул потоков " + Thread.currentThread().getName());
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup)
                        .channel(NioSocketChannel.class) // При подключении к серверу, открывается сокет канал
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel; // При открытии соединения получаем ссылку на соединение и запоминаем эту ссылку в поле channel
                                socketChannel.pipeline().addLast(new StringDecoder(), new StringEncoder(),
                                        new ClientHandler(onMessageReceivedCallback)); // Входящий и исходящий хэндлеры соответственно
                                // При отправке в канал строки, она проходит через encoder с преобразованием её в bytebuf
                                // Декодер наоборот собирает байты в строки при получении из сети
                            }
                        });
                logger.info("Канал настроен, хэндлеры подключены {" + b.toString() + "}, готов к подключению");
                ChannelFuture future = b.connect(host, port).sync();
                logger.info("Подключение... host:" + host + ", port:" + port);
                // Чтобы клиент сразу не закрыл соединение, делаем:
                future.channel().closeFuture().sync(); // блокирующая операция, чтобы ее обойти. надо снаружи дать команду (например по кнопке закрыть)
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Получено исключение");
                logger.error(e);
            } finally {
                workerGroup.shutdownGracefully();
                logger.info("Пул потоков выключается");
            }
        });
        t.setDaemon(true);
        logger.info("Поток " + t.getName() + " запустится как демон");
        t.start();
        logger.info("Запуск потока " + t.getName());
    }

    public void sendMessage(String s) { // Отправить строку не можем, т.к. в канале байт буффер
        logger.info("Отправляется сообщение " + s);
        channel.writeAndFlush(s); // чтобы строки уходили настраиваем конвейер (в initChannel)
    }

    public void close() {
        logger.info("Закрывается канал");
        channel.close();
    }
}
