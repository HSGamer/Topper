package me.hsgamer.topper.placeholderleaderboard.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UpdateNotificationManager {
    public static final String DEFAULT_HOLDER_GROUP = "topper";
    private static final List<UpdateNotificationConsumer> consumers = new ArrayList<>();

    public static void addConsumer(UpdateNotificationConsumer consumer) {
        consumers.add(consumer);
    }

    public static void removeConsumer(UpdateNotificationConsumer consumer) {
        consumers.remove(consumer);
    }

    public static void notifyConsumers(String holderGroup, String holderName, UUID uuid, Double value) {
        consumers.forEach(consumer -> consumer.accept(holderGroup, holderName, uuid, value));
    }

    public static void notifyConsumers(String holderName, UUID uuid, Double value) {
        notifyConsumers(DEFAULT_HOLDER_GROUP, holderName, uuid, value);
    }
}
