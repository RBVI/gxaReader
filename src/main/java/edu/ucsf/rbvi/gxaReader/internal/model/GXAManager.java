package edu.ucsf.rbvi.gxaReader.internal.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.cytoscape.command.AvailableCommands;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

import edu.ucsf.rbvi.gxaReader.internal.utils.HTTPUtils;

public class GXAManager {
	final public static String EXPERIMENTS_URL = "https://www.ebi.ac.uk/gxa/sc/json/experiments";
	final public static String RESULTS_URL = "https://www.ebi.ac.uk/gxa/sc/experiments/%s/Results";

	final AvailableCommands availableCommands;
	final CommandExecutorTaskFactory ceTaskFactory;
	final TaskManager taskManager;

	final Map<String, GXAExperiment> gxaMap;
	final Map<String, List<GXAEntry>> speciesMap;
	final CyServiceRegistrar registrar;
	final MTXManager mtxManager;
	final Map<String, GXAEntry> entryMap;

	public GXAManager(final CyServiceRegistrar registrar, final MTXManager mtxManager) {
		gxaMap = new HashMap<>();
		speciesMap = new HashMap<>();
		entryMap = new HashMap<>();
		this.mtxManager = mtxManager;
		this.registrar = registrar;
		this.availableCommands = registrar.getService(AvailableCommands.class);
		this.ceTaskFactory = registrar.getService(CommandExecutorTaskFactory.class);
		this.taskManager = registrar.getService(TaskManager.class);
	}

	public void addExperiment(GXAEntry entry, GXAExperiment gxa) {
		gxaMap.put(entry.getAccession(), gxa);
	}

	public void addExperiment(String accession, GXAExperiment gxa) {
		gxaMap.put(accession, gxa);
	}

	public void deleteExperiment(String accession) {
		if (gxaMap.containsKey(accession)) {
			GXAExperiment exp = gxaMap.get(accession);
			gxaMap.remove(accession);
		}
	}

	public GXAExperiment getExperiment(String accession) {
		if (gxaMap.containsKey(accession)) return gxaMap.get(accession);
		return null;
	}

	public List<GXAExperiment> getExperiments() {
		return new ArrayList<>(gxaMap.values());
	}

	public Set<String> getExperimentAccessions() {
		return gxaMap.keySet();
	}

	public List<GXAEntry> getGXAEntries(String species) {
		if (speciesMap.containsKey(species))
			return speciesMap.get(species);
		return null;
	}

	public List<GXAEntry> getGXAEntries() { return new ArrayList<>(entryMap.values()); }

	public GXAEntry getGXAEntry(String accession) {
		if (entryMap.containsKey(accession))
			return entryMap.get(accession);
		return null;
	}

	public Set<String> getSpecies() {
		return speciesMap.keySet();
	}

	public GXAExperiment fetchExperiment(GXAEntry entry, TaskMonitor taskMonitor) {
		return fetchExperiment(entry.getAccession(), taskMonitor);
	}

	public GXAExperiment fetchExperiment(String accession, TaskMonitor taskMonitor) {
		GXAExperiment newExp = new GXAExperiment(this);
		newExp.fetchMTX(accession, taskMonitor);
		return newExp;
	}

	public void loadGXAEntries(TaskMonitor taskMonitor) {
		if (entryMap.size() > 0) return;
		JSONObject json = HTTPUtils.fetchJSON(EXPERIMENTS_URL, taskMonitor);
		JSONArray experiments = (JSONArray) json.get("aaData");
		for (Object exp: experiments) {
			GXAEntry entry = new GXAEntry((JSONObject)exp);
			entryMap.put(entry.getAccession(), entry);
			if (!speciesMap.containsKey(entry.getSpecies()))
				speciesMap.put(entry.getSpecies(), new ArrayList<>());
			speciesMap.get(entry.getSpecies()).add(entry);
		}

	}

	public String getResultsURL(GXAEntry entry) {
		return getResultsURL(entry.getAccession());
	}

	public String getResultsURL(String accession) {
		return String.format(RESULTS_URL, accession);
	}

	public void executeCommand(String namespace, String command, Map<String, Object> args) {
		taskManager.execute(ceTaskFactory.createTaskIterator(namespace, command, args, null));
	}

	public MTXManager getMTXManager() { return mtxManager; }
}
