package org.inspirecenter.amazechallenge.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.inspirecenter.amazechallenge.R;

/**
 * @author Nearchos
 *         Created: 20-Aug-17
 */

class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable dividerDrawable;

    SimpleDividerItemDecoration(final Context context) {
        dividerDrawable = ContextCompat.getDrawable(context, R.drawable.line_divider);
    }

    @Override
    public void onDrawOver(final Canvas canvas, final RecyclerView parentRecyclerView, final RecyclerView.State state) {
        final int left = parentRecyclerView.getPaddingLeft();
        final int right = parentRecyclerView.getWidth() - parentRecyclerView.getPaddingRight();

        final int childCount = parentRecyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parentRecyclerView.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + dividerDrawable.getIntrinsicHeight();

            dividerDrawable.setBounds(left, top, right, bottom);
            dividerDrawable.draw(canvas);
        }
    }
}