module ru.list.rb.s {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.all;

    opens ru.list.rb.s to javafx.fxml;
    exports ru.list.rb.s;
}