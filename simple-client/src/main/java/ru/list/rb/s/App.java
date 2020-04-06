package ru.list.rb.s;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.*;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Logger logger = LogManager.getRootLogger();
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        AppConfig appConfig = AppConfig.getInstance();
        String appTitle = appConfig.getAppTitle();
        logger.info("Получен конфиг клиента : {host:" + appConfig.getHost() + ", port:" + appConfig.getPort() + "}");

        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.setTitle(appTitle);
        stage.show();
        logger.info("Создана десктоп сцена с параметрами окна " + scene.getWidth() + "x" + scene.getHeight() + " . Со свойствами: " +scene.getProperties().toString());
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        logger.info("Для создания сцены используется " + fxml + ".fxml");
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}