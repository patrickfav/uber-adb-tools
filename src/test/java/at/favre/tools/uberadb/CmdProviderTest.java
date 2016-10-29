package at.favre.tools.uberadb;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CmdProviderTest {

    CmdProvider cmdProvider;

    @Before
    public void setup() {
        cmdProvider = new CmdProvider.DefaultCmdProvider();
    }

    @Test
    public void testCanRunCommand() throws Exception {
        assertFalse("should not be able to run random", cmdProvider.canRunCmd(new String[]{"Thisadhpiwadahdjsahduhduwaheuawez27371236"}));
        assertTrue("should be able to run cmd 'java -version'", cmdProvider.canRunCmd(new String[]{"java", "-version"}));
    }
}
