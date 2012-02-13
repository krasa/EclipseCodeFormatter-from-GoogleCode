/*
 * External Code Formatter
 * Copyright (c) 2007-2009  Esko Luontola, www.orfjackal.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package krasa.formatter.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Builds a CodeFormatter based on the {@link Settings}.
 *
 * @author Esko Luontola
 * @since 4.12.2007
 */
public class SettingsManager {

    private static final String WHITESPACE = "\\s+";

    @Nullable
    public static void verify(@NotNull Settings settings)
            throws IllegalSettingsException {
        if (settings.getFormatter().equals(Settings.Formatter.ECLIPSE)) {
            mustNotBeEmpty(settings.getEclipsePrefs(), "settings.eclipsePrefs");

            File eclipsePrefs = new File(settings.getEclipsePrefs());

            fileMustExist(eclipsePrefs, "settings.eclipsePrefs");

            assert eclipsePrefs.isFile() : "Not a file: " + eclipsePrefs;
        }
    }

    private static void mustNotBeEmpty(@NotNull String s, @NotNull String field)
            throws IllegalSettingsException {
        if (isEmpty(s)) {
            throw new IllegalSettingsException(field, "error.emptyField");
        }
    }

    private static void fileMustExist(@NotNull File file, @NotNull String field)
            throws IllegalSettingsException {
        if (!file.isFile()) {
            throw new IllegalSettingsException(field, "error.noSuchFile",
                    file.toString());
        }
    }

    private static boolean isEmpty(@NotNull String s) {
        return s.trim().length() == 0;
    }
}
