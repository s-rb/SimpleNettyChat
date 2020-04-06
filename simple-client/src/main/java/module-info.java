module ru.list.rb.s {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.all;
    requires org.apache.logging.log4j;

    opens ru.list.rb.s to javafx.fxml;
    exports ru.list.rb.s;
}