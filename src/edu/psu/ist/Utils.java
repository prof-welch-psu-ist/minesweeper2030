package edu.psu.ist;

import io.vavr.collection.List;

public final class Utils {
    private Utils() {}

    public static <A> List<A> ofAll(A...as) {
        return List.of(as);
    }

    /**
     * Returns true only if text {@code s} contains a valid
     * number (positive or negative). Basically a predicate
     * wrapping {@link Integer#parseInt(String)}.
     */
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException _) {
            return false;
        }
    }
}
