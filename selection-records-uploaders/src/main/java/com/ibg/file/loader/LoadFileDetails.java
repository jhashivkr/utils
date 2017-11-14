package com.ibg.file.loader;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class LoadFileDetails {

	private Boolean processStat;
	private String dataProcGrp;
	private Set<File> flatFileSet = new HashSet<File>();
	private Set<File> jsonFileSet = new HashSet<File>();

	/**
	 * @return the processStat
	 */
	public Boolean getProcessStat() {
		return processStat;
	}

	/**
	 * @param processStat
	 *            the processStat to set
	 */
	public void setProcessStat(Boolean processStat) {
		this.processStat = processStat;
	}

	/**
	 * @return the dataProcGrp
	 */
	public String getDataProcGrp() {
		return dataProcGrp;
	}

	/**
	 * @param dataProcGrp
	 *            the dataProcGrp to set
	 */
	public void setDataProcGrp(String dataProcGrp) {
		this.dataProcGrp = dataProcGrp;
	}

	/**
	 * @return the flatFileSet
	 */
	public Set<File> getFlatFileSet() {
		return flatFileSet;
	}

	/**
	 * @param flatFileSet
	 *            the flatFileSet to set
	 */
	public void setFlatFileSet(Set<File> flatFileSet) {
		this.flatFileSet = flatFileSet;
	}

	/**
	 * @return the jsonFileSet
	 */
	public Set<File> getJsonFileSet() {
		return jsonFileSet;
	}

	/**
	 * @param jsonFileSet
	 *            the jsonFileSet to set
	 */
	public void setJsonFileSet(Set<File> jsonFileSet) {
		this.jsonFileSet = jsonFileSet;
	}

}
