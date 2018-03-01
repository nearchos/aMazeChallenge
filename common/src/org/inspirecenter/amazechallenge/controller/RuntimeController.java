package org.inspirecenter.amazechallenge.controller;

import org.inspirecenter.amazechallenge.algorithms.MazeSolver;
import org.inspirecenter.amazechallenge.algorithms.PlayerMove;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.Direction;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.Grid;
import org.inspirecenter.amazechallenge.model.Pickable;
import org.inspirecenter.amazechallenge.model.PickableType;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.PlayerPositionAndDirection;
import org.inspirecenter.amazechallenge.model.Position;

import java.util.*;

import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_LEFT_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_LOWER_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_RIGHT_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_UPPER_SIDE;

/**
 * @author Nearchos
 *         Created: 11-Dec-17
 */

public class RuntimeController {

    private static HashMap<String, Integer> doubleTurnsMap = new HashMap<>();
    private static HashMap<String, Integer> lostTurnsMap = new HashMap<>();

    public static void makeMove(final Challenge challenge, final Game game, final Map<String,MazeSolver> playerIdsToMazeSolvers) {
        final Grid grid = challenge.getGrid();

        // apply next move to active players
        final List<String> playerToMoveIDs = game.getActivePlayerIDs();

        // the adjusted list will contain the player IDs, possibly two or zero times according to the num of turns they get/lose from pickables
        final List<String> adjustedPlayerToMoveIDs = new Vector<>(playerToMoveIDs);

        for (final String playerToMoveId : playerToMoveIDs) {

            // check if player lost a move
            if (playerHasLostTurnsById(playerToMoveId)) {
                decreasePlayerLostTurnsRemainingById(playerToMoveId);
                adjustedPlayerToMoveIDs.remove(playerToMoveId); // remove first entry it finds
            }

            // check if a second move is needed
            if (playerHasDoubleTurnsById(playerToMoveId)) {
                decreasePlayerDoubleTurnsRemainingById(playerToMoveId);
                adjustedPlayerToMoveIDs.add(playerToMoveId); // add another entry at the end
            }
        }

        for (final String playerToMoveId : adjustedPlayerToMoveIDs) {
            final PlayerPositionAndDirection playerPositionAndDirection = game.getPlayerPositionAndDirectionById(playerToMoveId);
            final Player player = game.getPlayerById(playerToMoveId);
            final MazeSolver mazeSolver = playerIdsToMazeSolvers.get(playerToMoveId);

            // the player might have been deactivated (for instance if he had 2 moves and this one is the second)
            if(game.getActivePlayerIDs().contains(playerToMoveId)) {
                final PlayerMove nextPlayerMove = mazeSolver == null ? PlayerMove.NO_MOVE : mazeSolver.getNextMove(game);
                applyPlayerMove(grid, game, playerToMoveId, playerPositionAndDirection, nextPlayerMove);

                // check the player's health and finalize if needed
                if (player.getHealth().isAtMin()) {
                    game.setPlayerPositionAndDirectionById(playerToMoveId, new PlayerPositionAndDirection(grid.getStartingPosition(), Direction.NORTH));
                    game.resetPlayerById(playerToMoveId);
                    resetTurnEffects();
                    final AudioEventListener audioEventListener = game.getAudioEventListener();
                    if(audioEventListener != null) {
                        audioEventListener.onGameEndAudioEvent(false);
                    }
                }
            }

            generateItems(game, challenge, grid); // generate pickables etc.
            handlePickableState(game); // handle stateful pickables
        }
    }

