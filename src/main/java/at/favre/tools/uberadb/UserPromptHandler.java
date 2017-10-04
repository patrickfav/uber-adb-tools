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

import at.favre.tools.uberadb.actions.Commons;
import at.favre.tools.uberadb.ui.Arg;

/**
 * Used to handle user prompting a simple yes/no question
 */
public interface UserPromptHandler {

    /**
     * Will prompt the user with the context provided by the params
     *
     * @param actionResult result from action process
     * @param arguments    global arguments
     * @return true if user accepted or false otherwise
     */
    boolean promptUser(Commons.ActionResult actionResult, Arg arguments);
}
