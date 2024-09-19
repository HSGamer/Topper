package me.hsgamer.topper.placeholderleaderboard.notification;

import java.util.UUID;

public interface UpdateNotificationConsumer {
    void accept(String holderGroup, String holderName, UUID uuid, Double value);
}
