package edu.psu.ist;

import edu.psu.ist.immutableadts.Result;

/**
 * A (stateless -- re: no fields) utility class that wraps a
 * bunch of board deserialization related static methods.
 */
public final class BoardLoader {

    // not a singleton, but there's no reason to have an instance
    // of this class since it just exports static methods
    // (and we don't need an instance to invoke public static methods).
    private BoardLoader() {}

    //public static Result
}
