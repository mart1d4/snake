package com.upec;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.List;

public class Game {
    private static final int NUM_INITIAL_OBSTACLES = 5;
    private static final int NUM_INITIAL_FOODS = 3;
    private static final int NUM_GROWTH_THRESHOLD = 3;

    private Board board;
    private int width;
    private int height;
    private int cellSize;
    private int round;
    private boolean gameOver;
    private int winner;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private int moves;
    private Controller controller;
    private HashMap<String, Image> assets;

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOtherPlayer() {
        if (currentPlayer == player1) {
            return player2;
        } else {
            return player1;
        }
    }

    public void moveCurrentPlayer(String direction) {
        if (round % NUM_GROWTH_THRESHOLD == 0) {
            currentPlayer.moveSnake(direction, true);
        } else {
            currentPlayer.moveSnake(direction, false);
        }

        Snake snake = currentPlayer.getSnake();
        Snake otherSnake = getOtherPlayer().getSnake();

        List<Point> foods = board.getFoods();
        if (snake.checkCollisionWithFood(foods)) {
            if (snake.getBody().size() > 2) {
                snake.shrink();
                Point food = snake.getFoodCollided(foods);
                board.removeFood(food);
                randomlyAddCells(1, false);
            }
        }

        if (snake.isSnakeDead(board, otherSnake)) {
            gameOver = true;
            winner = currentPlayer == player1 ? 2 : 1;

            App.stopGame();
            controller.drawBoard(this, width, height, cellSize, assets);
            controller.stopGame(winner == 1 ? player1 : player2);

            return;
        }

        moves++;

        if (moves == 2) {
            moves = 0;
            round++;
            controller.showRound(round);
        }

        switchPlayer();
        controller.drawBoard(this, width, height, cellSize, assets);

        // We'll later implement the settings but let's say the user wants to play
        // against the computer
        // we'll make the computer move after the player has moved
        if (currentPlayer == player2) {
            moveCurrentPlayer(player2.getSnake().playAdvancedMoveAI(board, player1.getSnake()));
        }
    }

    private void switchPlayer() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
            controller.setCurrentPlayer(player2);
        } else {
            currentPlayer = player1;
            controller.setCurrentPlayer(player1);
        }
    }

    public Game(int width, int height, int cellSize, Controller controller, HashMap<String, Image> assets) {
        this.board = new Board(width, height);
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        this.controller = controller;
        this.assets = assets;
        this.round = 1;
        this.moves = 0;
        this.gameOver = false;
        this.player1 = new Player("Player 1", new Snake("red"));
        this.player2 = new Player("Player 2", new Snake("blue"));
        this.currentPlayer = player1;

        controller.setCurrentPlayer(player1);
    }

    public void initialize() {
        board.clear();
        round = 1;
        moves = 0;
        gameOver = false;
        winner = 0;
        currentPlayer = player1;
        initializeSnakes();
        randomlyAddCells(NUM_INITIAL_OBSTACLES, true);
        randomlyAddCells(NUM_INITIAL_FOODS, false);

        controller.showRound(round);
        controller.setCurrentPlayer(currentPlayer);
        controller.drawBoard(this, width, height, cellSize, assets);
    }

    private void initializeSnakes() {
        Snake snake1 = player1.getSnake();
        snake1.getBody().clear();
        snake1.addSegment(new Point(3, 1));
        snake1.addSegment(new Point(2, 1));
        snake1.addSegment(new Point(1, 1));
        board.addSnake(snake1);

        Snake snake2 = player2.getSnake();
        snake2.getBody().clear();
        snake2.addSegment(new Point(width - 4, height - 2));
        snake2.addSegment(new Point(width - 3, height - 2));
        snake2.addSegment(new Point(width - 2, height - 2));
        board.addSnake(snake2);
    }

    private void randomlyAddCells(int num, boolean isObstacle) {
        for (int i = 0; i < num; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);

            while (board.isCellOccupied(x, y)) {
                x = (int) (Math.random() * width);
                y = (int) (Math.random() * height);
            }

            if (isObstacle) {
                board.addObstacle(new Point(x, y));
            } else {
                board.addFood(new Point(x, y));
            }
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getWinner() {
        return winner;
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayer(int playerNumber) {
        if (playerNumber == 1) {
            return player1;
        } else {
            return player2;
        }
    }
}
