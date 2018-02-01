package org.inspirecenter.amazechallenge.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.controller.RuntimeController;
import org.inspirecenter.amazechallenge.model.BackgroundImage;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.GameFullState;
import org.inspirecenter.amazechallenge.model.Grid;
import org.inspirecenter.amazechallenge.model.PickableItem;
import org.inspirecenter.amazechallenge.model.PickableType;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.Direction;
import org.inspirecenter.amazechallenge.model.PlayerPositionAndDirection;
import org.inspirecenter.amazechallenge.model.Position;
import org.inspirecenter.amazechallenge.model.Shape;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_LEFT_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_LOWER_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_RIGHT_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_UPPER_SIDE;

/**
 * @author Nearchos
 *         Created: 14-Aug-17
 */

public class GameView extends View {

    public static final String TAG = "aMaze";

    public static final int COLOR_BLACK         = Color.rgb(0, 0, 0);
    public static final int COLOR_LIGHT_RED     = Color.rgb(255, 192, 192);
    public static final int COLOR_LIGHT_GREEN   = Color.rgb(192, 255, 192);

    private final Context context;

    public static final BackgroundImage DEFAULT_MAZE_BACKGROUND = BackgroundImage.TEXTURE_GRASS;

    public GameView(final Context context) {
        super(context);
        this.context = context;
        //backgroundDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), DEFAULT_MAZE_BACKGROUND.getResourceID()));
        backgroundDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(BackgroundImage.TEXTURE_GRASS.getResourceName(), "drawable", context.getPackageName())));
    }

    public GameView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        backgroundDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(BackgroundImage.TEXTURE_GRASS.getResourceName(), "drawable", context.getPackageName())));
    }

    private Grid grid = null;
    private int lineColor = Color.BLACK;
    private BitmapDrawable backgroundDrawable;
    public Map<String,Player> allPlayers;
    public Map<String,PlayerPositionAndDirection> activePlayerPositionAndDirectionMap = new HashMap<>();
    public List<String> queuedPlayerEmails;
    public List<PickableItem> pickableItems = new Vector<>();

    void setGrid(final Grid grid) {
        this.grid = grid;
    }

    void update(final Game game) {
        this.allPlayers = game.getAllPlayerEmailsToPlayers();
        for(final String activePlayerEmail : game.getActivePlayers()) {
            activePlayerPositionAndDirectionMap.put(activePlayerEmail, game.getPlayerPositionAndDirection(activePlayerEmail));
        }
        queuedPlayerEmails = game.getQueuedPlayers();
        this.pickableItems = game.getPickableItems();
    }

    void setLineColor(String lineColor) {
        this.lineColor = Color.parseColor(lineColor);
    }

    void setBackgroundDrawable(int backgroundImage) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), backgroundImage);
        backgroundDrawable = new BitmapDrawable(getResources(), bm);
    }

    void setBackgroundDrawable(BackgroundImage backgroundImage) {
        Bitmap bm;
        if (backgroundImage != null)
            bm = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(backgroundImage.getResourceName(), "drawable", context.getPackageName()));
        else
            bm = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(DEFAULT_MAZE_BACKGROUND.getResourceName(), "drawable", context.getPackageName()));
        backgroundDrawable = new BitmapDrawable(getResources(), bm);
    }

    void update(final GameFullState gameFullState) {
        this.grid = gameFullState.getGrid();
        allPlayers = gameFullState.getAllPlayers();
        activePlayerPositionAndDirectionMap = gameFullState.getActivePlayerPositionsAndDirections();
        queuedPlayerEmails = gameFullState.getQueuedPlayerEmails();
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

        backgroundDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        backgroundDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        backgroundDrawable.draw(canvas);

        if(grid == null) return;

        // compute tile_size and padding
        final int width = canvas.getWidth();
        final int height = canvas.getHeight();
        final int smallestSide = Math.min(width, height);
        // assumes the tiles are squares
        final int tile_size = smallestSide / grid.getWidth();
        final int padding = (smallestSide - (tile_size * grid.getWidth())) / 2;

        // draw maze grid (row 0 is top, and col 0 is left (so move from top left rightwards, then next row, and so on)
        for(int row = 0; row < grid.getWidth(); row++) {
            for(int col = 0; col < grid.getHeight(); col++) {
                final int shape = RuntimeController.getGridCell(grid, row, col);
//                Log.d("aMaze", "row: " + row + ", col: " + col + " -> shape: " + shape);
                drawGridCell(row, col, tile_size, padding, shape, lineColor, canvas);
            }
        }

        // draw starting and target positions
        final Position startingPosition = grid.getStartingPosition();
        drawGridCell(startingPosition.getRow(), startingPosition.getCol(), tile_size, padding, 0x0, COLOR_BLACK, COLOR_LIGHT_RED, canvas);
        final Position targetPosition = grid.getTargetPosition();
        drawGridCell(targetPosition.getRow(), targetPosition.getCol(), tile_size, padding, 0x0, COLOR_BLACK, COLOR_LIGHT_GREEN, canvas);

        // draw pickableItems and rewards
        for(final PickableItem pickableItem : pickableItems)
            drawPickupItem(pickableItem.getPosition(), getDrawableResourceId(pickableItem), tile_size, padding, canvas);


        // draw active players
        for(final Map.Entry<String,PlayerPositionAndDirection> entry : activePlayerPositionAndDirectionMap.entrySet()) {
            final String activePlayerEmail = entry.getKey();
            final PlayerPositionAndDirection playerPositionAndDirection = entry.getValue();
            drawPlayer(allPlayers.get(activePlayerEmail), playerPositionAndDirection.getPosition(), playerPositionAndDirection.getDirection(), tile_size, padding, canvas);
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
        paint.setStrokeWidth(5f);

        final int topLeftX = col * tile_size + padding;
        final int topLeftY = row * tile_size + padding;

        int gridDimension = Math.max(grid.getHeight(), grid.getWidth());
        if (gridDimension > 25) paint.setStrokeWidth(3f);

        // draw left line
        if((shape & SHAPE_ONLY_LEFT_SIDE) != 0) { canvas.drawLine(topLeftX, topLeftY, topLeftX, topLeftY + tile_size, paint); }
        // draw right line
        if((shape & SHAPE_ONLY_RIGHT_SIDE) != 0) { canvas.drawLine(topLeftX + tile_size, topLeftY, topLeftX + tile_size, topLeftY + tile_size, paint); }
        // draw upper line
        if((shape & SHAPE_ONLY_UPPER_SIDE) != 0) { canvas.drawLine(topLeftX, topLeftY, topLeftX + tile_size, topLeftY, paint); }
        // draw lower line
        if((shape & SHAPE_ONLY_LOWER_SIDE) != 0) { canvas.drawLine(topLeftX, topLeftY + tile_size, topLeftX + tile_size, topLeftY + tile_size, paint); }

    }

    private void drawPlayer(final Player player, final Position position, final Direction direction, final int tile_size, final int padding, final Canvas canvas) {
        if (player.isActive()) drawShape(position, player.getShape(), direction, Color.parseColor(player.getColor().getCode()), tile_size, padding, canvas);
    }

    private void drawPickupItem(final Position position, final int imageResourceID, final int tile_size, final int padding, final Canvas canvas) {

        final int topLeftX = position.getCol() * tile_size + padding;
        final int topLeftY = position.getRow() * tile_size + padding;

        Drawable d = getResources().getDrawable(imageResourceID);

        if (d != null) {
            d.setBounds(topLeftX + tile_size / 8, topLeftY + tile_size / 8, topLeftX + tile_size * 7 / 8, topLeftY + tile_size * 7 / 8);
            d.draw(canvas);
        }

    }

    private void drawShape(final Position position, final Shape shape, final Direction direction, final int color, final int tile_size, final int padding, final Canvas canvas) {

        paint.setColor(color);

        final int topLeftX = position.getCol() * tile_size + padding;
        final int topLeftY = position.getRow() * tile_size + padding;

        switch (shape) {

                //Player shapes:

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

                //Draw the inner part of the triangle:
                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(point1.x,point1.y);
                path.lineTo(point2.x,point2.y);
                path.lineTo(point3.x,point3.y);
                path.lineTo(point1.x,point1.y);
                path.close();
                canvas.drawPath(path, paint);

                //Draw the outline of the triangle:
                paint.setColor(Color.WHITE);
                canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint);
                canvas.drawLine(point2.x, point2.y, point3.x, point3.y, paint);
                canvas.drawLine(point1.x, point1.y, point3.x, point3.y, paint);

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

    private int getDrawableResourceId(final PickableItem item) {
        if (item.getPickableType() == PickableType.BOMB) {
            if (item.getState() > 5) return R.drawable.bomb;
            else if (item.getState() > 1) return R.drawable.bomb_stage2;
            else return R.drawable.bomb_stage3;
        }
        return getResources().getIdentifier(item.getPickableType().getImageResourceName(), "drawable", context.getPackageName());
    }
}