    private static void applyPlayerMove(final Grid grid, final Game game, final String playerId, final PlayerPositionAndDirection playerPositionAndDirection, final PlayerMove playerMove) {
        Direction direction = playerPositionAndDirection.getDirection();
        Position position = playerPositionAndDirection.getPosition();
        AudioEventListener audioEventListener = game.getAudioEventListener();

        switch (playerMove) {
            case TURN_CLOCKWISE:
                direction = playerPositionAndDirection.getDirection().turnClockwise();
                break;
            case TURN_COUNTERCLOCKWISE:
                direction = direction.turnCounterClockwise();
                break;
            case MOVE_FORWARD:
                if (canMoveForward(grid, playerPositionAndDirection.getPosition(), playerPositionAndDirection.getDirection())) {
                    position = movePlayerForward(playerPositionAndDirection);
                    // handle pickables and rewards (i.e. add/substract health etc.)
                    for(int i = 0; i < game.getPickables().size(); i++) {
                        Pickable pickable = game.getPickables().get(i);

                        if(pickable.getPosition().equals(position)) {

                            //Health-related pickables:
                            if (pickable.getPickableType() == PickableType.BOMB) {
                                if (pickable.getState() == 1 || pickable.getState() == 2)
                                    game.getPlayerById(playerId).getHealth().changeBy(pickable.getPickableType().getHealthChange());
                            }
                            else game.getPlayerById(playerId).getHealth().changeBy(pickable.getPickableType().getHealthChange());

                            //Apply effects of point-related pickables:
                            game.getPlayerById(playerId).changePointsBy(pickable.getPickableType().getPointsChange());

                            //Apply effects of speed-related pickables:
                            if (pickable.getPickableType() == PickableType.SPEEDHACK) {
                                addPlayerDoubleTurnsById(playerId, PickableType.SPEEDHACK_TURNS_AMOUNT);
                            } else if (pickable.getPickableType() == PickableType.TRAP) {
                                addPlayerLostTurnsById(playerId, PickableType.TRAP_TURNS_AMOUNT);
                            }

                            // if audio event listener set, notify with event
                            if(audioEventListener != null && pickable.getPickableType() != PickableType.BOMB) audioEventListener.onAudioEvent(pickable);
                            if (pickable.getPickableType() != PickableType.BOMB) game.removePickupItem(i);
                        }

                        //Player is near a bomb, but not over it:
                        if (pickable.getPickableType() == PickableType.BOMB && (pickable.getState() == 1 || pickable.getState() == 2)) {

                            /*
                                NOTE: Bombs will do damage in terms of radius:
                                    1) On top of bomb - 100% damage of original.
                                    2) 1 block away from bomb (incl. diagonals) - 50% damage of original.
                             */
                            int colDifference = Math.abs(position.getCol() - pickable.getPosition().getCol());
                            int rowDifference = Math.abs(position.getRow() - pickable.getPosition().getRow());

                            //1 Block-radius away:
                            if (colDifference <= 1 && rowDifference <= 1) {
                                game.getPlayerById(playerId).getHealth().changeBy(pickable.getPickableType().getHealthChange() / 2);
                            }

                            if(audioEventListener != null) {
                                audioEventListener.onAudioEvent(pickable);
                            }
                        }
                    }
                }
                break;
            case NO_MOVE:
                // Log.d("grid-challenge", "move: " + playerMove);
                break;
            default:
                throw new RuntimeException("Invalid PlayerMove: " + playerMove);
        }
        final PlayerPositionAndDirection updatedPlayerPositionAndDirection = new PlayerPositionAndDirection(position, direction);
        game.setPlayerPositionAndDirectionById(playerId, updatedPlayerPositionAndDirection);
    }

    public static boolean hasSomeoneReachedTheTargetPosition(final Game game, final Grid grid) {
        final Position targetPosition = grid.getTargetPosition();
        boolean someoneHasReachedTheTargetPosition = false;
        for (final String playerId : game.getActivePlayerIDs()) {
            final PlayerPositionAndDirection playerPositionAndDirection = game.getPlayerPositionAndDirectionById(playerId);
            if (targetPosition.equals(playerPositionAndDirection.getPosition())) {
                if (grid.getTargetPosition().equals(playerPositionAndDirection.getPosition())) {
                    game.getAudioEventListener().onGameEndAudioEvent(true); //TODO Remove, make for each player individually.
                }
                someoneHasReachedTheTargetPosition = true;
                resetTurnEffects();
            }
        }
        return someoneHasReachedTheTargetPosition;
    }

    /**
     * Checks if the given player (specified by its {@link Position} and {@link Direction} can move forward in the
     * given {@link Grid}.
     *
     * @param grid the grid in which the {@link Player} operates
     * @param position the {@link Position} of the {@link Player}
     * @param direction the {@link Direction} of the {@link Player}
     * @return true iff the player at the given {@link Position} and {@link Direction} can move forward
     */
    public static boolean canMoveForward(final Grid grid, final Position position, final Direction direction) {
        return !RuntimeController.hasWall(grid, position, direction);
    }

    /**
     * Checks if the given player (specified by its {@link Position} and {@link Direction} can move forward in the
     * given {@link Grid}.
     *
     * @param grid the grid in which the {@link Player} operates
     * @param position the {@link Position} of the {@link Player}
     * @param direction the {@link Direction} of the {@link Player}
     * @return true iff the player at the given {@link Position} and {@link Direction} can move forward
     */
    public static boolean canMoveBackward(final Grid grid, final Position position, final Direction direction) {
        final Direction oppositeDirection = direction.opposite();
        return !hasWall(grid, position, oppositeDirection);
    }

