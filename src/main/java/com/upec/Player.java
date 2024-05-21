package com.upec;

public class Player {
    private String name;
    private int score;
    private Snake snake;

    public Player(String name, Snake snake) {
        this.name = name;
        this.score = 0;
        this.snake = snake;
    }

    public static Point getPointFromDirection(String direction) {
        switch (direction) {
            case "up":
                return new Point(-1, 0);
            case "down":
                return new Point(1, 0);
            case "left":
                return new Point(0, -1);
            case "right":
                return new Point(0, 1);
            default:
                return new Point(0, 0);
        }
    }

    public void moveSnake(String direction, boolean grow) {
        Point dir = getPointFromDirection(direction);
        snake.move(dir.x, dir.y, grow);
    }

    public String getName() {
        return name + " (" + snake.getColor() + ")";
    }

    public Snake getSnake() {
        return snake;
    }

    public int getScore() {
        return score;
    }

    public void addVictory() {
        score++;
    }
}
