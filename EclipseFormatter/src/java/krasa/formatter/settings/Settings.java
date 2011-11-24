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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
	private String joinedGroup = "";
	private boolean optimizeImports = true;
	private Integer notifyFromTextLenght = 300;

	public Integer getNotifyFromTextLenght() {
		return notifyFromTextLenght;
	}

	public void setNotifyFromTextLenght(Integer notifyFromTextLenght) {
		this.notifyFromTextLenght = notifyFromTextLenght;
	}

	public void setJoinedGroup(String joinedGroup) {
		this.joinedGroup = joinedGroup;
	}

	public boolean isOptimizeImports() {
		return optimizeImports;
	}

	public void setOptimizeImports(boolean optimizeImports) {
		this.optimizeImports = optimizeImports;
	}

	public String getJoinedGroup() {
		return joinedGroup;
	}

	public List<JoinedGroup> getJoinedGroupAsList() {
		if (joinedGroup == null || joinedGroup.isEmpty()) {
			return Collections.emptyList();
		}
		List<JoinedGroup> joinedGroups = new ArrayList<JoinedGroup>();
        for (String group : joinedGroup.split(";")) {
			String[] split = group.split("-");
			if (split.length != 2) {
				continue;
			}
			joinedGroups.add(JoinedGroup.from(split[0], split[1]));
		}
		return joinedGroups;
	}

	@NotNull
	public final Settings clone() {
		try {
			return (Settings) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
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
