package at.favre.tools.uberadb;

import at.favre.tools.uberadb.util.CmdUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public interface CmdProvider {
    /**
     * Runs the provided command and waits for its result
     *
     * @param args tokenized
     * @return the result
     */
    Result runCmd(String[] args);

    /**
     * @param cmd tokenized
     * @return true if the command could be run without exceptions
     */
    boolean canRunCmd(String[] cmd);

    /**
     * @return all previous run commands
     */
    List<Result> getHistory();

    class DefaultCmdProvider implements CmdProvider {

        List<Result> history = new ArrayList<>();

        @Override
        public Result runCmd(String[] args) {
            StringBuilder logStringBuilder = new StringBuilder();
            Exception exception = null;
            int exitValue = -1;
            try {
                ProcessBuilder pb = new ProcessBuilder(args);
                pb.redirectErrorStream(true);
                Process process = pb.start();
                try (BufferedReader inStreamReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String s;
                    while ((s = inStreamReader.readLine()) != null) {
                        if (!s.isEmpty()) logStringBuilder.append(s).append("\n");
                    }
                }
                process.waitFor();
                exitValue = process.exitValue();
            } catch (Exception e) {
                exception = e;
            }
            Result r = new Result(logStringBuilder.toString(), exception, args, exitValue);
            history.add(r);
            return r;
        }

        @Override
        public boolean canRunCmd(String[] cmd) {
            Result result = runCmd(cmd);
            history.add(result);

            if (result.exception != null) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public List<Result> getHistory() {
            return history;
        }
    }

    class Result {
        public final Exception exception;
        public final String out;
        public final String cmd;
        public final int exitValue;

        public Result(String out, Exception exception, String[] cmd, int exitValue) {
            this.out = out;
            this.exception = exception;
            this.cmd = CmdUtil.toPlainString(cmd);
            this.exitValue = exitValue;
        }

        @Override
        public String toString() {
            return "command: " + cmd + "\n" + out + "\nexit value (" + exitValue + ")\n";
        }

        public boolean isSuccess() {
            return exitValue == 0;
        }
    }
}
