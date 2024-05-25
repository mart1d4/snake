package com.upec;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Arrays;

public class Game {
    private static final int NUM_INITIAL_OBSTACLES = 5;
    private static final int NUM_INITIAL_FOODS = 3;
    private static final int NUM_GROWTH_THRESHOLD = 4;
    private static final boolean USE_AI_OPPONENT = true;
    private static final boolean TRY_EATING_FOOD = true;

    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private int round;
    private int moves;

    private HashMap<String, Image> assets;
    private Controller controller;

    public Game(int width, int height, int cellSize, Controller controller, HashMap<String, Image> assets) {
        this.board = new Board(width, height, cellSize);
        this.controller = controller;
        this.assets = assets;
        this.round = 1;
        this.moves = 0;
        this.player1 = new Player("Player 1", new Snake("red"));
        this.player2 = new Player("Player 2", new Snake("blue"));
        this.currentPlayer = player1;

        controller.setCurrentPlayer(player1);
        controller.updateGameStats(player1, player2);
    }

    public void initialize() {
        board.clear();
        round = 1;
        moves = 0;
        currentPlayer = player1;

        initializeSnakes();
        board.randomlyAddCells(NUM_INITIAL_OBSTACLES, true);
        board.randomlyAddCells(NUM_INITIAL_FOODS, false);

        controller.showRound(round);
        controller.setCurrentPlayer(currentPlayer);
        controller.drawBoard(this, assets);
    }

    private void initializeSnakes() {
        int w = board.getWidth();
        int h = board.getHeight();

        Snake snake1 = player1.getSnake();
        snake1.getBody().clear();
        snake1.addBody(Arrays.asList(new Point(3, 1), new Point(2, 1), new Point(1, 1)));
        board.addSnake(snake1);

        Snake snake2 = player2.getSnake();
        snake2.getBody().clear();
        snake2.addBody(Arrays.asList(new Point(w - 4, h - 2), new Point(w - 3, h - 2), new Point(w - 2, h - 2)));
        board.addSnake(snake2);
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOtherPlayer() {
        return currentPlayer == player1 ? player2 : player1;
    }

    public Player getPlayer(int playerNumber) {
        return playerNumber == 1 ? player1 : player2;
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

    // This gets called when the player presses the arrow keys,
    // the WASD keys, or if the AI makes a move
    public void moveCurrentPlayer(String direction) {
        // If growth threshold is reached, grow the snake when it moves
        currentPlayer.moveSnake(direction, round % NUM_GROWTH_THRESHOLD == 0);

        Snake snake = getCurrentPlayer().getSnake();
        Snake otherSnake = getOtherPlayer().getSnake();

        if (snake.checkCollision(board.getFoods())) {
            // Currently, shrinking the snake if it's not longer than 2 cells
            // would throw an exception, so we're not doing that
            // (This has to do with indexing in the snake's body list)
            if (snake.getBody().size() > 2) {
                foodAte(snake);
            }
        }

        if (snake.isSnakeDead(board, otherSnake)) {
            endCurrentGame();
            return;
        }

        moves++;
        if (moves == 2) {
            moves = 0;
            round++;
            controller.showRound(round);
        }

        switchPlayer();
        controller.drawBoard(this, assets);

        if (currentPlayer == player2 && USE_AI_OPPONENT) {
            moveCurrentPlayer(player2.getSnake().playMoveAI(board, player1.getSnake(), TRY_EATING_FOOD));
        }
    }

    // This gets called when the snake collides with food
    // It shrinks the snake, removes the food, and adds a new food at a random
    // location on the board
    public void foodAte(Snake snake) {
        snake.shrink();
        Point food = snake.getFoodCollided(board.getFoods());
        board.removeFood(food);
        board.randomlyAddCells(1, false);
    }

    public void endCurrentGame() {
        Player winner = getOtherPlayer();
        winner.addWin();

        App.stopGame();
        controller.drawBoard(this, assets);
        controller.stopGame(winner);
        controller.updateGameStats(player1, player2);
    }
}
