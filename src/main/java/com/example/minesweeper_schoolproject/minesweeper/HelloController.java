package com.example.minesweeper_schoolproject.minesweeper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;

public class HelloController {
    @FXML private Button resetButton;
    @FXML private GridPane gameGrid;
    @FXML private Label timerLabel;
    @FXML private Label flagLabel;
    @FXML private ComboBox<String> myComboBox;
    @FXML private ComboBox<String> algorithmComboBox;

    private MinesweeperModel model;
    private int secondsElapsed = 0;
    private Timeline timeline;

    @FXML
    public void initialize() {
        model = new MinesweeperModel();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            timerLabel.setText("Time: " + secondsElapsed);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        myComboBox.setOnAction(e -> handleLevelChange());
        algorithmComboBox.setOnAction(e -> handleAlgorithmChange());

        model.setLevel(Levels.ONE);
        startNewGame();
    }

    @FXML
    public void handleReset() {
        model.initializeGrid();
        startNewGame();
    }

    @FXML
    public void handleEmojiPressed() {
        if (resetButton != null) {
            resetButton.setText("😮");
        }
    }

    @FXML
    public void handleEmojiReleased() {
        if (resetButton != null) {
            resetButton.setText("🙂");
        }
    }

    private void handleLevelChange() {
        String selected = myComboBox.getValue();
        if (selected != null) {
            switch (selected) {
                case "EASY" -> model.setLevel(Levels.ONE);
                case "MEDIUM" -> model.setLevel(Levels.TWO);
                case "HARD" -> model.setLevel(Levels.THREE);
            }
            startNewGame();
        }
    }

    private void handleAlgorithmChange() {
        String selected = algorithmComboBox.getValue();
        if (selected != null) {
            try {
                model.setAlgorithm(Algorithms.valueOf(selected));
            } catch (Exception e) {
                model.setAlgorithm(Algorithms.FYS);
            }
            model.initializeGrid();
            startNewGame();
        }
    }





    @FXML
    private void startNewGame() {
        timeline.stop();
        secondsElapsed = 0;
        timerLabel.setText("Time: 0");
        flagLabel.setText("Flags: " + model.getBombCount());
        resetButton.setText("🙂");

        gameGrid.getChildren().clear();
        gameGrid.getColumnConstraints().clear();
        gameGrid.getRowConstraints().clear();

        int SIZE = model.getSize();
        for (int i = 0; i < SIZE; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / SIZE);
            gameGrid.getColumnConstraints().add(col);
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / SIZE);
            gameGrid.getRowConstraints().add(row);
        }

        Cell[][] grid = model.getGrid();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Cell cell = grid[x][y];

                cell.getStyleClass().add("mine-cell");

                GridPane.setHgrow(cell, Priority.ALWAYS);
                GridPane.setVgrow(cell, Priority.ALWAYS);
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                cell.styleProperty().bind(javafx.beans.binding.Bindings.concat(
                        "-fx-font-size: ", cell.widthProperty().multiply(0.35).asString(), "px;"
                ));
                cell.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) handleLeftClick(cell);
                    else if (e.getButton() == MouseButton.SECONDARY) handleRightClick(cell);
                });
                gameGrid.add(cell, x, y);
            }
        }
    }

    private void handleLeftClick(Cell cell) {
        if (cell.isOpen || cell.isFlagged) return;
        if (!model.isGameStarted()) {
            List<Cell> forbidden = new ArrayList<>();
            forbidden.add(cell);
            forbidden.addAll(model.getNeighbors(cell));
            model.startGame(forbidden);
            timeline.play();
        }
        boolean isBomb = model.revealCell(cell);
        if (isBomb) {
            resetButton.setText("\uD83D\uDE1E");
            showEndGameDialog("BOMBOOCLAAAT!");
        } else {
            model.revealEmptyArea(cell);
            checkWin();
        }
    }

    private void handleRightClick(Cell cell) {
        if (cell.isOpen) return;
        model.toggleFlag(cell);
        flagLabel.setText("Flags: " + (model.getBombCount() - model.getFlagsPlaced()));
    }



    private void checkWin() {
        if (model.checkWin()) {
            resetButton.setText("😎");
            showEndGameDialog("WIN!");
        }
    }

    private void showEndGameDialog(String msg) {
        timeline.stop();
        model.revealAllBombs();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg + "\nTime: " + secondsElapsed + "s\nNew Game?");
        ButtonType restart = new ButtonType("Restart");
        alert.getButtonTypes().setAll(restart, ButtonType.CLOSE);
        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent()) {
            if (res.get() == restart) {
                handleReset();
            } else {
                Platform.exit();
            }
        }
    }


}