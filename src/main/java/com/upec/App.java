package com.upec;

import javafx.application.Application;

import javafx.scene.input.KeyEvent;
import javafx.scene.image.Image;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class App extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int BOARD_WIDTH = 20;
    private static final int BOARD_HEIGHT = 20;
    private static final int CELL_SIZE = getCellSize();

    private static Game game;
    private static boolean isPlaying = false;

    @SuppressWarnings("unused")
    private static int getCellSize() {
        return WIDTH / BOARD_WIDTH < HEIGHT / BOARD_HEIGHT
                ? WIDTH / BOARD_WIDTH
                : HEIGHT / BOARD_HEIGHT;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        Scene scene = new Scene(root, 1500, 900);

        HashMap<String, Image> assets = getAssets();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Snake Blockade");
        primaryStage.getIcons().add(assets.get("apple"));
        primaryStage.show();

        game = new Game(BOARD_WIDTH, BOARD_HEIGHT, CELL_SIZE, controller, assets);
        scene.setOnKeyPressed(this::handleKeyPress);
    }

    public static void startGame() {
        game.initialize();
        isPlaying = true;
    }

    public static void stopGame() {
        isPlaying = false;
    }

    public static void closeApp() {
        System.exit(0);
    }

    private HashMap<String, Image> getAssets() {
        List<String> assetNames = List.of(
                "apple",
                "obstacle",
                "body_bottomleft",
                "body_bottomleft2",
                "body_bottomright",
                "body_bottomright2",
                "body_horizontal",
                "body_horizontal2",
                "body_topleft",
                "body_topleft2",
                "body_topright",
                "body_topright2",
                "body_vertical",
                "body_vertical2",
                "head_down",
                "head_down2",
                "head_left",
                "head_left2",
                "head_right",
                "head_right2",
                "head_up",
                "head_up2",
                "tail_down",
                "tail_down2",
                "tail_left",
                "tail_left2",
                "tail_right",
                "tail_right2",
                "tail_up",
                "tail_up2");

        HashMap<String, Image> assets = new HashMap<>();

        for (String name : assetNames) {
            Image asset = new Image(getClass().getResourceAsStream("/graphics/" + name + ".png"));
            assets.put(name, asset);
        }

        return assets;
    }

    private void handleKeyPress(KeyEvent event) {
        event.consume();

        if (!isPlaying) {
            return;
        }

        switch (event.getCode()) {
            case UP:
                game.moveCurrentPlayer("up");
                break;
            case DOWN:
                game.moveCurrentPlayer("down");
                break;
            case LEFT:
                game.moveCurrentPlayer("left");
                break;
            case RIGHT:
                game.moveCurrentPlayer("right");
                break;
            case W:
                game.moveCurrentPlayer("up");
                break;
            case S:
                game.moveCurrentPlayer("down");
                break;
            case A:
                game.moveCurrentPlayer("left");
                break;
            case D:
                game.moveCurrentPlayer("right");
                break;
            case ESCAPE:
                closeApp();
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}