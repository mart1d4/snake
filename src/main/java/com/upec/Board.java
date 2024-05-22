package com.upec;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private int width;
    private int height;
    private int cellSize;
    private int[][] cells;
    private List<Snake> snakes;
    private List<Point> obstacles;
    private List<Point> foods;

    public Board(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        this.cells = new int[height][width];
        this.snakes = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.foods = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void clear() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                cells[i][j] = 0;
            }
        }

        snakes.clear();
        obstacles.clear();
        foods.clear();
    }

    public void addSnake(Snake snake) {
        snakes.add(snake);
    }

    public void addObstacle(Point obstacle) {
        obstacles.add(obstacle);
    }

    public void addFood(Point food) {
        foods.add(food);
    }

    public List<Point> getObstacles() {
        return obstacles;
    }

    public List<Point> getFoods() {
        return foods;
    }

    public void removeFood(Point food) {
        foods.remove(food);
    }

    public void setCell(int[][] board, int x, int y, int value) {
        // Set the value of a cell at position (x, y)
        if (x >= 0 && x < height && y >= 0 && y < width) {
            board[x][y] = value;
        }
    }

    public boolean isFood(int x, int y) {
        return getBoard()[x][y] == 6;
    }

    public boolean isObstacle(int x, int y) {
        return getBoard()[x][y] == 5;
    }

    // Merge all the snakes, obstacles, and foods into a single board
    // So we can draw them all at once
    public int[][] getBoard() {
        int[][] mergedBoard = new int[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                mergedBoard[i][j] = 0;
            }
        }

        for (Snake snake : snakes) {
            List<Point> body = snake.getBody();
            String color = snake.getColor();
            boolean isRed = color.equals("red");

            for (Point p : body) {
                if (p.equals(body.get(0))) {
                    setCell(mergedBoard, p.x, p.y, isRed ? 1 : 3);
                } else {
                    setCell(mergedBoard, p.x, p.y, isRed ? 2 : 4);
                }
            }
        }

        for (Point p : obstacles) {
            setCell(mergedBoard, p.x, p.y, 5);
        }

        for (Point p : foods) {
            setCell(mergedBoard, p.x, p.y, 6);
        }

        return mergedBoard;
    }

    private boolean isPointOutOfBounds(Point p) {
        return p.x < 0 || p.x >= height || p.y < 0 || p.y >= width;
    }

    // Check if a cell is occupied by anything
    public boolean isCellOccupied(int x, int y) {
        if (this.isPointOutOfBounds(new Point(x, y))) { // Don't go out of bounds
            return true;
        }

        return getBoard()[x][y] != 0;
    }

    // Check if a cell is blocked (obstacle, snake, wall)
    public boolean isCellBlocked(int x, int y) {
        if (this.isPointOutOfBounds(new Point(x, y))) { // Don't go out of bounds
            return true;
        }

        return getBoard()[x][y] != 0 && getBoard()[x][y] != 6;
    }
}
