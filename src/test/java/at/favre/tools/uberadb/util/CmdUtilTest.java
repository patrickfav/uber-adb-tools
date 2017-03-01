package at.favre.tools.uberadb.util;

import at.favre.tools.uberadb.MockAdbCmdProvider;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CmdUtilTest {
    @Test
    public void testOs() {
        assertNotNull(CmdUtil.getOsType());
    }

    @Test
    public void testPathShouldBeNull() {
        assertNull(CmdUtil.checkAndGetFromPATHEnvVar(
                new MockAdbCmdProvider(false)
                , "shouldNotExist"));
    }
}
