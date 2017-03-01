package at.favre.tools.uberadb;

import org.junit.Test;

import static org.junit.Assert.fail;

public class AdbLocationFinderImplTest {

    @Test
    public void testLocation() {
        AdbLocationFinderImpl locationFinder = new AdbLocationFinderImpl();
        try {
            locationFinder.find(
                    new MockAdbCmdProvider(false),
                    null
            );
            fail();
        } catch (Exception e) {
        }
    }

}
