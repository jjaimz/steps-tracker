package com.steps.api;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DatetimeService {
    public static long dateTimeToEpoch(String formattedDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(formattedDate, formatter);
        ZonedDateTime utcDateTime = offsetDateTime.toZonedDateTime().toInstant().atZone(ZoneOffset.UTC);
        Instant instant = utcDateTime.toInstant();
        return instant.getEpochSecond();
    }

    public static String currentDateTime() {
        LocalDateTime currentTime = LocalDateTime.now();

        ZoneOffset zoneOffset = ZoneOffset.from(OffsetDateTime.now());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        return currentTime.atOffset(zoneOffset).format(formatter);
    }

    public static String epochtoDateTime(long epoch) {
        ZoneOffset zoneOffset = ZoneOffset.from(OffsetDateTime.now());
        Instant instant = Instant.ofEpochSecond(epoch);
        OffsetDateTime offsetDateTime = instant.atOffset(zoneOffset);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        return offsetDateTime.format(formatter);
    }
}
