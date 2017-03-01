package at.favre.tools.uberadb;

import at.favre.tools.uberadb.actions.Commons;
import at.favre.tools.uberadb.ui.Arg;

public class MockUserPromptHandler implements UserPromptHandler {
    private boolean returnsYes;
    private boolean wasUserPrompted = false;

    public MockUserPromptHandler() {
        this(true);
    }

    public MockUserPromptHandler(boolean returnsYes) {
        this.returnsYes = returnsYes;
    }

    @Override
    public boolean promptUser(Commons.ActionResult actionResult, Arg arguments) {
        wasUserPrompted = true;
        return returnsYes;
    }

    public boolean isWasUserPrompted() {
        return wasUserPrompted;
    }
}
