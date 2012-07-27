package org.signalml.plugin.method.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.config.preset.PresetManagerEvent;
import org.signalml.app.config.preset.PresetManagerListener;

public class PluginPresetManagerFilter implements PresetManager {

	private class _Listener implements PresetManagerListener {

		private PluginPresetManagerFilter filterPresetManager;

		public _Listener(PluginPresetManagerFilter filterPresetManager) {
			this.filterPresetManager = filterPresetManager;
		}

		@Override
		public void presetAdded(PresetManagerEvent ev) {
			this.filterPresetManager.resetState();
		}

		@Override
		public void presetRemoved(PresetManagerEvent ev) {
			this.filterPresetManager.resetState();
		}

		@Override
		public void presetReplaced(PresetManagerEvent ev) {
			this.filterPresetManager.resetState();
		}

		@Override
		public void defaultPresetChanged(PresetManagerEvent ev) {
			this.filterPresetManager.resetState();
		}
		
	}
	
	private PresetManager delegate;
	private Collection<Class<? extends Preset>> allowedPresetClassCollection;
	private boolean needsRefreshFlag;
	private Map<Preset, Integer> filteredPresetToPositionMap;
	private List<Preset> filteredPresets;

	
	public PluginPresetManagerFilter(PresetManager delegate, Collection<Class<? extends Preset>> allowedPresetClassCollection) {
		this.delegate = delegate;
		this.allowedPresetClassCollection = allowedPresetClassCollection;
		
		this.filteredPresetToPositionMap = new HashMap<Preset, Integer>(this.delegate.getPresetCount());
		this.filteredPresets = new ArrayList<Preset>();
		
		this.needsRefreshFlag = true;
		
		this.delegate.addPresetManagerListener(new _Listener(this));
	}
	
	@Override
	public Class<?> getPresetClass() {
		return this.delegate.getPresetClass();
	}

	@Override
	public int getPresetCount() {
		this.refreshStateIfNeeded();

		return this.filteredPresets.size();
	}

	@Override
	public Preset[] getPresets() {
		this.refreshStateIfNeeded();
		
		return this.filteredPresets.toArray(new Preset[0]);
	}

	@Override
	public Preset getPresetAt(int index) {
		this.refreshStateIfNeeded();
		
		if (index < 0 || index >= this.filteredPresets.size()) {
			return null;
		}
		
		return this.filteredPresets.get(index);
	}

	@Override
	public Preset getPresetByName(String name) {
		this.refreshStateIfNeeded();
		
		Preset preset = this.delegate.getPresetByName(name);
		if (preset == null || !this.filteredPresetToPositionMap.containsKey(preset)) {
			return null;
		}
		return preset;
	}

	@Override
	public int setPreset(Preset preset) {
		if (this.checkClass(preset)) {
			this.delegate.setPreset(preset);
			this.refreshState();
			return this.filteredPresets.indexOf(preset);
		}
		
		return -1;
	}

	@Override
	public void removePresetAt(int index) {
		this.refreshStateIfNeeded();
		
		if (index < 0 || index >= this.filteredPresets.size()) {
			return;
		}
		
		Preset preset = this.filteredPresets.get(index);
		int position = this.filteredPresetToPositionMap.get(preset);
		this.delegate.removePresetAt(position);
	}

	@Override
	public boolean removePresetByName(String name) {
		Preset preset = this.delegate.getPresetByName(name);
		if (preset == null) {
			return false;
		}
		
		return this.delegate.removePresetByName(name);
	}

	@Override
	public void writeToFile(File file, Preset preset) throws IOException {
		this.delegate.writeToFile(file, preset);
	}

	@Override
	public Preset readFromFile(File file) throws IOException {
		return this.delegate.readFromFile(file);
	}

	@Override
	public void writeToPersistence(File file) throws IOException {
		this.delegate.writeToPersistence(file);
	}

	@Override
	public void readFromPersistence(File file) throws IOException {
		this.delegate.readFromPersistence(file);
	}

	@Override
	public Preset getDefaultPreset() {
		return this.delegate.getDefaultPreset();
	}

	@Override
	public void setDefaultPreset(Preset defaultPreset) {
		this.delegate.setDefaultPreset(defaultPreset);
	}

	@Override
	public void addPresetManagerListener(PresetManagerListener l) {
		this.delegate.addPresetManagerListener(l);
	}

	@Override
	public void removePresetManagerListener(PresetManagerListener l) {
		this.delegate.removePresetManagerListener(l);
	}

	private void resetState() {
		this.needsRefreshFlag = true;
	}
	
	private void refreshStateIfNeeded() {
		if (this.needsRefreshFlag) {
			this.refreshState();
		}
	}

	private void refreshState() {
		Preset presets[] = this.delegate.getPresets();
		
		this.filteredPresetToPositionMap.clear();
		this.filteredPresets.clear();
		
		for (int i = 0; i < presets.length; ++i) {
			Preset preset = presets[i];
			
			if (this.checkClass(preset)) {
				this.filteredPresetToPositionMap.put(preset, i);
				this.filteredPresets.add(preset);
			}
		}
		
		this.needsRefreshFlag = false;
	}
	
	private boolean checkClass(Preset preset) {
		for (Class<? extends Preset> klass : this.allowedPresetClassCollection) {
			if (klass.isInstance(preset)) {
				return true;
			}
		}
		
		return false;
	}
	
}
