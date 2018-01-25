package org.inspirecenter.amazechallenge.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.Sound;

import java.util.HashMap;


public class SoundPlayer {

    private static SoundPool soundPool;
    private static HashMap soundPoolMap;

    private static void initSounds(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap(3);
        soundPoolMap.put( Sound.SOUND_BOMB_1, soundPool.load(context, R.raw.bomb1, 1) );
        soundPoolMap.put( Sound.SOUND_BOMB_2, soundPool.load(context, R.raw.bomb2, 2) );
    }

    public static void playSound(Context context, int soundID) {
        if(soundPool == null || soundPoolMap == null) initSounds(context);
        float volume = 1.0f;
        soundPool.play((int)soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
    }

}
