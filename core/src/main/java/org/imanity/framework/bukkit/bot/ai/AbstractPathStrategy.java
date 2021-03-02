package org.imanity.framework.bukkit.bot.ai;

public abstract class AbstractPathStrategy implements PathStrategy {
    private CancelReason cancelReason;
    private final TargetType type;

    protected AbstractPathStrategy(TargetType type) {
        this.type = type;
    }

    public void clearCancelReason() {
        this.cancelReason = null;
    }

    public CancelReason getCancelReason() {
        return this.cancelReason;
    }

    public TargetType getTargetType() {
        return this.type;
    }

    protected void setCancelReason(CancelReason reason) {
        this.cancelReason = reason;
    }
}
