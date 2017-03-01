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
