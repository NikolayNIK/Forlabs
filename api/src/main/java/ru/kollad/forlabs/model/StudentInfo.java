package ru.kollad.forlabs.model;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Locale;

public class StudentInfo {
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

    public StudentInfo(Document doc) {
        Elements elements = doc.getElementsByClass("dropup").get(0).child(0).children();
        studentPhotoUrl = elements.get(0).attr("src");
        studentName = elements.get(1).text();

        elements = doc.getElementsByClass("col-md-6").get(0).children();
        groupName = elements.get(0).text();

        Elements tableRows = elements.get(1).child(0).children();

        directionName = tableRows.get(0).child(1).text();
        profileName = tableRows.get(1).child(1).text();
        qualificationName = tableRows.get(2).child(1).text();
        studyFormName = tableRows.get(3).child(1).text();
        admissionYear = Integer.parseInt(tableRows.get(4).child(1).text());
        String course = tableRows.get(5).child(1).text();
        currentCourse = Integer.parseInt(course.substring(0, 1));
        totalCourseCount = Integer.parseInt(course.substring(course.length() - 1));
    }

    public String getStudentName() { return studentName; }
    public String getStudentPhotoUrl() { return studentPhotoUrl; }
    public String getGroupName() { return groupName; }
    public String getDirectionName() { return directionName; }
    public String getProfileName() { return profileName; }
    public String getQualificationName() { return qualificationName; }
    public String getStudyFormName() { return studyFormName; }
    public int getAdmissionYear() { return admissionYear; }
    public int getCurrentCourse() { return currentCourse; }
    public int getTotalCourseCount() { return totalCourseCount; }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s. %s, %s, %s, %s, %s (from %d). %d / %d", studentName, groupName,
                qualificationName, profileName, directionName, studyFormName, admissionYear, currentCourse, totalCourseCount);
    }
}