    /**
     * Checks if the {@link Player} can move 'left' in the given {@link Grid}, relative to its {@link Position} and
     * {@link Direction}.
     *
     * @param grid the grid in which the {@link Player} operates
     * @param position the {@link Position} of the {@link Player}
     * @param direction the {@link Direction} of the {@link Player}
     * @return true iff the {@link Player} can move 'left', relative to {@link Position} and {@link Direction}
     */
    public static boolean canMoveLeft(final Grid grid, final Position position, final Direction direction) {
        final Direction leftDirection = direction.turnCounterClockwise();
        return !hasWall(grid, position, leftDirection);
    }

    /**
     * Checks if the {@link Player} can move 'right' in the given {@link Grid}, relative to its {@link Position} and
     * {@link Direction}.
     *
     * @param grid the grid in which the {@link Player} operates
     * @param position the {@link Position} of the {@link Player}
     * @param direction the {@link Direction} of the {@link Player}
     * @return true iff the {@link Player} can move 'right', relative to {@link Position} and {@link Direction}
     */
    public static boolean canMoveRight(final Grid grid, final Position position, final Direction direction) {
        final Direction rightDirection = direction.turnClockwise();
        return !hasWall(grid, position, rightDirection);
    }

    public static PickableType.Bias look(final Game game, final Grid grid, final Position position, final Direction direction) {

        switch (direction) {
            case NORTH:
                if (position.getRow() - 1 > 0) {
                    for (Pickable i : game.getPickables()) {
                        if (i.getPosition().getRow() == position.getRow()-1 && i.getPosition().getCol() == position.getCol())
                            return i.getPickableType().getBias();
                    }
                }
                break;
            case SOUTH:
                if (position.getRow() + 1 < grid.getHeight()) {
                    for (Pickable i : game.getPickables()) {
                        if (i.getPosition().getRow() == position.getRow()+1 && i.getPosition().getCol() == position.getCol())
                            return i.getPickableType().getBias();
                    }
                }
                break;
            case EAST:
                if (position.getCol() + 1 < grid.getWidth()) {
                    for (Pickable i : game.getPickables()) {
                        if (i.getPosition().getCol() == position.getCol()+1 && i.getPosition().getRow() == position.getRow())
                            return i.getPickableType().getBias();
                    }
                }
                break;
            case WEST:
                if (position.getCol() - 1 > 0) {
                    for (Pickable i : game.getPickables()) {
                        if (i.getPosition().getCol() == position.getCol()-1 && i.getPosition().getRow() == position.getRow())
                            return i.getPickableType().getBias();
                    }
                }
                break;
        }
        return PickableType.Bias.NONE;
    }

    public static Direction compass(final Position targetPosition, final Position playerPosition) {
        int rowDifference = playerPosition.getRow() - targetPosition.getRow();
        int colDifference = playerPosition.getCol() - targetPosition.getCol();

        /*
            NOTE:

                Positive rowDifference => Exit is toward NORTH.
                Negative rowDifference => Exit is toward SOUTH.

                Positive colDifference => Exit is toward WEST.
                Negative rowDifference => Exit is toward EAST.
         */

        Direction predominantEastWestDirection;
        Direction predominantNorthSouthDirection;

        if (rowDifference >= 0) predominantNorthSouthDirection = Direction.NORTH;
        else predominantNorthSouthDirection = Direction.SOUTH;

        if (colDifference >= 0) predominantEastWestDirection = Direction.WEST;
        else predominantEastWestDirection = Direction.EAST;

        if (Math.max(Math.abs(rowDifference), Math.abs(colDifference)) == Math.abs(rowDifference)) {
            return predominantNorthSouthDirection;
        } else {
            return predominantEastWestDirection;
        }
    }

    public static int getGridCell(final Grid grid, final int row, final int col) throws IndexOutOfBoundsException {
        if(col < 0 || col > grid.getWidth()) throw new IndexOutOfBoundsException("col not in bounds [0, " + grid.getWidth() + ")");
        if(row < 0 || row > grid.getHeight()) throw new IndexOutOfBoundsException("row not in bounds [0, " + grid.getHeight() + ")");
        final char c = grid.getData().charAt(row * grid.getWidth() + col);
        return Integer.parseInt(Character.toString(c), 16);
    }

    private static boolean hasWall(final Grid grid, final Position position, final Direction direction) {
        final int shape = getGridCell(grid, position.getRow(), position.getCol());
        switch (direction) {
            case NORTH:
                return (shape & SHAPE_ONLY_UPPER_SIDE) != 0;
            case SOUTH:
                return (shape & SHAPE_ONLY_LOWER_SIDE) != 0;
            case WEST:
                return (shape & SHAPE_ONLY_LEFT_SIDE) != 0;
            case EAST:
                return (shape & SHAPE_ONLY_RIGHT_SIDE) != 0;
            default:
                throw new RuntimeException("Invalid direction: " + direction);
        }
    }

