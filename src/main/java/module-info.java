module com.example.minesweeper_schoolproject.minesweeper {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.example.minesweeper_schoolproject.minesweeper to javafx.fxml;
    exports com.example.minesweeper_schoolproject.minesweeper;
}