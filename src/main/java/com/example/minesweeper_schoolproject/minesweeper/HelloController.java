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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class HelloController {
    @FXML private Button resetButton;
    @FXML private GridPane gameGrid;
    @FXML private Label timerLabel;
    @FXML private Label flagLabel;
    @FXML private ComboBox<String> myComboBox;
    @FXML private ComboBox<String> algorithmComboBox;

    private int SIZE;
    private int BOMB_COUNT;
    private Cell[][] grid;
    private int secondsElapsed = 0;
    private int flagsPlaced = 0;
    private Timeline timeline;
    private boolean gameStarted = false;
    private Algorithms currentAlgorithm = Algorithms.FYS;

    private static final SecureRandom secureRandom = new SecureRandom();

    @FXML
    public void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            timerLabel.setText("Time: " + secondsElapsed);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        myComboBox.setOnAction(e -> handleLevelChange());
        algorithmComboBox.setOnAction(e -> handleAlgorithmChange());

        LevelSelect(Levels.ONE);
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
                case "EASY" -> LevelSelect(Levels.ONE);
                case "MEDIUM" -> LevelSelect(Levels.TWO);
                case "HARD" -> LevelSelect(Levels.THREE);
            }
        }
    }

    private void handleAlgorithmChange() {
        String selected = algorithmComboBox.getValue();
        if (selected != null) {
            try {
                currentAlgorithm = Algorithms.valueOf(selected);
            } catch (Exception e) {
                currentAlgorithm = Algorithms.FYS;
            }
            startNewGame();
        }
    }

    private void placeBombs(Algorithms algorithm, List<Cell> forbidden) {
        boolean[][] forb = new boolean[SIZE][SIZE];
        for (Cell c : forbidden) {
            forb[c.x][c.y] = true;
        }

        int placed = 0;
        boolean[][] visited = new boolean[SIZE][SIZE];
        Stack<Cell> stack = new Stack<>();

        if (algorithm == Algorithms.DFS || algorithm.name().equals("DFS_HARD")) {
            int startX, startY;
            do {
                startX = secureRandom.nextInt(SIZE);
                startY = secureRandom.nextInt(SIZE);
            } while (forb[startX][startY]);
            stack.push(grid[startX][startY]);
            visited[startX][startY] = true;
        }

        switch (algorithm) {
            case DFS:
                while (!stack.isEmpty() && placed < BOMB_COUNT) {
                    Cell current = stack.pop();
                    if (!forb[current.x][current.y] && !current.hasBomb) {
                        if (secureRandom.nextDouble() < 0.3) {
                            current.hasBomb = true;
                            placed++;
                        }
                    }
                    List<Cell> neighbors = getNeighbors(current);
                    java.util.Collections.shuffle(neighbors, secureRandom);
                    for (Cell n : neighbors) {
                        if (!visited[n.x][n.y]) {
                            visited[n.x][n.y] = true;
                            stack.push(n);
                        }
                    }
                }
                break;
            case DFS_HARD:
                while (!stack.isEmpty() && placed < BOMB_COUNT) {
                    Cell current = stack.pop();
                    if (!forb[current.x][current.y] && !current.hasBomb) {
                        boolean neighborHasBomb = getNeighbors(current).stream().anyMatch(n -> n.hasBomb);
                        if (!neighborHasBomb) {
                            current.hasBomb = true;
                            placed++;
                        }
                    }
                    List<Cell> neighbors = getNeighbors(current);
                    java.util.Collections.shuffle(neighbors, secureRandom);
                    for (Cell n : neighbors) {
                        if (!visited[n.x][n.y]) {
                            visited[n.x][n.y] = true;
                            stack.push(n);
                        }
                    }
                }
                break;
            case FYS:
                List<int[]> positions = new ArrayList<>();
                for (int x = 0; x < SIZE; x++) {
                    for (int y = 0; y < SIZE; y++) positions.add(new int[]{x, y});
                }
                java.util.Collections.shuffle(positions, secureRandom);
                for (int[] pos : positions) {
                    if (!forb[pos[0]][pos[1]] && placed < BOMB_COUNT) {
                        grid[pos[0]][pos[1]].hasBomb = true;
                        placed++;
                    }
                }
                break;
        }
        while (placed < BOMB_COUNT) {
            int rx = secureRandom.nextInt(SIZE);
            int ry = secureRandom.nextInt(SIZE);
            if (!forb[rx][ry] && !grid[rx][ry].hasBomb) {
                grid[rx][ry].hasBomb = true;
                placed++;
            }
        }
    }

    public void LevelSelect(Levels level) {
        switch (level) {
            case ONE -> { SIZE = 9; BOMB_COUNT = 10; }
            case TWO -> { SIZE = 16; BOMB_COUNT = 26; }
            case THREE -> { SIZE = 20; BOMB_COUNT = 40; }
        }
        grid = new Cell[SIZE][SIZE];
        startNewGame();
    }

    @FXML
    private void startNewGame() {
        timeline.stop();
        secondsElapsed = 0;
        flagsPlaced = 0;
        gameStarted = false;
        timerLabel.setText("Time: 0");
        flagLabel.setText("Flags: " + BOMB_COUNT);
        resetButton.setText("🙂");

        gameGrid.getChildren().clear();
        gameGrid.getColumnConstraints().clear();
        gameGrid.getRowConstraints().clear();

        for (int i = 0; i < SIZE; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / SIZE);
            gameGrid.getColumnConstraints().add(col);
            RowConstraints row = new RowConstraints();
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
        if (!gameStarted) {
            List<Cell> forbidden = new ArrayList<>();
            forbidden.add(cell);
            forbidden.addAll(getNeighbors(cell));
            placeBombs(currentAlgorithm, forbidden);
            setupNumbers();
            timeline.play();
            gameStarted = true;
        }
        cell.reveal();
        if (cell.hasBomb) {
            resetButton.setText("\uD83D\uDE1E");
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
        flagLabel.setText("Flags: " + (BOMB_COUNT - flagsPlaced));
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
                if (!c.hasBomb && !c.isOpen) { won = false; break; }
            }
        }
        if (won) {
            resetButton.setText("😎");
            showEndGameDialog("WIN!");
        }
    }

    private void showEndGameDialog(String msg) {
        timeline.stop();
        for (Cell[] row : grid) {
            for (Cell c : row) if (c.hasBomb) c.reveal();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg + "\nTime: " + secondsElapsed + "s\nNew Game?");
        ButtonType restart = new ButtonType("Restart");
        alert.getButtonTypes().setAll(restart, ButtonType.CLOSE);
        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == restart) startNewGame();
    }


}