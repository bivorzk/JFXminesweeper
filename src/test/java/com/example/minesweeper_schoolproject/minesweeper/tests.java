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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class tests {

    private HelloController controller;
    private MinesweeperModel model;

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {}
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new HelloController();
        model = new MinesweeperModel();

        setPrivateField(controller, "timerLabel", new Label());
        setPrivateField(controller, "flagLabel", new Label());
        setPrivateField(controller, "gameGrid", new GridPane());
        setPrivateField(controller, "resetButton", new Button());
        setPrivateField(controller, "myComboBox", new ComboBox<String>());
        setPrivateField(controller, "algorithmComboBox", new ComboBox<String>());
        setPrivateField(controller, "model", model);

        javafx.animation.Timeline mockTimeline = new javafx.animation.Timeline();
        setPrivateField(controller, "timeline", mockTimeline);

        model.setLevel(Levels.ONE);
    }


    @Test
    void testModelLevels() {
        model.setLevel(Levels.ONE);
        assertEquals(9, model.getSize(), "First level is wrong size");
        assertEquals(10, model.getBombCount(), "First level bombcount is wrong");

        model.setLevel(Levels.TWO);
        assertEquals(16, model.getSize(), "Second level is wrong size");
        assertEquals(26, model.getBombCount(), "Second level bombcount is wrong");

        model.setLevel(Levels.THREE);
        assertEquals(20, model.getSize(), "Third level is wrong size");
        assertEquals(40, model.getBombCount(), "Third level bombcount is wrong");
    }

    @Test
    void testGetNeighborsEdgeCases() {
        Cell corner = model.getGrid()[0][0];
        assertEquals(3, model.getNeighbors(corner).size());

        Cell center = model.getGrid()[1][1];
        assertEquals(8, model.getNeighbors(center).size());
    }

    @Test
    void testBombPlacementAlgorithms() {
        List<Cell> forbidden = new ArrayList<>();
        forbidden.add(model.getGrid()[0][0]);

        model.setAlgorithm(Algorithms.FYS);
        model.startGame(forbidden);
        int bombs = countBombsInGrid();
        assertEquals(model.getBombCount(), bombs, "FYS didn't generate the right amount of bombs");
    }

    @Test
    void testWinConditionLogic() {
        model.initializeGrid();
        for (Cell[] row : model.getGrid()) {
            for (Cell c : row) {
                if (!c.hasBomb) c.reveal();
            }
        }
        assertTrue(model.checkWin(), "Game has to be in won status");
    }

    @Test
    void testToggleFlagUpdates() {
        Cell cell = model.getGrid()[0][0];
        model.toggleFlag(cell);
        assertTrue(cell.isFlagged, "Cell needs to be flagged");
        assertEquals(1, model.getFlagsPlaced(), "Model should show 1 placed flag");
    }


    @Test
    void testHandleAlgorithmChange() throws Exception {
        ComboBox<String> algoBox = (ComboBox<String>) getPrivateField(controller, "algorithmComboBox");
        algoBox.setValue("DFS");

        java.lang.reflect.Method method = controller.getClass().getDeclaredMethod("handleAlgorithmChange");
        method.setAccessible(true);
        method.invoke(controller);

        MinesweeperModel ctrlModel = (MinesweeperModel) getPrivateField(controller, "model");
    }

    @Test
    void testTimerStartsOnFirstClick() throws Exception {
        Cell cell = model.getGrid()[0][0];
        assertFalse(model.isGameStarted());

        java.lang.reflect.Method clickMethod = controller.getClass().getDeclaredMethod("handleLeftClick", Cell.class);
        clickMethod.setAccessible(true);
        clickMethod.invoke(controller, cell);

        assertTrue(model.isGameStarted(), "Game should start after first click");
    }

    @Test
    void testRevealCellEdgeCases() {
        Cell cell = model.getGrid()[0][0];
        cell.toggleFlag();


        boolean result = model.revealCell(cell);
        assertFalse(result, "Flagged cell should be rejected by revealCell");
        assertFalse(cell.isOpen);
    }



    private int countBombsInGrid() {
        int count = 0;
        for (Cell[] row : model.getGrid()) {
            for (Cell c : row) if (c.hasBomb) count++;
        }
        return count;
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
}