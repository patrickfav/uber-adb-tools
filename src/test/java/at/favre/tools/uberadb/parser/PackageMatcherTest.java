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

import static org.junit.Assert.*;

public class PackageMatcherTest {

    @Test
    public void testMatches() throws Exception {
        assertTrue(PackageMatcher.match("com.*", "com.example.android.livecubes"));
        assertFalse(PackageMatcher.match("com.*", "de.example.android.livecubes"));
        assertTrue(PackageMatcher.match("com.*.android.*", "com.example.android.livecubes"));
        assertFalse(PackageMatcher.match("com.*.android", "com.example.android.livecubes"));
        assertTrue(PackageMatcher.match("com.*.android", "com.example.android"));
        assertTrue(PackageMatcher.match("com.example.android.*", "com.example.android.livecubes"));
        assertTrue(PackageMatcher.match("com.example.android*", "com.example.android.livecubes"));
        assertTrue(PackageMatcher.match("com.example.android*", "com.example.android"));
        assertFalse(PackageMatcher.match("com.example.android.*", "com.example.android"));
        assertFalse(PackageMatcher.match("com.example.android.*", "com.example.android"));
    }

    @Test
    public void testParseFilterArg() throws Exception {
        String[] filters1 = PackageMatcher.parseFiltersArg("com.android.*");
        assertTrue(filters1.length == 1);
        assertEquals("com.android.*", filters1[0]);

        String[] filters2 = PackageMatcher.parseFiltersArg("com.android.*,at.test.*");
        assertTrue(filters2.length == 2);
        assertEquals("com.android.*", filters2[0]);
        assertEquals("at.test.*", filters2[1]);
    }
}
