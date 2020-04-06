package ru.list.rb.s;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ServerApp {

    private static Logger logger = LogManager.getRootLogger();

    public static void main(String[] args) throws IOException {

        AppConfig conf = AppConfig.getInstance();
        logger.info("Получен конфиг : {host:" + conf.getHost() + ", port:" + conf.getPort() + "}");
        int port = conf.getPort();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // Пул потоков для работы с подключающимися клиентами (иниц)
        logger.info("Создан основной пул потоков для инициализации работы с подключениями");
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // Для работы. По умолч. около 20
        logger.info("Создан рабочий пул потоков");
        try {
            ServerBootstrap bootstrap = new ServerBootstrap(); // Для преднастройки сервера
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // Для подключения клиентов используется канал
                    .childHandler(new ChannelInitializer<SocketChannel>() { // Настраиваем процесс общения с клиентом
                        // При подключении, информация о подключении лежит в SocketChannel (адрес. потоки отправки и т.п.)
                        // После запуска сервера требуются хэндлеры (может быть много последовательно)
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // для каждого клиента свой конвеер создается, каждому клиенту можно разные хэндлеры
                            // Так как они в начале конвейера, то мы работаем только со строками
                            socketChannel.pipeline().addLast(new StringDecoder(), new StringEncoder(), new MainHandler());
                        }
                    });
            logger.info("Канал настроен, хэндлер подключен {" + bootstrap.toString() + "}, готов к подключению");
            // Сервер настроен, запускаем
            ChannelFuture future = bootstrap.bind(port).sync(); // Привязываем старт сервера к порту. Синк - запускаем
            logger.info("Запущен сервер на порту " + port);
            future.channel().closeFuture().sync(); // closeFuture() и sync() - "сидим и ждем" когда сервер кто-то остановит
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Получено исключение");
            logger.error(e);
        } finally { // если Sync прошли и сервер остановили, то закрываем потоки
            bossGroup.shutdownGracefully();
            logger.info("Основной пул потоков отключен");
            workerGroup.shutdownGracefully();
            logger.info("Рабочий пул потоков отключен");
        }
    }
}