    private static Position movePlayerForward(final PlayerPositionAndDirection playerPositionAndDirection) {
        switch (playerPositionAndDirection.getDirection()) {
            case NORTH:
                return playerPositionAndDirection.getPosition().moveNorth();
            case SOUTH:
                return playerPositionAndDirection.getPosition().moveSouth();
            case WEST:
                return playerPositionAndDirection.getPosition().moveWest();
            case EAST:
                return playerPositionAndDirection.getPosition().moveEast();
            default: throw new RuntimeException("Invalid direction: " + playerPositionAndDirection.getDirection());
        }
    }

    public static boolean allPlayersHaveLost(final Game game) {
        return game.getActivePlayerIDs().isEmpty();
    }

    private static void generateItems(Game game, Challenge challenge, Grid grid) {

        final Random random = new Random();

        //Generate rewards:
        if (game.getNumOfBiasType(PickableType.Bias.REWARD) < challenge.getMaxRewards()) {
            int row = random.nextInt(grid.getHeight());
            int col = random.nextInt(grid.getWidth());
            final Position position = new Position(row, col);

            final PickableType type = PickableType.getRandomReward();

            boolean exists = false;
            for (Pickable pickable : game.getPickables()) {
                if (pickable.getPosition().equals(position)) {
                    exists = true;
                    break;
                }
            }

            if (!exists && !grid.getTargetPosition().equals(position) && !grid.getStartingPosition().equals(position)) {
                game.addPickableItem(new Pickable(position, type));
            }
        }

        //Generate penalties:
        if (game.getNumOfBiasType(PickableType.Bias.PENALTY) < challenge.getMaxPenalties()) {
            int row = random.nextInt(grid.getHeight());
            int col = random.nextInt(grid.getWidth());
            final Position position = new Position(row, col);

            final PickableType type = PickableType.getRandomPenalty();

            boolean exists = false;
            for (Pickable pickable : game.getPickables()) {
                if (pickable.getPosition().equals(position)) {
                    exists = true;
                    break;
                }
            }

            if (!exists && !grid.getTargetPosition().equals(position) && !grid.getStartingPosition().equals(position)) {
                game.addPickableItem(new Pickable(position, type));
            }
        }
    }

    public static void handlePickableState(Game game) {
        for (int i = 0; i < game.getPickables().size(); i++) {
            game.getPickables().get(i).reduceState();
            if (game.getPickables().get(i).getState() <= 0) game.removePickupItem(i);
        }
    }

    public static int getPlayerDoubleTurnsRemainingById(String playerId) {
        if (!doubleTurnsMap.containsKey(playerId)) return 0;
        return doubleTurnsMap.get(playerId);
    }

    public static int getPlayerLostTurnsRemainingById(String playerId) {
        if (!lostTurnsMap.containsKey(playerId)) return 0;
        return lostTurnsMap.get(playerId);
    }

    public static boolean playerHasDoubleTurnsById(String playerId) {
        return doubleTurnsMap.containsKey(playerId);
    }

    public static boolean playerHasLostTurnsById(String playerId) {
        return lostTurnsMap.containsKey(playerId);
    }

    public static void decreasePlayerDoubleTurnsRemainingById(String playerId) {
        if (playerHasDoubleTurnsById(playerId)) {
            Integer turnsLeft = doubleTurnsMap.get(playerId);
            doubleTurnsMap.remove(playerId);
            turnsLeft--;
            if (turnsLeft > 0) doubleTurnsMap.put(playerId, turnsLeft);
        }
    }

    public static void decreasePlayerLostTurnsRemainingById(String playerId) {
        if (playerHasLostTurnsById(playerId)) {
            Integer turnsLeft = lostTurnsMap.get(playerId);
            lostTurnsMap.remove(playerId);
            turnsLeft--;
            if (turnsLeft > 0) lostTurnsMap.put(playerId, turnsLeft);
        }
    }

    public static void addPlayerDoubleTurnsById(String playerId, Integer doubleTurnsAmount) {
        doubleTurnsMap.put(playerId, doubleTurnsAmount);
    }

    public static void addPlayerLostTurnsById(String playerId, Integer lostTurnsAmount) {
        lostTurnsMap.put(playerId, lostTurnsAmount);
    }

    public static void resetTurnEffects() {
        lostTurnsMap = new HashMap<>();
        doubleTurnsMap = new HashMap<>();
    }
}