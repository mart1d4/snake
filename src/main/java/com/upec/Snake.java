package com.upec;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Queue;
import java.util.List;

public class Snake {
    private List<Point> body;
    private String color;
    private String[] directions = new String[] { "up", "down", "left", "right" };

    public Snake(String color) {
        this.body = new ArrayList<>();
        this.color = color;
    }

    public List<Point> getBody() {
        return body;
    }

    public Point getHead() {
        return body.get(0);
    }

    public String getColor() {
        return color;
    }

    public void addBody(List<Point> body) {
        this.body.addAll(body);
    }

    public void addTail(Point segment) {
        body.add(segment);
    }

    // Used to determine the direction of the snake
    // to correctly draw its head in the UI
    // or to play the game with the AI
    public String getDirection() {
        Point head = body.get(0);
        Point next = body.get(1);

        if (head.x == next.x) {
            return head.y < next.y ? "left" : "right";
        } else {
            return head.x < next.x ? "up" : "down";
        }
    }

    // Used to determine the direction of the tail
    // to correctly draw the tail in the UI
    public String getTailDirection() {
        Point tail = body.get(body.size() - 1);
        Point beforeTail = body.get(body.size() - 2);

        if (tail.x == beforeTail.x) {
            return tail.y < beforeTail.y ? "left" : "right";
        } else {
            return tail.x < beforeTail.x ? "up" : "down";
        }
    }

    // Used to determine the direction of the body segment
    // to correctly draw the body in the UI
    public String getBodyDirection(int i) {
        if (i < 1 || i >= body.size() - 1) { // Prevent out of bounds
            return getDirection();
        }

        Point current = body.get(i);
        Point next = body.get(i + 1);
        Point previous = body.get(i - 1);

        // Determine if the segment is a straight part
        if (current.x == next.x && current.x == previous.x) {
            return "horizontal";
        } else if (current.y == next.y && current.y == previous.y) {
            return "vertical";
        }

        // Determine if the segment is a turning part
        if ((current.x == previous.x && current.y == next.y) || (current.y == previous.y && current.x == next.x)) {
            // Bottom left turn
            if ((previous.x < current.x && next.y > current.y) || (next.x < current.x && previous.y > current.y)) {
                return "topright";
            }
            // Bottom right turn
            if ((previous.x < current.x && next.y < current.y) || (next.x < current.x && previous.y < current.y)) {
                return "topleft";
            }
            // Top left turn
            if ((previous.x > current.x && next.y > current.y) || (next.x > current.x && previous.y > current.y)) {
                return "bottomright";
            }
            // Top right turn
            if ((previous.x > current.x && next.y < current.y) || (next.x > current.x && previous.y < current.y)) {
                return "bottomleft";
            }
        }

        return "vertical"; // Default case, shouldn't happen
    }

    // Move the snake by adding a new head and removing the tail
    // unless the snake is growing in which case the tail is not removed
    public void move(int dx, int dy, boolean grow) {
        Point head = body.get(0);
        Point newHead = new Point(head.x + dx, head.y + dy);
        body.add(0, newHead);

        if (!grow) {
            body.remove(body.size() - 1);
        }
    }

    // Remove the last segment of the snake
    public void shrink() {
        body.remove(body.size() - 1);
    }

    public boolean checkCollisionWithItself() {
        for (int i = 1; i < body.size(); i++) {
            if (body.get(0).equals(body.get(i))) {
                return true;
            }
        }

        return false;
    }

    // Used to check if the snake collided with
    // - the other snake
    // - an obstacle
    // - a food
    public boolean checkCollision(List<Point> list) {
        for (Point point : list) {
            if (body.get(0).equals(point)) {
                return true;
            }
        }

        return false;
    }

    // This method is called after checkCollion returns true for foods,
    // to get the food that the snake collided with
    // and remove it from the list of foods
    public Point getFoodCollided(List<Point> foods) {
        for (Point food : foods) {
            if (body.get(0).equals(food)) {
                return food;
            }
        }

        return null;
    }

    public boolean checkCollisionWithWall(int width, int height) {
        Point head = body.get(0);
        return head.x < 0 || head.x >= height || head.y < 0 || head.y >= width;
    }

    // Snake is dead if it collides with itself, the other snake, an obstacle or a
    // wall
    public boolean isSnakeDead(Board board, Snake otherSnake) {
        return (checkCollisionWithItself()
                || checkCollision(otherSnake.getBody())
                || checkCollision(board.getObstacles())
                || checkCollisionWithWall(board.getWidth(), board.getHeight()));
    }

