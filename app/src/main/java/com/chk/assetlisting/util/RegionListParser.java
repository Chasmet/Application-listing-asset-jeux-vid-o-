package com.chk.assetlisting.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegionListParser {
    private static final Pattern LEADING_MARKER = Pattern.compile("^\\s*(?:(?:[-•*]+)|(?:\\d{1,3}\\s*[.)\\-:]?))\\s*");
    private static final Pattern NUMBER_MARKER = Pattern.compile("(?:^|\\s)(\\d{1,3})\\s*[.)\\-:]?\\s+");

    private RegionListParser() {}

    public static List<String> parse(String raw) {
        List<String> result = new ArrayList<>();
        if (raw == null) return result;

        String normalized = raw.replace('\r', '\n').replaceAll("\\n+", "\n").trim();
        if (normalized.isEmpty()) return result;

        String[] lines = normalized.split("\\n|;");
        if (lines.length > 1) {
            for (String line : lines) addClean(result, line);
        } else {
            List<String> numbered = parseSequentialNumberedList(normalized);
            if (numbered.size() >= 2) result.addAll(numbered);
            else addClean(result, normalized);
        }

        Set<String> seen = new LinkedHashSet<>();
        List<String> unique = new ArrayList<>();
        for (String name : result) {
            String key = comparisonKey(name);
            if (!key.isEmpty() && seen.add(key)) unique.add(name);
        }
        return unique;
    }

    private static List<String> parseSequentialNumberedList(String text) {
        List<String> result = new ArrayList<>();
        Matcher matcher = NUMBER_MARKER.matcher(text);
        int expected = 1;
        int contentStart = -1;

        while (matcher.find()) {
            int number;
            try {
                number = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                continue;
            }
            if (number != expected) continue;

            if (contentStart >= 0) addClean(result, text.substring(contentStart, matcher.start()));
            contentStart = matcher.end();
            expected++;
        }

        if (contentStart >= 0) addClean(result, text.substring(contentStart));
        return result;
    }

    private static void addClean(List<String> output, String value) {
        if (value == null) return;
        String clean = LEADING_MARKER.matcher(value).replaceFirst("").trim();
        clean = clean.replaceAll("^[,.;:]+|[,.;:]+$", "").trim();
        if (!clean.isEmpty()) output.add(clean);
    }

    public static String comparisonKey(String value) {
        if (value == null) return "";
        String withoutOrder = LEADING_MARKER.matcher(value.trim()).replaceFirst("");
        return Normalizer.normalize(withoutOrder.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
    }
}
