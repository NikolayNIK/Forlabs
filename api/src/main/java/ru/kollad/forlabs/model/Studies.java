package ru.kollad.forlabs.model;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Studies {

	private List<Study> studies;

	public Studies(Elements elements) {
		studies = new ArrayList<>();
		for (Element e : elements)
			studies.add(new Study(e));
	}

	public List<Study> getStudies() {
		return studies;
	}
}
