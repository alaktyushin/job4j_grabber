package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static java.util.Map.entry;

public class SqlRuDateTimeParser implements DateTimeParser {

    private static final Map<String, String> MONTHS = Map.ofEntries(
            entry("янв", "Jan"),
            entry("фев", "Feb"),
            entry("мар", "Mar"),
            entry("апр", "Apr"),
            entry("май", "May"),
            entry("июн", "Jun"),
            entry("июл", "Jul"),
            entry("авг", "Aug"),
            entry("сен", "Sep"),
            entry("окт", "Oct"),
            entry("ноя", "Nov"),
            entry("дек", "Dec")
    );

    @Override
    public LocalDateTime parse(String parse) {
        parse = parse.replaceAll("сегодня", LocalDate.now()
                .format(DateTimeFormatter.ofPattern("d MMM yy")))
                .replaceAll("вчера", (LocalDate.now().minusDays(1))
                        .format(DateTimeFormatter.ofPattern("d MMM yy")));
        for (var m : MONTHS.entrySet()) {
            parse = parse.replace(m.getKey().toLowerCase(), m.getValue());
        }
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("d MMM yy, HH:mm");
        return LocalDateTime.parse(parse, dTF);
    }
}
