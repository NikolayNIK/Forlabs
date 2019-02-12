package ru.kollad.forlabs.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Provides a list for studies.
 */
public class Semesters extends ArrayList<Semester> implements Serializable {

	private static final long serialVersionUID = -7890043171358234840L;

	/**
	 * Constructor for elements.
	 */
	public Semesters() {
	}

	public Semester findSemester(String name) {
		for (Semester semester : this) {
			if (equals(name, semester.getName()))
				return semester;
		}

		Semester semester = new Semester(name);
		this.add(semester);

		return semester;
	}

	public Study findStudy(String name) {
		for (Semester semester : this) {
			Study study = semester.find(name);
			if (study != null) return study;
		}

		return null;
	}

	private static boolean equals(Object a, Object b) {
		if (a == null || b == null) return a == b;
		return a.equals(b);
	}
}
