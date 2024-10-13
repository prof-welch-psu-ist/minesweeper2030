package edu.psu.ist.immutableadts;

import io.vavr.control.Option;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/** A maybe/option type modeled after the one found in haskell. */
public sealed interface Maybe<A> {

    static <U> Maybe<U> fromOpt(Option<U> vavrOpt) {
        return vavrOpt.isDefined() ? of(vavrOpt.get()) : none();
    }

    static <U> Maybe<U> fromOpt(Optional<U> javaOpt) {
        return javaOpt.isPresent() ? of(javaOpt.get()) : none();
    }

    // factory methods
    static <T> Maybe<T> of(T value) {
        return (value == null) ? none() : new Some<>(value);
    }

    @SuppressWarnings("unchecked") static <T> Maybe<T> none() {
        return (None<T>) None.INSTANCE;
    }

    static <T> Maybe<T> fromOption(Option<T> o) {
        if (o.isDefined()) {
            return Maybe.of(o.get());
        }
        return Maybe.none();
    }

    default <B> Maybe<B> flatMap(Function<A, Maybe<B>> f) {
        return switch (this) {
            case Some(var v) -> f.apply(v);
            case None<A> _   -> Maybe.none();
        };
    }

    // fmap: 'functorMap'
    default <B> Maybe<B> map(Function<A, B> f) {
        Objects.requireNonNull(f, "fn is null");

        return switch (this) {
            case Maybe.None<?> _ -> none();
            case Some(var x) -> new Some<>(f.apply(x));
        };
    }

    default A getOrElse(A other) {
        return isEmpty() ? other : get();
    }

    default A getOrElse(Supplier<A> supplier) {
        return isEmpty() ? supplier.get() : get();
    }

    A get();

    default boolean isEmpty() {
        return this instanceof Maybe.None<A>;
    }

    default boolean nonEmpty() {
        return this instanceof Maybe.Some<A>;
    }

    default boolean isDefined() {
        return nonEmpty();
    }

    /**
     * Returns true if this maybe is nonempty and predicate {@code p} returns
     * true when applied to this maybe's value.
     */
    default boolean exists(Predicate<A> p) {
        return !isEmpty() && p.test(this.get());
    }

    default boolean contains(A item) {
        return switch (this) {
            case Some(var x) -> x.equals(item);
            case None<A> _ -> false;
        };
    }

    final class None<A> implements Maybe<A> {
        public static final None<?> INSTANCE = new None<>();

        private None() {
        }

        @Override public A get() {
            throw new NoSuchElementException("option is empty");
        }

        @Override public boolean equals(Object o) {
            return o == this;
        }

        @Override public int hashCode() {
            return 1;
        }
    }

    record Some<A>(A value) implements Maybe<A> {
        @Override public A get() {
            return value;
        }

        @Override public boolean equals(Object o) {
            return o instanceof Maybe.Some<?> om &&
                    this.value.equals(om.value);
        }

        @Override public int hashCode() {
            return Objects.hashCode(value);
        }
    }


}