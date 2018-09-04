package fr.box.jean.tools.dateModifier.model;

import java.time.LocalDateTime;

public class Fichier implements Comparable<Fichier> {

	private String name;
	private String path;
	private LocalDateTime lastModifiedTime;

	public Fichier(String name, String path) {
		super();
		this.name = name;
		this.path = path;
	}

	public int compareTo(Fichier other) {
		return name.compareTo(other.name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public LocalDateTime getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(LocalDateTime lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

}
