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
