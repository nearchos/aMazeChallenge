package org.inspirecenter.amazechallenge.ui;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.Player;

import java.util.Vector;

/**
 * @author Nearchos
 *         Created: 19-Aug-17
 */

class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    private final Vector<Player> players;

    /**
     * Provide a reference to the views for each data item. Complex data items may need more than
     * one view per item, and you provide access to all the views for a data item in a view holder.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        final TextView nameTextView;
        final TextView algorithmTextView;
        final TextView colorTextView;

        ViewHolder(final View view) {
            super(view);
            nameTextView = view.findViewById(R.id.item_player_name);
            algorithmTextView = view.findViewById(R.id.item_player_algorithm);
            colorTextView = view.findViewById(R.id.item_player_color);
        }
    }

    PlayerAdapter () {
        this.players = new Vector<>();
    }

    void add(final Player player) {
        players.add(player);
    }

    Vector<Player> getPlayers() {
        return new Vector<>(players);
    }

    int getSize() {
        return players.size();
    }

    boolean isEmpty() {
        return players.isEmpty();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlayerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);

        // set the view's size, margins, paddings and layout parameters

        return  new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Player player = players.elementAt(position);
        holder.nameTextView.setText(player.getName());
        holder.algorithmTextView.setText(player.getMazeSolverClass().getSimpleName());
        holder.colorTextView.setText(player.getColor().getName());
        holder.colorTextView.setTextColor(Color.parseColor("#" + Integer.toHexString(Color.parseColor(player.getColor().getCode()))));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return players.size();
    }
}