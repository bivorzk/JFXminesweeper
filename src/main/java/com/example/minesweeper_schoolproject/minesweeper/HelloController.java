package com.example.minesweeper_schoolproject.minesweeper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class HelloController  {

    @FXML private GridPane gameGrid;
    @FXML private Label timerLabel;
    @FXML private Label flagLabel;

    private  int SIZE = 20;
    private  int BOMB_COUNT = 40;
    private Cell[][] grid = new Cell[SIZE][SIZE];
    private int secondsElapsed = 0;
    private int flagsPlaced = 0;
    private Timeline timeline;
    private boolean gameStarted = false;

    @FXML
    public void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            timerLabel.setText("Idő: " + secondsElapsed);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        startNewGame();
    }

    private void startNewGame() {
        timeline.stop();
        secondsElapsed = 0;
        flagsPlaced = 0;
        gameStarted = false;
        timerLabel.setText("Idő: 0");
        flagLabel.setText("Zászló: " + BOMB_COUNT);
        gameGrid.getChildren().clear();

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                grid[x][y] = new Cell(x, y, false);
            }
        }

        int placed = 0;
        while (placed < BOMB_COUNT) {
            int rx = (int)(Math.random() * SIZE);
            int ry = (int)(Math.random() * SIZE);
            if (!grid[rx][ry].hasBomb) {
                grid[rx][ry].hasBomb = true;
                placed++;
            }
        }

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Cell cell = grid[x][y];
                cell.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) handleLeftClick(cell);
                    else if (e.getButton() == MouseButton.SECONDARY) handleRightClick(cell);
                });
                gameGrid.add(cell, x, y);
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
            showEndGameDialog("Vége a játéknak!");
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
        flagLabel.setText("Zászló: " + (BOMB_COUNT - flagsPlaced));
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
            int nx = cell.x + points[i], ny = cell.y + points[i+1];
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
        if (won) showEndGameDialog("Győzelem!");
    }

    private void showEndGameDialog(String msg) {
        timeline.stop();
        for (Cell[] row : grid) {
            for (Cell c : row) if (c.hasBomb) c.reveal();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg + "\nIdő: " + secondsElapsed + "s\nÚj játék?");
        ButtonType restart = new ButtonType("Újrakezdés");
        alert.getButtonTypes().setAll(restart, ButtonType.CLOSE);
        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == restart) startNewGame();
    }

    public int LevelSelect(Levels object) {
        switch (object) {
            case ONE:
                 SIZE = 9 ;
                 BOMB_COUNT = 10;
            case TWO:
                 SIZE = 16;
                BOMB_COUNT = 26;
            case THREE:
                 SIZE = 20;
                BOMB_COUNT = 40;
                default:
                    return SIZE = 9;
        }

    }


}