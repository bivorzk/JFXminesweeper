package com.example.minesweeper_schoolproject.minesweeper;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class tests {

    private HelloController controller;

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new HelloController();

        setPrivateField(controller, "timerLabel", new Label());
        setPrivateField(controller, "flagLabel", new Label());
        setPrivateField(controller, "gameGrid", new GridPane());
        setPrivateField(controller, "resetButton", new Button());
        setPrivateField(controller, "myComboBox", new ComboBox<String>());
        setPrivateField(controller, "algorithmComboBox", new ComboBox<String>());

        javafx.animation.Timeline mockTimeline = new javafx.animation.Timeline();
        setPrivateField(controller, "timeline", mockTimeline);
        // Note: LevelSelect method removed in MVC refactor; initialize model directly if needed
    }

    private void setPrivateField(Object instance, String fieldName, Object value) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    private Object getPrivateField(Object instance, String fieldName) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }


    
    // TODO 
    // Test MinesweeperModel initialization for different levels (ONE, TWO, THREE)
    // Test MinesweeperModel bomb placement algorithms (DFS, DFS_HARD, FYS)
    // Test MinesweeperModel revealCell method (bomb detection, empty area reveal)
    // Test MinesweeperModel toggleFlag method (flag count updates)
    // Test MinesweeperModel checkWin method (win condition logic)
    // Test MinesweeperModel getNeighbors method (correct neighbor calculation)
    // Test controller integration with model (level changes, algorithm changes)
    // Test timer starts on first click in controller
    // Test flag label updates when placing/removing flags
    // Test reset button functionality and game restart
    // Test combo box selections (levels and algorithms) trigger model updates
    // Test edge cases: clicking on corners, already revealed cells, flagged cells
    // Test game over scenarios (win/lose dialogs, bomb reveal)
    
    
    
}