/*
 *
 *  *  Copyright 2016 Patrick Favre-Bulle
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package at.favre.tools.uberadb.parser;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

public class InstalledPackagesParserTest {
    @Test
    public void testParseAdbOutput() throws Exception {
        List<String> packages = new InstalledPackagesParser().parse("package:/data/app/com.synology.dsphoto-1/base.apk=com.synology.dsphoto\n" +
                "package:/data/app/com.skype.raider-1/base.apk=com.skype.raider\n" +
                "package:/system/priv-app/CtsShimPrivPrebuilt/CtsShimPrivPrebuilt.apk=com.android.cts.priv.ctsshim\n" +
                "package:/data/app/com.google.android.youtube-2/base.apk=com.google.android.youtube");
        assertTrue(packages.size() == 4);
        assertEquals("com.synology.dsphoto", packages.get(0));
        assertEquals("com.skype.raider", packages.get(1));
        assertEquals("com.android.cts.priv.ctsshim", packages.get(2));
        assertEquals("com.google.android.youtube", packages.get(3));
    }

    @Test
    public void testParseAdbOutputEmpty() throws Exception {
        List<String> packages = new InstalledPackagesParser().parse("");
        assertTrue(packages.isEmpty());
    }

    @Test
    public void testParseSingleLines() throws Exception {
        assertEquals(
                "com.synology.dsphoto",
                InstalledPackagesParser.parsePackage("package:/data/app/com.synology.dsphoto-1/base.apk=com.synology.dsphoto")
        );
        assertEquals(
                "com.whatsapp",
                InstalledPackagesParser.parsePackage("package:/data/app/com.whatsapp-1/base.apk=com.whatsapp")
        );
        assertEquals(
                "com.google.android.apps.docs.editors.sheets",
                InstalledPackagesParser.parsePackage("package:/data/app/com.google.android.apps.docs.editors.sheets-1/base.apk=com.google.android.apps.docs.editors.sheets")
        );
        assertEquals(
                "com.google.android.webview",
                InstalledPackagesParser.parsePackage("package:/data/app/com.google.android.webview-1/base.apk=com.google.android.webview")
        );
    }

    @Test
    public void testParseSingleLinesInvalidPackage() throws Exception {
        assertNull(InstalledPackagesParser.parsePackage("package:/data/app/com.google.android.webview-1/base.apk=com"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseSingleLinesInvalidSyntax() throws Exception {
        InstalledPackagesParser.parsePackage("package:/data/app/com.whatsapp-1/base.apk:com.whatsapp");
    }

}
