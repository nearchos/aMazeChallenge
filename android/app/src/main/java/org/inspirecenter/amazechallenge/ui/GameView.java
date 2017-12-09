package org.inspirecenter.amazechallenge.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.Direction;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.Position;
import org.inspirecenter.amazechallenge.model.Shape;

import java.util.Collection;

import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_LEFT_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_LOWER_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_RIGHT_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_UPPER_SIDE;

/**
 * @author Nearchos
 *         Created: 14-Aug-17
 */

public class GameView extends View {

    public static final int COLOR_BLACK         = Color.rgb(0, 0, 0);
    public static final int COLOR_RED           = Color.rgb(255, 0, 0);
    public static final int COLOR_LIGHT_RED     = Color.rgb(255, 192, 192);
    public static final int COLOR_LIGHT_GREEN   = Color.rgb(192, 255, 192);

    public GameView(final Context context) {
        super(context);
    }

    public GameView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    private Game game = null;

    void setGame(final Game game) {
        this.game = game;
        invalidate();
    }

    /**
     * This allows this custom view to maintain a 'square' dimension
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int dimen = (width > height) ? height : width;

        setMeasuredDimension(dimen, dimen);
    }

    private final Paint paint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(game == null) return;

        // compute tile_size and padding
        final int width = canvas.getWidth();
        final int height = canvas.getHeight();
        final int smallestSide = Math.min(width, height);
        // todo adjust if non-square grids are to be supported at drawn stage
        final int tile_size = smallestSide / game.getGridWidth();
        final int padding = (smallestSide - (tile_size * game.getGridWidth())) / 2;

        // draw maze grid (row 0 is top, and col 0 is left (so move from top left rightwards, then next row, and so on)
        if(game != null) {
            for(int row = 0; row < game.getGridWidth(); row++) {
                for(int col = 0; col < game.getGridHeight(); col++) {
                    final int shape = game.getCell(row, col);
//                    Log.d("aMaze", "row: " + row + ", col: " + col + " -> shape: " + shape);
                    drawGridCell(row, col, tile_size, padding, shape, COLOR_BLACK, canvas);
                }
            }
        }

        // draw starting and target positions
        final Position startingPosition = game.getStartingPosition();
        drawGridCell(startingPosition.getRow(), startingPosition.getCol(), tile_size, padding, 0x0, COLOR_BLACK, COLOR_LIGHT_RED, canvas);
        final Position targetPosition = game.getTargetPosition();
        drawGridCell(targetPosition.getRow(), targetPosition.getCol(), tile_size, padding, 0x0, COLOR_BLACK, COLOR_LIGHT_GREEN, canvas);

        // draw players
        final Collection<Player> players = game.getAllPlayers();
        for(final Player player : players) {
            drawPlayer(player, tile_size, padding, canvas);
        }
    }

    /**
     * @param shape the hex value of the shape (LSB is top, next is bottom, next is left and MSB is right)
     */
    private void drawGridCell(final int row, final int col, final int tile_size, final int padding, final int shape, final int color, final int background, final Canvas canvas) {

        paint.setColor(background);

        final int topLeftX = col * tile_size + padding;
        final int topLeftY = row * tile_size + padding;

        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(topLeftX + 1, topLeftY + 1, topLeftX + tile_size - 1, topLeftY + tile_size - 1, paint);

        drawGridCell(row, col, tile_size, padding, shape, color, canvas);
    }

    /**
     * @param shape the hex value of the shape (LSB is top, next is bottom, next is left and MSB is right)
     */
    private void drawGridCell(final int row, final int col, final int tile_size, final int padding, final int shape, final int color, final Canvas canvas) {

        paint.setColor(color);
        paint.setStrokeWidth(2f);

        final int topLeftX = col * tile_size + padding;
        final int topLeftY = row * tile_size + padding;

        // draw left line
        if((shape & SHAPE_ONLY_LEFT_SIDE) != 0) { canvas.drawLine(topLeftX, topLeftY, topLeftX, topLeftY + tile_size, paint); }
        // draw right line
        if((shape & SHAPE_ONLY_RIGHT_SIDE) != 0) { canvas.drawLine(topLeftX + tile_size, topLeftY, topLeftX + tile_size, topLeftY + tile_size, paint); }
        // draw upper line
        if((shape & SHAPE_ONLY_UPPER_SIDE) != 0) { canvas.drawLine(topLeftX, topLeftY, topLeftX + tile_size, topLeftY, paint); }
        // draw lower line
        if((shape & SHAPE_ONLY_LOWER_SIDE) != 0) { canvas.drawLine(topLeftX, topLeftY + tile_size, topLeftX + tile_size, topLeftY + tile_size, paint); }
    }

    private void drawPlayer(final Player player, final int tile_size, final int padding, final Canvas canvas) {
        drawShape(player.getPosition(), player.getShape(), player.getDirection(), Color.parseColor(player.getColor().getCode()), tile_size, padding, canvas);
    }

    private void drawShape(final Position position, final Shape shape, final Direction direction, final int color, final int tile_size, final int padding, final Canvas canvas) {

        paint.setColor(color);

        final int topLeftX = position.getCol() * tile_size + padding;
        final int topLeftY = position.getRow() * tile_size + padding;

        switch (shape) {
            case TRIANGLE:
                // draw directed triangle
                final Point point1, point2, point3;
                switch (direction) {
                    case NORTH:
                        point1 = new Point(topLeftX + tile_size / 2, topLeftY + tile_size / 4);
                        point2 = new Point(topLeftX + tile_size / 4, topLeftY + tile_size * 3 / 4);
                        point3 = new Point(topLeftX + tile_size * 3 / 4, topLeftY + tile_size * 3 / 4);
                        break;
                    case SOUTH:
                        point1 = new Point(topLeftX + tile_size / 2, topLeftY + tile_size * 3 / 4);
                        point2 = new Point(topLeftX + tile_size / 4, topLeftY + tile_size / 4);
                        point3 = new Point(topLeftX + tile_size * 3 / 4, topLeftY + tile_size / 4);
                        break;
                    case WEST:
                        point1 = new Point(topLeftX + tile_size / 4, topLeftY + tile_size / 2);
                        point2 = new Point(topLeftX + tile_size * 3 / 4, topLeftY + tile_size / 4);
                        point3 = new Point(topLeftX + tile_size * 3 / 4, topLeftY + tile_size * 3 / 4);
                        break;
                    case EAST:
                        point1 = new Point(topLeftX + tile_size * 3 / 4, topLeftY + tile_size / 2);
                        point2 = new Point(topLeftX + tile_size / 4, topLeftY + tile_size / 4);
                        point3 = new Point(topLeftX + tile_size / 4, topLeftY + tile_size * 3 / 4);
                        break;
                    default:
                        throw new RuntimeException("Invalid direction: " + direction);
                }

                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(point1.x,point1.y);
                path.lineTo(point2.x,point2.y);
                path.lineTo(point3.x,point3.y);
                path.lineTo(point1.x,point1.y);
                path.close();

                canvas.drawPath(path, paint);
                break;

            case CIRCLE:
                // draw full circle
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(topLeftX + tile_size / 2, topLeftY + tile_size / 2, tile_size / 3, paint);
                break;

            case EMPTY_CIRCLE:
                // draw empty circle
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(topLeftX + tile_size / 2, topLeftY + tile_size / 2, tile_size / 3, paint);
                break;

            default:
                throw new RuntimeException("Invalid shape: " + shape);
        }
    }
}