package me.hsgamer.topper.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TopFormatter {
    private static Supplier<String> nullValueSupplier = () -> "";
    private static Supplier<String> nullUuidSupplier = () -> "";
    private final Map<String, BiFunction<UUID, BigDecimal, String>> replacers = new HashMap<>();
    private String displayName = "";
    private String prefix = "";
    private String suffix = "";
    private int fractionDigits = -1;
    private char decimalSeparator = '.';
    private char groupSeparator = ',';
    private boolean showGroupSeparator = false;
    private DecimalFormat format;

    public TopFormatter(Map<String, Object> map) {
        Optional.ofNullable(map.get("display-name")).ifPresent(s -> displayName = String.valueOf(s));
        Optional.ofNullable(map.get("prefix")).ifPresent(s -> prefix = String.valueOf(s));
        Optional.ofNullable(map.get("suffix")).ifPresent(s -> suffix = String.valueOf(s));
        Optional.ofNullable(map.get("fraction-digits")).ifPresent(s -> fractionDigits = Integer.parseInt(String.valueOf(s)));
        Optional.ofNullable(map.get("decimal-separator")).ifPresent(s -> decimalSeparator = String.valueOf(s).charAt(0));
        Optional.ofNullable(map.get("group-separator")).ifPresent(s -> groupSeparator = String.valueOf(s).charAt(0));
        Optional.ofNullable(map.get("show-group-separator")).ifPresent(s -> showGroupSeparator = Boolean.parseBoolean(String.valueOf(s)));
    }

    public TopFormatter() {
        // EMPTY
    }

    public static void setNullValueSupplier(Supplier<String> supplier) {
        nullValueSupplier = supplier;
    }

    public static void setNullUuidSupplier(Supplier<String> supplier) {
        nullUuidSupplier = supplier;
    }

    public void addReplacer(String key, BiFunction<UUID, BigDecimal, String> replacer) {
        replacers.put(key, replacer);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getFractionDigits() {
        return fractionDigits;
    }

    public void setFractionDigits(int fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    public char getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(char decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public char getGroupSeparator() {
        return groupSeparator;
    }

    public void setGroupSeparator(char groupSeparator) {
        this.groupSeparator = groupSeparator;
    }

    public boolean isShowGroupSeparator() {
        return showGroupSeparator;
    }

    public void setShowGroupSeparator(boolean showGroupSeparator) {
        this.showGroupSeparator = showGroupSeparator;
    }

    private DecimalFormat getFormat() {
        if (format == null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(decimalSeparator);
            symbols.setGroupingSeparator(groupSeparator);

            format = new DecimalFormat();
            format.setRoundingMode(RoundingMode.HALF_EVEN);
            format.setGroupingUsed(showGroupSeparator);
            format.setMinimumFractionDigits(0);
            if (fractionDigits >= 0) {
                format.setMaximumFractionDigits(fractionDigits);
            }
            format.setDecimalFormatSymbols(symbols);
        }
        return format;
    }

    public String format(BigDecimal value) {
        return getFormat().format(value);
    }

    public String replace(String text, UUID uuid, BigDecimal value) {
        String replaced = text.replace("{prefix}", prefix)
                .replace("{suffix}", suffix)
                .replace("{uuid}", uuid != null ? uuid.toString() : nullUuidSupplier.get())
                .replace("{value}", value != null ? format(value) : nullValueSupplier.get())
                .replace("{value_raw}", value != null ? value.toPlainString() : nullValueSupplier.get())
                .replace("{display_name}", displayName);
        for (Map.Entry<String, BiFunction<UUID, BigDecimal, String>> entry : replacers.entrySet()) {
            replaced = replaced.replace("{" + entry.getKey() + "}", entry.getValue().apply(uuid, value));
        }
        return replaced;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("prefix", prefix);
        map.put("suffix", suffix);
        map.put("fraction-digits", fractionDigits);
        map.put("decimal-separator", decimalSeparator);
        map.put("group-separator", groupSeparator);
        map.put("show-group-separator", showGroupSeparator);
        return map;
    }
}
