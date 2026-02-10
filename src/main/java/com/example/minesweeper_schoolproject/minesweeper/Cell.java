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
        setPrefSize(30, 30);
        setStyle("-fx-background-radius: 0; -fx-border-color: #777;");
    }

    public void toggleFlag() {
        if (isOpen) return;
        isFlagged = !isFlagged;
        if (isFlagged) {
            setText("🚩");
            setStyle("-fx-background-color: black; -fx-background-radius: 0; -fx-text-fill: white;");
        } else {
            setText("");
            setStyle("-fx-background-radius: 0; -fx-border-color: #777;");
        }
    }

    public void reveal() {
        if (isOpen || isFlagged) return;
        isOpen = true;
        setDisable(true);
        if (hasBomb) {
            setText("💣");
            setStyle("-fx-background-color: red; -fx-opacity: 1; -fx-text-fill: black;");
        } else {
            setStyle("-fx-background-color: #eeeeee; -fx-opacity: 1; -fx-border-color: #ccc;");
            if (neighboringBombs > 0) {
                setText(String.valueOf(neighboringBombs));
                String color = switch (neighboringBombs) {
                    case 1 -> "blue";
                    case 2 -> "green";
                    case 3 -> "red";
                    case 4 -> "darkblue";
                    default -> "darkred";
                };
                setStyle(getStyle() + "-fx-text-fill: " + color + "; -fx-font-weight: bold;");
            }
        }
    }
}