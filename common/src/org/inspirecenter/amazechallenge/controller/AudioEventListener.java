package org.inspirecenter.amazechallenge.controller;

import org.inspirecenter.amazechallenge.model.Pickable;

public interface AudioEventListener {

    public void onAudioEvent(final Pickable pickable);
}