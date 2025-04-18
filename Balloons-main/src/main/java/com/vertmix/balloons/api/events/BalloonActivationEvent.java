package com.vertmix.balloons.api.events;

import com.vertmix.balloons.models.Balloon;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a balloon is activated.

 */
public class BalloonActivationEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Balloon balloon;
    private boolean cancelled;

    /**
     * Constructs a new BalloonActivationEvent.
     *
     * @param player  The player who activated the balloon.
     * @param balloon The balloon that was activated.
     */
    public BalloonActivationEvent(Player player, Balloon balloon) {
        this.player = player;
        this.balloon = balloon;
    }

    public Player getPlayer() {
        return player;
    }

    public Balloon getBalloon() {
        return balloon;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
