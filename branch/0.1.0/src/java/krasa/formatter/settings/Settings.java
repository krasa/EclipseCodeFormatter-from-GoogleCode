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

/**
 * @author Esko Luontola
 * @since 4.12.2007
 */
public class Settings implements Cloneable {

	public static enum Formatter {
		DEFAULT, ECLIPSE
	}

	@NotNull
	private Formatter formatter = Formatter.DEFAULT;
	@NotNull
	private String eclipsePrefs = "";
	@NotNull
	private String lineSeparator = "\\n";

	@NotNull
	public final Settings clone() {
		try {
			return (Settings) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@NotNull
	public String getLineSeparator() {
		return lineSeparator;
	}

	public void setLineSeparator(@NotNull String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	@NotNull
	public Formatter getFormatter() {
		return formatter;
	}

	public void setFormatter(@NotNull Formatter formatter) {
		this.formatter = formatter;
	}

	@NotNull
	public String getEclipsePrefs() {
		return eclipsePrefs;
	}

	public void setEclipsePrefs(@NotNull String eclipsePrefs) {
		this.eclipsePrefs = eclipsePrefs;
	}
}
