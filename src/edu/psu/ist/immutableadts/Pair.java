package edu.psu.ist.immutableadts;

public record Pair<T, U>(T first, U second){

    /**
     * A "static factory method" that produces pair objects
     * (a mini-design pattern). This method is parameterized by
     * two generic types {@code A} and {@code B}
     * <p>
     * This essentially allows users to construct a pair object
     * by writing:
     * <pre><code>
     *     Pair.of(3, "cat")
     * </code></pre>
     * as opposed to:
     * <pre><code>
     *     new Pair<>(3, "cat")
     * </code></pre>
     */
    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }

    @Override public String toString() {
        return String.format("(%s, %s)", first, second);
    }
}
