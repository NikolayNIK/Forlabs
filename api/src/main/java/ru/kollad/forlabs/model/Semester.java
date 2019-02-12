package ru.kollad.forlabs.model;

import java.util.ArrayList;

/**
 * Created by NikolayNIK on 11.02.2019.
 */
public class Semester extends ArrayList<Study> {

	private final String name;

	public Semester(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Study find(String name) {
		for (Study study : this)
			if (study.getName().equals(name))
				return study;
		return null;
	}
}
