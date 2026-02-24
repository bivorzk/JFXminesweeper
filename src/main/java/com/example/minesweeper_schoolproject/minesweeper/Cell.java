package com.example.minesweeper_schoolproject.minesweeper;

import javafx.scene.control.Button;

public class Cell extends Button {
    int x, y;
    boolean hasBomb;
    boolean isOpen = false;
    boolean isFlagged = false;
    int neighboringBombs = 0;

    public Cell(int x, int y, boolean hasBomb) {
        this.x = x;
        this.y = y;
        this.hasBomb = hasBomb;
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setMinSize(10, 10);
        this.getStyleClass().add("button");
        this.getStyleClass().add("mine-cell");
    }

    public void toggleFlag() {
        if (isOpen) return;
        isFlagged = !isFlagged;
        if (isFlagged) {
            setText("🚩");

            if (!getStyleClass().contains("flagged")) {
                getStyleClass().add("flagged");
            }
        } else {
            setText("");
            getStyleClass().remove("flagged");
        }
    }

    public void reveal() {
        if (isOpen || isFlagged) return;
        isOpen = true;

        this.getStyleClass().add("revealed");

        if (hasBomb) {
            this.getStyleClass().add("bomb");
            setText("💣");
        } else if (neighboringBombs > 0) {
            setText(String.valueOf(neighboringBombs));
            this.getStyleClass().add("count-" + neighboringBombs);
        }
    }
}