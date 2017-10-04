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

            return result.exception == null;
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
            this.cmd = CmdUtil.concat(cmd, " ");
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
