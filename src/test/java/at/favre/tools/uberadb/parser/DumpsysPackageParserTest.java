package at.favre.tools.uberadb.parser;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

import static junit.framework.TestCase.assertEquals;

public class DumpsysPackageParserTest {

    private String dumpsysExample1Content;
    private String dumpsysExample2Content;
    private String dumpsysExample3Content;
    private String dumpsysExample4Content;
    private String testPackage = "com.example.testapp";

    @Before
    public void setup() throws Exception {
        File dumpsysExample1 = new File(getClass().getClassLoader().getResource("test-files/dumpsys-package-nexus6p.txt").toURI().getPath());
        dumpsysExample1Content = new String(Files.readAllBytes(dumpsysExample1.toPath()), "UTF-8");
        File dumpsysExample2 = new File(getClass().getClassLoader().getResource("test-files/dumpsys-packages-emu-android18.txt").toURI().getPath());
        dumpsysExample2Content = new String(Files.readAllBytes(dumpsysExample2.toPath()), "UTF-8");
        File dumpsysExample3 = new File(getClass().getClassLoader().getResource("test-files/dumpsys-packages-emu-android19.txt").toURI().getPath());
        dumpsysExample3Content = new String(Files.readAllBytes(dumpsysExample3.toPath()), "UTF-8");
        File dumpsysExample4 = new File(getClass().getClassLoader().getResource("test-files/dumpsys-packages-emu-android23.txt").toURI().getPath());
        dumpsysExample4Content = new String(Files.readAllBytes(dumpsysExample4.toPath()), "UTF-8");
    }

    @Test
    public void testExample1() throws Exception {
        DumpsysPackageParser packageParser = new DumpsysPackageParser();
        DumpsysPackageParser.PackageInfo info = packageParser.parseSingleDumpsysPackage(testPackage, dumpsysExample1Content);
        assertEquals(new DumpsysPackageParser.PackageInfo(testPackage, 1, "1.0", "/data/app/com.example.testapp.first-1", "2016-11-03 01:12:09", "2016-11-03 01:12:09"), info);
    }

    @Test
    public void testExample2() throws Exception {
        DumpsysPackageParser packageParser = new DumpsysPackageParser();
        DumpsysPackageParser.PackageInfo info = packageParser.parseSingleDumpsysPackage(testPackage, dumpsysExample2Content);
        assertEquals(new DumpsysPackageParser.PackageInfo(testPackage, 1, "1.0", "/data/app/com.example.testapp.first-1.apk", "2016-11-05 12:10:40", "2016-11-05 12:10:40"), info);
    }

    @Test
    public void testExample3() throws Exception {
        DumpsysPackageParser packageParser = new DumpsysPackageParser();
        DumpsysPackageParser.PackageInfo info = packageParser.parseSingleDumpsysPackage(testPackage, dumpsysExample3Content);
        assertEquals(new DumpsysPackageParser.PackageInfo(testPackage, 1, "1.0", "/data/app/com.example.testapp.first-1.apk", "2016-11-05 12:16:06", "2016-11-05 12:16:06"), info);
    }

    @Test
    public void testExample4() throws Exception {
        DumpsysPackageParser packageParser = new DumpsysPackageParser();
        DumpsysPackageParser.PackageInfo info = packageParser.parseSingleDumpsysPackage(testPackage, dumpsysExample4Content);
        assertEquals(new DumpsysPackageParser.PackageInfo(testPackage, 1, "1.0", "/data/app/com.example.testapp.first-1", "2016-11-05 16:22:57", "2016-11-05 16:22:57"), info);
    }


}