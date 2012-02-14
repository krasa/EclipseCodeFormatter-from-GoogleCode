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

package krasa.formatter;

import com.intellij.CommonBundle;
import krasa.formatter.settings.IllegalSettingsException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * @author Esko Luontola
 * @since 18.12.2007
 */
public class Messages {

    @NonNls
    private static final String BUNDLE_NAME = "krasa.formatter.messages";

    private static Reference<ResourceBundle> bundle;

    private Messages() {
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = null;
        if (Messages.bundle != null) {
            bundle = Messages.bundle.get();
        }
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME);
            Messages.bundle = new SoftReference<ResourceBundle>(bundle);
        }
        return bundle;
    }

    public static String message(
            @PropertyKey(resourceBundle = BUNDLE_NAME) String key,
            Object... params) {
        return CommonBundle.message(getBundle(), key, params);
    }

    public static String message(IllegalSettingsException e) {
        String field = message(e.getField());
        String error = message(e.getErrorKey(), (Object[]) e.getErrorParams());
        return message("error.errorInField", field, error);
    }
}
