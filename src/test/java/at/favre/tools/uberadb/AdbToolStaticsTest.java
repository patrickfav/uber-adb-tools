package at.favre.tools.uberadb;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AdbToolStaticsTest {

    @Test
    public void testCheckQuickInstall() throws Exception {
        assertNull(AdbTool.checkIfIsQuickInstall(new String[0]));
        assertNotNull(AdbTool.checkIfIsQuickInstall(new String[]{new File(getClass().getClassLoader().getResource("apks").toURI().getPath()).listFiles()[0].getAbsolutePath()}));
    }

}
