package ru.kollad.forlabs.model;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a list for studies.
 */
public class Studies implements Serializable {
	private List<Study> studies;

	/**
	 * Constructor for elements.
	 * @param elements Elements of the page.
	 */
	public Studies(Elements elements) {
		studies = new ArrayList<>();
		for (Element e : elements)
			studies.add(new Study(e));
	}

	public List<Study> getStudies() {
		return studies;
	}
}
