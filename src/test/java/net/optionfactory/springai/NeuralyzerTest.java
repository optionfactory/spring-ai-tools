package net.optionfactory.springai;

import com.knuddels.jtokkit.api.ModelType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NeuralyzerTest {


    @Test
    public void testTruncation() {
        final var loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur. Duis aute irure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        final var truncatedLoremIpsum = Neuralyzer.forget(loremIpsum, 50);
        assertTrue(truncatedLoremIpsum.length() < loremIpsum.length());
    }

    @Test
    public void testTruncationWithDifferentModel() {
        final var loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur. Duis aute irure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        final var truncatedLoremIpsum = Neuralyzer.forget(loremIpsum, 50, ModelType.GPT_4);
        assertTrue(truncatedLoremIpsum.length() < loremIpsum.length());
    }

    @Test
    public void doNotTruncateIfAlreadyUnderLimit() {
        final var loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur. Duis aute irure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        final var truncatedLoremIpsum = Neuralyzer.forget(loremIpsum, 5000);
        assertEquals(loremIpsum, truncatedLoremIpsum);
    }

}
