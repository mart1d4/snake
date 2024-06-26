package com.upec;

import java.util.HashMap;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.fxml.FXML;

public class Controller {

    @FXML
    private BorderPane gameContainer;
    @FXML
    private BorderPane startContainer;
    @FXML
    private Canvas canvas;
    @FXML
    private HBox header;
    @FXML
    private Label roundLabel;
    @FXML
    private Label playerLabel;
    @FXML
    private Label winLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button playAgainButton;
    @FXML
    private Label gameAmount;
    @FXML
    private Label playerWin1;
    @FXML
    private Label playerWin2;

    private Player currentPlayer;

    // This runs when the elements are mounted
    // and sets up the event listeners
    public void initialize() {
        startButton.setOnAction((event) -> {
            startGame();
        });

        playAgainButton.setOnAction((event) -> {
            startGame();
        });

        stopButton.setOnAction((event) -> {
            App.closeApp();
        });
    }

    public void startGame() {
        // Just to hide the win message
        showGameOver("");
        // We don't want to show the play again button
        // when the game is running
        hidePlayAgain();
        showGame();
        gameContainer.requestFocus();
        App.startGame();
    }

    // Shows the winner and displays the play again button
    public void stopGame(Player winner) {
        showGameOver("Game Over! " + winner.getName() + " wins!");
        showPlayAgain();
    }

    public void drawBoard(Game game, HashMap<String, Image> assets) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Board board = game.getBoard();
        int width = board.getWidth();
        int height = board.getHeight();
        int cellSize = board.getCellSize();

        gc.clearRect(0, 0, width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x % 2 == 0) {
                    if (y % 2 == 0) {
                        gc.setFill(Color.web("#aad751"));
                    } else {
                        gc.setFill(Color.web("#a2d149"));
                    }
                } else {
                    if (y % 2 == 0) {
                        gc.setFill(Color.web("#a2d149"));
                    } else {
                        gc.setFill(Color.web("#aad751"));
                    }
                }

                gc.fillRect(y * cellSize, x * cellSize, cellSize, cellSize);

                if (board.isFood(x, y)) {
                    gc.drawImage(assets.get("apple"), y * cellSize, x * cellSize, cellSize, cellSize);
                } else if (board.isObstacle(x, y)) {
                    gc.drawImage(assets.get("obstacle"), y * cellSize, x * cellSize, cellSize, cellSize);
                }
            }

            drawSnake(gc, game.getPlayer(1).getSnake(), assets, cellSize);
            drawSnake(gc, game.getPlayer(2).getSnake(), assets, cellSize);
        }
    }

    private void drawSnake(GraphicsContext gc, Snake snake, HashMap<String, Image> assets, int cellSize) {
        List<Point> body = snake.getBody();

        String end = "2";
        if (snake.getColor() == "blue") {
            end = "";
        }

        for (int i = 0; i < body.size(); i++) {
            Point segment = body.get(i);
            Image imageToDraw;

            if (i == 0) { // Head
                imageToDraw = assets.get("head_" + snake.getDirection() + end);
            } else if (i == body.size() - 1) { // Tail
                imageToDraw = assets.get("tail_" + snake.getTailDirection() + end);
            } else { // Body
                imageToDraw = assets.get("body_" + snake.getBodyDirection(i) + end);
            }

            gc.drawImage(imageToDraw, segment.y * cellSize, segment.x * cellSize, cellSize, cellSize);
        }
    }

    public void showRound(int round) {
        roundLabel.setText("Round: " + round);
    }

    public void setCurrentPlayer(Player player) {
        currentPlayer = player;
        updatePlayerLabel();
    }

    private void updatePlayerLabel() {
        playerLabel.setText("Current Player: " + currentPlayer.getName());
    }

    public void showGameOver(String message) {
        winLabel.setText(message);
    }

    public void hideHeader() {
        header.setVisible(false);
    }

    public void showGame() {
        gameContainer.setVisible(true);
        startContainer.setVisible(false);
    }

    public void showPlayAgain() {
        playAgainButton.setVisible(true);
    }

    public void hidePlayAgain() {
        playAgainButton.setVisible(false);
    }

    public void updateGameStats(Player player1, Player player2) {
        gameAmount.setText("Games played: " + (player1.getWins() + player2.getWins()));
        playerWin1.setText(player1.getName() + " wins: " + player1.getWins());
        playerWin2.setText(player2.getName() + " wins: " + player2.getWins());
    }
}
