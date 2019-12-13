package com.fax.showdt.manager.musicPlug;

import android.media.session.PlaybackState;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-8-7
 * Description:
 */
public interface PlayState {

    /**
     * State indicating this item is currently stopped.
     *
     * @see PlaybackState.Builder#setState
     */
    public final static int STATE_STOPPED = 1;

    /**
     * State indicating this item is currently paused.
     *
     * @see PlaybackState.Builder#setState
     */
    public final static int STATE_PAUSED = 2;

    /**
     * State indicating this item is currently playing.
     *
     * @see PlaybackState.Builder#setState
     */
    public final static int STATE_PLAYING = 3;

    /**
     * State indicating this item is currently fast forwarding.
     *
     * @see PlaybackState.Builder#setState
     */
    public final static int STATE_FAST_FORWARDING = 4;

    /**
     * State indicating this item is currently rewinding.
     *
     * @see PlaybackState.Builder#setState
     */
    public final static int STATE_REWINDING = 5;

    /**
     * State indicating this item is currently buffering and will begin playing
     * when enough data has buffered.
     *
     * @see PlaybackState.Builder#setState
     */
    public final static int STATE_BUFFERING = 6;

    /**
     * State indicating this item is currently in an error state. The error
     * message should also be set when entering this state.
     *
     * @see PlaybackState.Builder#setState
     */
    public final static int STATE_ERROR = 7;

    /**
     * State indicating the class doing playback is currently connecting to a
     * new destination.  Depending on the implementation you may return to the previous
     * If the connection failed {@link #STATE_ERROR} should be used.
     *
     * @see PlaybackState.Builder#setState
     */
    public final static int STATE_CONNECTING = 8;

    /**
     * State indicating the player is currently skipping to the previous item.
     *
     * @see PlaybackState.Builder#setState
     */
    public final static int STATE_SKIPPING_TO_PREVIOUS = 9;

    /**
     * State indicating the player is currently skipping to the next item.
     *
     * @see PlaybackState.Builder#setState
     */
    public final static int STATE_SKIPPING_TO_NEXT = 10;

    /**
     * State indicating the player is currently skipping to a specific item in
     * the queue.
     *
     * @see PlaybackState.Builder#setState
     */
    public final static int STATE_SKIPPING_TO_QUEUE_ITEM = 11;

    /**
     * Use this value for the position to indicate the position is not known.
     */
    public final static long PLAYBACK_POSITION_UNKNOWN = -1;

}
