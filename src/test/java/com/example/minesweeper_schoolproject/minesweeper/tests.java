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
        controller.LevelSelect(Levels.ONE);
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

    @Test
    void testLevelOneInitialization() throws Exception {
        int size = (int) getPrivateField(controller, "SIZE");
        int bombs = (int) getPrivateField(controller, "BOMB_COUNT");

        assertEquals(9, size);
        assertEquals(10, bombs);
    }

    @Test
    void testInitialFlags() throws Exception {
        Label flagLabel = (Label) getPrivateField(controller, "flagLabel");
        int bombs = (int) getPrivateField(controller, "BOMB_COUNT");

        assertTrue(flagLabel.getText().contains(String.valueOf(bombs)));
    }

    @Test
    void testResetButtonText() throws Exception {
        Button resetBtn = (Button) getPrivateField(controller, "resetButton");
        assertEquals("🙂", resetBtn.getText());
    }

    @Test
    void testGridCreation() throws Exception {
        Object[][] grid = (Object[][]) getPrivateField(controller, "grid");
        assertNotNull(grid);
        assertEquals(9, grid.length);
    }

    @Test
    void testGameNotStartedInitially() throws Exception {
        boolean started = (boolean) getPrivateField(controller, "gameStarted");
        assertFalse(started);
    }

    @Test
    void testLevelThreeSelection() throws Exception {
        controller.LevelSelect(Levels.THREE);
        int size = (int) getPrivateField(controller, "SIZE");
        int bombs = (int) getPrivateField(controller, "BOMB_COUNT");

        assertEquals(20, size);
        assertEquals(40, bombs);
    }
}