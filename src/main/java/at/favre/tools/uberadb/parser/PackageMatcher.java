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

import java.util.*;

public class PackageMatcher {
    private List<String> packages;

    public PackageMatcher(List<String> packages) {
        this.packages = packages;
    }

    public static String[] parseFiltersArg(String arg) {
        if (arg != null && !arg.isEmpty()) {
            return arg.split(",");
        }
        throw new IllegalArgumentException("unexpected arg: " + arg);
    }

    public List<String> findMatches(String... moreFilters) {
        Set<String> matchedPackages = new HashSet<>();

        List<String> filters = new ArrayList<>();
        if (moreFilters != null && moreFilters.length > 0) {
            filters.addAll(Arrays.asList(moreFilters));
        }

        for (String aPackage : packages) {
            for (String filter : filters) {
                if (match(filter, aPackage)) {
                    matchedPackages.add(aPackage);
                }
            }
        }
        List<String> list = new ArrayList<>(matchedPackages);
        Collections.sort(list);
        return list;
    }

    static boolean match(String filter, String aPackage) {
        if (filter == null || filter.isEmpty()) {
            return false;
        } else {
            String escapedFilterString = "^" + filter.replace(".", "\\Q.\\E").replace("*", ".*");
            return aPackage.matches(escapedFilterString);
        }
    }
}
