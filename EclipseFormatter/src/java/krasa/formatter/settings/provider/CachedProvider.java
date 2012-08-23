package krasa.formatter.settings.provider;

import krasa.formatter.common.ModifiableFile;

import java.io.File;

/**
 * @author Vojtech Krasa
 */
public abstract class CachedProvider<T> {
	private ModifiableFile modifiableFile;
	private ModifiableFile.Monitor lastState;
	private T cachedValue;

	protected CachedProvider(ModifiableFile modifiableFile) {
		this.modifiableFile = modifiableFile;
	}

	protected abstract T readFile(File file);

	public T get() {
		if (cachedValue == null || modifiableFile.wasChanged(lastState)) {
			saveLastModified();
			cachedValue = readFile(modifiableFile);
		}
		return cachedValue;
	}

	public ModifiableFile.Monitor getModifiedMonitor() {
		return modifiableFile.getModifiedMonitor();
	}

	public boolean wasChanged(ModifiableFile.Monitor lastState) {
		return lastState == null || modifiableFile.wasChanged(lastState);
	}

	private void saveLastModified() {
		lastState = modifiableFile.getModifiedMonitor();
	}
}
