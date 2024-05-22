package com.upec;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Snake {
    private List<Point> body;
    private String color;

    public Snake(String color) {
        this.body = new ArrayList<>();
        this.color = color;
    }

    public List<Point> getBody() {
        return body;
    }

    public String getColor() {
        return color;
    }

    public void addSegment(Point segment) {
        body.add(segment);
    }

    public String getDirection() {
        Point head = body.get(0);
        Point next = body.get(1);

        if (head.x == next.x) {
            return head.y < next.y ? "left" : "right";
        } else {
            return head.x < next.x ? "up" : "down";
        }
    }

    public String getTailDirection() {
        Point tail = body.get(body.size() - 1);
        Point beforeTail = body.get(body.size() - 2);

        if (tail.x == beforeTail.x) {
            return tail.y < beforeTail.y ? "left" : "right";
        } else {
            return tail.x < beforeTail.x ? "up" : "down";
        }
    }

    public String getBodyDirection(int i) {
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
    public void move(int dx, int dy, boolean grow) {
        Point head = body.get(0);
        Point newHead = new Point(head.x + dx, head.y + dy);
        body.add(0, newHead);

        if (!grow) {
            body.remove(body.size() - 1);
        }
    }

    public void shrink() {
        body.remove(body.size() - 1);
    }

    public boolean checkCollisionWithItself() {
        Point head = body.get(0);

        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }

        return false;
    }

    public boolean checkCollisionWithOtherSnake(Snake otherSnake) {
        for (Point segment : otherSnake.getBody()) {
            if (body.get(0).equals(segment)) {
                return true;
            }
        }

        return false;
    }

    public boolean checkCollisionWithObstacle(List<Point> obstacles) {
        for (Point obstacle : obstacles) {
            if (body.get(0).equals(obstacle)) {
                return true;
            }
        }

        return false;
    }

    public boolean checkCollisionWithFood(List<Point> foods) {
        for (Point food : foods) {
            if (body.get(0).equals(food)) {
                return true;
            }
        }

        return false;
    }

    // This method is called after checkCollisionWithFood
    // returns true to get the food that the snake collided with
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

    public boolean isSnakeDead(Board board, Snake otherSnake) {
        List<Point> obstacles = board.getObstacles();
        int width = board.getWidth();
        int height = board.getHeight();

        return checkCollisionWithItself() || checkCollisionWithOtherSnake(otherSnake)
                || checkCollisionWithObstacle(obstacles)
                || checkCollisionWithWall(width, height);
    }

    public Point getHead() {
        return body.get(0);
    }

    public String playAdvancedMoveAI(Board board, Snake otherSnake) {
        String[] directions = new String[] { "up", "down", "left", "right" };
        Point food = findNearestFood(board);
        Point opponentHead = otherSnake.getHead();

        // Move towards the nearest food if available
        if (food != null) {
            String moveToFood = moveToTarget(board, food, otherSnake);
            if (moveToFood != null) {
                return moveToFood;
            }
        }

        // Chase the opponent if no food is found
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
        String[] directions = new String[] { "up", "down", "left", "right" };
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
        int maxSteps = 5; // Initial number of lookahead steps
        while (maxSteps > 0) {
            if (isPathClear(board, clone, otherSnake, direction, maxSteps)) {
                return true; // Safe path found
            }
            maxSteps--; // Reduce the number of lookahead steps
        }
        return false; // No safe path found within the specified number of steps
    }

    // Use BFS to check if a path is clear for a given number of steps
    private boolean isPathClear(Board board, Snake clone, Snake otherSnake, Point direction, int steps) {
        Queue<Snake> queue = new LinkedList<>();
        queue.add(clone);

        while (!queue.isEmpty() && steps > 0) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Snake current = queue.poll();
                Point head = current.getHead();

                for (String d : new String[] { "up", "down", "left", "right" }) {
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
        for (Point segment : body) {
            clone.addSegment(segment);
        }
        return clone;
    }

    private Point findNearestFood(Board board) {
        List<Point> foods = board.getFoods();
        Point head = this.getHead();
        Point nearestFood = null;
        int minDistance = Integer.MAX_VALUE;

        for (Point food : foods) {
            int distance = Math.abs(food.x - head.x) + Math.abs(food.y - head.y);
            if (distance < minDistance) {
                minDistance = distance;
                nearestFood = food;
            }
        }
        return nearestFood;
    }
}
