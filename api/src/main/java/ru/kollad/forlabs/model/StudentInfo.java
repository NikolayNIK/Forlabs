package ru.kollad.forlabs.model;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.Locale;

import androidx.annotation.NonNull;
import ru.kollad.forlabs.api.exceptions.UnsupportedForlabsException;

/**
 * Provides storage for student info.
 */
public class StudentInfo implements Serializable {
	private String studentName;
	private String studentPhotoUrl;
	private String groupName;
	private String directionName;
	private String profileName;
	private String qualificationName;
	private String studyFormName;
	private int admissionYear;
	private int currentCourse;
	private int totalCourseCount;

	/**
	 * Constructor for document.
	 * @param doc Document.
	 */
	public StudentInfo(Document doc) throws Exception {
		// get needed elements
		Elements elements = doc.getElementsByClass("dropup");
		if (elements.size() == 0)
			throw new UnsupportedForlabsException();

		// fill student info
		elements = elements.get(0).child(0).children();
		studentPhotoUrl = elements.get(0).attr("src");
		studentName = elements.get(1).text();

		elements = doc.getElementsByClass("col-md-6").get(0).children();
		for (Element el : elements) {
			if (el.tagName().equals("h3")) {
				groupName = el.text();
				break;
			}
		}

		Elements tableRows = null;
		for (Element a : elements) {
			if (a.tagName().equals("table")){
				tableRows = a.child(0).children();
				break;
			}
		}

		if (tableRows == null) throw new Exception("Shit happens!");

		directionName = tableRows.get(0).child(1).text();
		profileName = tableRows.get(1).child(1).text();
		qualificationName = tableRows.get(2).child(1).text();
		studyFormName = tableRows.get(3).child(1).text();
		admissionYear = Integer.parseInt(tableRows.get(4).child(1).text());
		String course = tableRows.get(5).child(1).text();
		currentCourse = Integer.parseInt(course.substring(0, 1));
		totalCourseCount = Integer.parseInt(course.substring(course.length() - 1));
	}

	public String getStudentName() {
		return studentName;
	}
	public String getStudentPhotoUrl() {
		return studentPhotoUrl;
	}
	public String getGroupName() {
		return groupName;
	}
	public String getDirectionName() {
		return directionName;
	}
	public String getProfileName() {
		return profileName;
	}
	public String getQualificationName() {
		return qualificationName;
	}
	public String getStudyFormName() {
		return studyFormName;
	}
	public int getAdmissionYear() {
		return admissionYear;
	}
	public int getCurrentCourse() {
		return currentCourse;
	}
	public int getTotalCourseCount() {
		return totalCourseCount;
	}

	@NonNull
	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "%s. %s, %s, %s, %s, %s (from %d). %d / %d", studentName, groupName,
				qualificationName, profileName, directionName, studyFormName, admissionYear, currentCourse, totalCourseCount);
	}
}
