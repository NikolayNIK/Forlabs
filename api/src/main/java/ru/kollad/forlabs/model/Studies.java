package ru.kollad.forlabs.model;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Provides a list for studies.
 */
public class Studies extends ArrayList<Study> implements Serializable {

	/**
	 * Constructor for elements.
	 * @param elements Elements of the page.
	 */
	public Studies(Elements elements) {
		for (Element e : elements)
			add(new Study(e));
	}
}