    public String playMoveAI(Board board, Snake otherSnake, boolean eatFood) {
        Point food = findNearestFood(board);
        Point opponentHead = otherSnake.getHead();

        // Move towards the nearest food if available
        // and if it is enabled
        if (food != null && eatFood) {
            String moveToFood = moveToTarget(board, food, otherSnake);
            if (moveToFood != null) {
                return moveToFood;
            }
        }

        // Chase the opponent if no food is found or if it is disabled
        String moveToOpponent = moveToTarget(board, opponentHead, otherSnake);
        if (moveToOpponent != null) {
            return moveToOpponent;
        }

        // Fallback to a safe random move
        List<String> possibleMoves = new ArrayList<>();
        for (String direction : directions) {
            Snake clone = cloneSnake();
            Point dir = Player.getPointFromDirection(direction);
            clone.move(dir.x, dir.y, false);

            if (!clone.isSnakeDead(board, otherSnake) && isPathSafe(board, clone,
                    otherSnake, dir)) {
                possibleMoves.add(direction);
            }
        }

        if (possibleMoves.size() == 0) {
            return getDirection();
        }

        int randomIndex = (int) (Math.random() * possibleMoves.size());
        return possibleMoves.get(randomIndex);
    }

    // Move towards a target point
    private String moveToTarget(Board board, Point target, Snake otherSnake) {
        Point head = this.getHead();
        int minDistance = Integer.MAX_VALUE;
        String bestDirection = null;

        for (String direction : directions) {
            Point dir = Player.getPointFromDirection(direction);
            Point newPos = new Point(head.x + dir.x, head.y + dir.y);

            if (!board.isCellBlocked(newPos.x, newPos.y)) {
                int distance = Math.abs(target.x - newPos.x) + Math.abs(target.y - newPos.y);
                if (distance < minDistance) {
                    Snake clone = cloneSnake();
                    clone.move(dir.x, dir.y, false);
                    if (isPathSafe(board, clone, otherSnake, dir)) {
                        minDistance = distance;
                        bestDirection = direction;
                    }
                }
            }
        }

        return bestDirection;
    }

    // Check if moving in a direction is safe by evaluating multiple steps ahead
    private boolean isPathSafe(Board board, Snake clone, Snake otherSnake, Point direction) {
        int maxSteps = 3; // Initial number of lookahead steps
        while (maxSteps > 0) {
            if (isPathClear(board, clone, otherSnake, direction, maxSteps)) {
                return true; // Safe path found
            }
            maxSteps--; // Reduce the number of lookahead steps
        }
        return false; // No safe path found within the specified number of steps
    }

    // Use BFS to check if a path is clear for a given number of steps
    // (https://fr.wikipedia.org/wiki/Algorithme_de_parcours_en_largeur)
    private boolean isPathClear(Board board, Snake clone, Snake otherSnake, Point direction, int steps) {
        Queue<Snake> queue = new LinkedList<>();
        queue.add(clone);

        while (!queue.isEmpty() && steps > 0) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Snake current = queue.poll();
                Point head = current.getHead();

                for (String d : directions) {
                    Point dir = Player.getPointFromDirection(d);
                    Snake nextMove = current.cloneSnake();
                    nextMove.move(dir.x, dir.y, false);

                    if (nextMove.isSnakeDead(board, otherSnake)
                            || board.isCellBlocked(head.x + dir.x, head.y + dir.y)) {
                        continue;
                    }

                    queue.add(nextMove);
                }
            }
            steps--;
        }

        if (queue.isEmpty()) {
            // If the path is not clear, recursively reduce the number of steps
            if (steps > 0) {
                return isPathClear(board, clone, otherSnake, direction, steps - 1);
            } else {
                return false; // No safe path found within the specified number of steps
            }
        } else {
            return true; // Safe path found
        }
    }

    public Snake cloneSnake() {
        Snake clone = new Snake(color);
        clone.addBody(new ArrayList<>(body));
        return clone;
    }

    private Point findNearestFood(Board board) {
        Point head = this.getHead();
        Point nearestFood = null;
        int minDistance = Integer.MAX_VALUE;

        for (Point food : board.getFoods()) {
            int distance = Math.abs(food.x - head.x) + Math.abs(food.y - head.y);
            if (distance < minDistance) {
                minDistance = distance;
                nearestFood = food;
            }
        }

        return nearestFood;
    }
}
