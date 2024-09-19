package edu.psu.ist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// can rename as preferred
// (your @Test methods for the various steps go in here)
public class SweeperTests {

    @Test public void test01() {
        String theActualStr = "I'm a string"; // pretend a fn returned this or something
        Assertions.assertEquals("I'm a string", theActualStr);
    }
}
