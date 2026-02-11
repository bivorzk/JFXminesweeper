package com.example.minesweeper_schoolproject.minesweeper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HelloController {

    @FXML private GridPane gameGrid;
    @FXML private Label timerLabel;
    @FXML private Label flagLabel;
    @FXML private ComboBox<String> myComboBox;

    private int SIZE;
    private int BOMB_COUNT;
    private Cell[][] grid;
    private int secondsElapsed = 0;
    private int flagsPlaced = 0;
    private Timeline timeline;
    private boolean gameStarted = false;

    private static final SecureRandom secureRandom = new SecureRandom();

    @FXML
    public void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            timerLabel.setText("Time: " + secondsElapsed);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        myComboBox.setOnAction(e -> handleLevelChange());
        myComboBox.getSelectionModel().selectFirst();
        handleLevelChange();
    }

    private void handleLevelChange() {
        String selected = myComboBox.getValue();
        if (selected != null) {
            try {
                LevelSelect(Levels.valueOf(selected));
            } catch (IllegalArgumentException e) {
                switch (selected) {
                    case "EASY" -> LevelSelect(Levels.ONE);
                    case "MEDIUM" -> LevelSelect(Levels.TWO);
                    case "HARD" -> LevelSelect(Levels.THREE);
                    default -> LevelSelect(Levels.ONE);
                }
            }
        }
    }

    public void LevelSelect(Levels level) {
        switch (level) {
            case ONE:
                SIZE = 9;
                BOMB_COUNT = 10;
                break;
            case TWO:
                SIZE = 16;
                BOMB_COUNT = 26;
                break;
            case THREE:
                SIZE = 20;
                BOMB_COUNT = 40;
                break;

        }
        grid = new Cell[SIZE][SIZE];
        startNewGame();
    }

    private void startNewGame() {
        timeline.stop();
        secondsElapsed = 0;
        flagsPlaced = 0;
        gameStarted = false;
        timerLabel.setText("Time: 0");
        flagLabel.setText("Flags: " + BOMB_COUNT);

        gameGrid.getChildren().clear();
        gameGrid.getColumnConstraints().clear();
        gameGrid.getRowConstraints().clear();

        for (int i = 0; i < SIZE; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            col.setPercentWidth(100.0 / SIZE);
            gameGrid.getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            row.setPercentHeight(100.0 / SIZE);
            gameGrid.getRowConstraints().add(row);
        }

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                grid[x][y] = new Cell(x, y, false);
                Cell cell = grid[x][y];

                cell.getStyleClass().add("mine-cell");

                GridPane.setHgrow(cell, Priority.ALWAYS);
                GridPane.setVgrow(cell, Priority.ALWAYS);
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                cell.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) handleLeftClick(cell);
                    else if (e.getButton() == MouseButton.SECONDARY) handleRightClick(cell);
                });
                gameGrid.add(cell, x, y);
            }
        }

        int placed = 0;
        while (placed < BOMB_COUNT) {
            int rx = (int) (Math.abs(nextSeed()) % SIZE);
            int ry = (int) (Math.abs(nextSeed()) % SIZE);
            if (!grid[rx][ry].hasBomb) {
                grid[rx][ry].hasBomb = true;
                placed++;
            }
        }
        setupNumbers();
    }

    private void handleLeftClick(Cell cell) {
        if (cell.isOpen || cell.isFlagged) return;
        if (!gameStarted) {
            timeline.play();
            gameStarted = true;
        }
        cell.reveal();
        if (cell.hasBomb) {
            showEndGameDialog("BOMBOOCLAAAT!");
        } else if (cell.neighboringBombs == 0) {
            getNeighbors(cell).forEach(this::handleLeftClick);
        }
        checkWin();
    }

    private void handleRightClick(Cell cell) {
        if (cell.isOpen) return;
        if (!cell.isFlagged && flagsPlaced < BOMB_COUNT) {
            cell.toggleFlag();
            flagsPlaced++;
        } else if (cell.isFlagged) {
            cell.toggleFlag();
            flagsPlaced--;
        }
        flagLabel.setText("Flag: " + (BOMB_COUNT - flagsPlaced));
    }

    private void setupNumbers() {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (!grid[x][y].hasBomb) {
                    grid[x][y].neighboringBombs = (int) getNeighbors(grid[x][y]).stream().filter(c -> c.hasBomb).count();
                }
            }
        }
    }

    private List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int[] points = {-1, -1, -1, 0, -1, 1, 0, -1, 0, 1, 1, -1, 1, 0, 1, 1};
        for (int i = 0; i < points.length; i += 2) {
            int nx = cell.x + points[i], ny = cell.y + points[i + 1];
            if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE) neighbors.add(grid[nx][ny]);
        }
        return neighbors;
    }

    private void checkWin() {
        boolean won = true;
        for (Cell[] row : grid) {
            for (Cell c : row) {
                if (!c.hasBomb && !c.isOpen) {
                    won = false;
                    break;
                }
            }
        }
        if (won) showEndGameDialog("WIN!");
    }

    private void showEndGameDialog(String msg) {
        timeline.stop();
        for (Cell[] row : grid) {
            for (Cell c : row) if (c.hasBomb) c.reveal();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg + "\nTime: " + secondsElapsed + "s\nNew Game?");
        ButtonType restart = new ButtonType("Restart");
        alert.getButtonTypes().setAll(restart, ButtonType.CLOSE);
        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == restart) startNewGame();
    }

    private static long nextSeed() {
        return secureRandom.nextLong();
    }
}