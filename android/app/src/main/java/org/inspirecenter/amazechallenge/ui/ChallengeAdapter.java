package org.inspirecenter.amazechallenge.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.Challenge;

import java.util.Collection;
import java.util.Vector;

/**
 * @author Nearchos
 *         Created: 19-Aug-17
 */

class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {

    interface OnChallengeSelectedListener {
        void onChallengeSelected(final Challenge challenge);
    }

    private final Vector<Challenge> challenges;

    /**
     * Provide a reference to the views for each data item. Complex data items may need more than
     * one view per item, and you provide access to all the views for a data item in a view holder.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        final TextView mazeNameTextView;
        Challenge challenge;

        ViewHolder(final View view, final OnChallengeSelectedListener onChallengeSelectedListener) {
            super(view);
            this.mazeNameTextView = view.findViewById(R.id.item_challenge_name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    onChallengeSelectedListener.onChallengeSelected(challenge);
                }
            });
        }
    }

    private final OnChallengeSelectedListener onChallengeSelectedListener;

    ChallengeAdapter(final OnChallengeSelectedListener onChallengeSelectedListener) {
        this.challenges = new Vector<>();
        this.onChallengeSelectedListener = onChallengeSelectedListener;
    }

    void add(final Challenge challenge) {
        challenges.add(challenge);
    }

    void addAll(final Collection<Challenge> challenges) {
        this.challenges.addAll(challenges);
    }

    void clear() {
        this.challenges.clear();
    }

    Vector<Challenge> getChallenges() {
        return new Vector<>(challenges);
    }

    int getSize() {
        return challenges.size();
    }

    boolean isEmpty() {
        return challenges.isEmpty();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChallengeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge, parent, false);

        // set the view's size, margins, paddings and layout parameters

        return  new ViewHolder(view, onChallengeSelectedListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Challenge challenge = challenges.elementAt(position);
        holder.challenge = challenge;
        holder.mazeNameTextView.setText(challenge.getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return challenges.size();
    }
}