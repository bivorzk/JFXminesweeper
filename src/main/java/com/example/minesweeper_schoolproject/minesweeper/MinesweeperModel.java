package com.example.minesweeper_schoolproject.minesweeper;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MinesweeperModel {
    private int SIZE;
    private int BOMB_COUNT;
    private Cell[][] grid;
    private boolean gameStarted = false;
    private Algorithms currentAlgorithm = Algorithms.FYS;
    private int flagsPlaced = 0;

    private static final SecureRandom secureRandom = new SecureRandom();

    public MinesweeperModel() {
        // Default initialization
    }

    public void setLevel(Levels level) {
        switch (level) {
            case ONE -> { SIZE = 9; BOMB_COUNT = 10; }
            case TWO -> { SIZE = 16; BOMB_COUNT = 26; }
            case THREE -> { SIZE = 20; BOMB_COUNT = 40; }
        }
        initializeGrid();
    }

    public void setAlgorithm(Algorithms algorithm) {
        this.currentAlgorithm = algorithm;
    }

    public void initializeGrid() {
        grid = new Cell[SIZE][SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                grid[x][y] = new Cell(x, y, false);
            }
        }
        gameStarted = false;
        flagsPlaced = 0;
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public int getSize() {
        return SIZE;
    }

    public int getBombCount() {
        return BOMB_COUNT;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame(List<Cell> forbidden) {
        placeBombs(currentAlgorithm, forbidden);
        setupNumbers();
        gameStarted = true;
    }

    public void placeBombs(Algorithms algorithm, List<Cell> forbidden) {
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

    private void setupNumbers() {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (!grid[x][y].hasBomb) {
                    grid[x][y].neighboringBombs = (int) getNeighbors(grid[x][y]).stream().filter(c -> c.hasBomb).count();
                }
            }
        }
    }

    public List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int[] points = {-1, -1, -1, 0, -1, 1, 0, -1, 0, 1, 1, -1, 1, 0, 1, 1};
        for (int i = 0; i < points.length; i += 2) {
            int nx = cell.x + points[i], ny = cell.y + points[i + 1];
            if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE) neighbors.add(grid[nx][ny]);
        }
        return neighbors;
    }

    public boolean revealCell(Cell cell) {
        if (cell.isOpen || cell.isFlagged) return false;
        cell.reveal();
        return cell.hasBomb;
    }

    public void revealEmptyArea(Cell cell) {
        if (cell.neighboringBombs == 0) {
            getNeighbors(cell).forEach(this::revealCell);
        }
    }


    public boolean toggleFlag(Cell cell) {
        if (cell.isOpen) return false;
        if (!cell.isFlagged && flagsPlaced < BOMB_COUNT) {
            cell.toggleFlag();
            flagsPlaced++;
            return true;
        } else if (cell.isFlagged) {
            cell.toggleFlag();
            flagsPlaced--;
            return true;
        }
        return false;
    }

    public int getFlagsPlaced() {
        return flagsPlaced;
    }

    public boolean checkWin() {
        for (Cell[] row : grid) {
            for (Cell c : row) {
                if (!c.hasBomb && !c.isOpen) return false;
            }
        }
        return true;
    }

    public void revealAllBombs() {
        for (Cell[] row : grid) {
            for (Cell c : row) if (c.hasBomb) c.reveal();
        }
    }
}