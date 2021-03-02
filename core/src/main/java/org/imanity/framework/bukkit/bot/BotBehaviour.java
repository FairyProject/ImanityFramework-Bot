package org.imanity.framework.bukkit.bot;

import org.imanity.framework.bukkit.bot.ai.NavigatorCallback;

import java.util.List;

public interface BotBehaviour {

    boolean avoidWater();

    double pathDistanceMargin(); // 1.0D

    float speed(); // 1.0D

    List<NavigatorCallback> callbacks();

    int range();

}
