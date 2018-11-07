package ru.kollad.forlabs.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import ru.kollad.forlabs.api.API;
import ru.kollad.forlabs.api.exceptions.UnsupportedForlabsException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Study {
    private static final String FETCH_URL = "https://forlabs.ru/studies/${id}";

    public static final int NORMAL = 0;
    public static final int DEBT = -1;
    public static final int CERTIFIED = 1;

    private int id;
    private List<Integer> lecturersId;
    private String name;

    private double points;
    private int grade;
    private float attendPercent;
    private List<Attendance> attendances;

    private String teacherName;
    private String simpleTeacherName;
    private int status;

    private List<Task> tasks;

    private List<ScheduleItem> scheduleItems;

    public Study(Element html) {
        Elements elements = html.child(0).children();

        // block A
        Elements block = elements.get(0).children();
        String url = block.get(0).attr("href");
        id = Integer.parseInt(url.substring(url.lastIndexOf('/') + 1));
        name = block.get(0).child(0).text();
        points = Float.parseFloat(block.get(1).text().replace(',','.'));

        simpleTeacherName = elements.get(1).text();
        block = elements.get(1).children();
        if (block.size() == 2)
            status = "аттестован".equals(block.get(1).attr("title")) ? CERTIFIED : DEBT;
    }

    public Study fetch(Parser p, Cookies cookies) throws IOException, UnsupportedForlabsException, ParseException, JSONException {
        HttpURLConnection con = (HttpURLConnection) new URL(FETCH_URL.replace("${id}", Integer.toString(id))).openConnection();
        con.addRequestProperty("User-Agent", API.USER_AGENT);
        con.setDoInput(true);
        cookies.putTo(con);
        Scanner sc = new Scanner(con.getInputStream(), "utf-8");
        StringBuilder response = new StringBuilder();
        while (sc.hasNextLine()) {
            response.append(sc.nextLine());
            response.append("\n");
        }
        sc.close();
        cookies.replaceBy(con);

        Document doc = p.parseInput(response.toString(), con.getURL().toString());

        Elements elements = doc.getElementsByClass("dl-horizontal");
        if (elements.size() == 0)
            throw new UnsupportedForlabsException();
        teacherName = elements.get(0).child(6).text();

        elements = doc.getElementsByClass("container-fluid");
        if (elements.size() == 0)
            throw new UnsupportedForlabsException();

        sc = new Scanner(elements.get(0).child(0).childNode(0).toString());
        sc.nextLine();

        // perform study object
        String jsonStr = sc.nextLine();
        JSONObject json = new JSONObject(jsonStr.substring(jsonStr.indexOf('{'), jsonStr.lastIndexOf(';')));

        // lecturers
        lecturersId = new ArrayList<>();
        lecturersId.add(json.optInt("lecturer_id", 0));

        int lecturerId = json.optInt("lecturer2_id", 0);
        if (lecturerId != 0) lecturersId.add(lecturerId);

        lecturerId = json.optInt("lecturer3_id", 0);
        if (lecturerId != 0) lecturersId.add(lecturerId);

        tasks = new ArrayList<>();
        JSONArray jsonArr = json.getJSONArray("tasks");
        for (int i = 0; i < jsonArr.length(); i++)
            tasks.add(new Task(jsonArr.getJSONObject(i)));

        // skip student info
        sc.nextLine();

        // get score object
        jsonStr = sc.nextLine();
        json = new JSONObject(jsonStr.substring(jsonStr.indexOf('{'), jsonStr.lastIndexOf(';')));

        // fill score fields
        points = json.optDouble("credits", 0);
        grade = json.optInt("grade");

        // skip assessments
        sc.nextLine();

        // get task assignments
        jsonStr = sc.nextLine();
        json = new JSONObject(jsonStr.substring(jsonStr.indexOf('{'), jsonStr.lastIndexOf(';')));

        // fill assessments
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            Task.Assignment ta = new Task.Assignment(json.getJSONObject(keys.next()));
            for (Task t: tasks)
                if (t.getId() == ta.getTaskId()) {
                    t.setAssignment(ta);
                    break;
                }
        }

        // skip attend percent (we will calculate it)
        sc.nextLine();

        // get attendances
        jsonStr = sc.nextLine();
        jsonArr = new JSONArray(jsonStr.substring(jsonStr.indexOf('['), jsonStr.lastIndexOf(';')));

        // fill it!
        attendances = new ArrayList<>();
        float isPresentCount = 0;
        for (int i = 0; i < jsonArr.length(); i++) {
            Attendance a = new Attendance(jsonArr.getJSONObject(i));
            isPresentCount += a.isPresent() ? 1 : 0;
            attendances.add(a);
        }
        attendPercent = (int) (isPresentCount / jsonArr.length() * 100);

        // get schedule items
        jsonStr = sc.nextLine();
        jsonArr = new JSONArray(jsonStr.substring(jsonStr.indexOf('['), jsonStr.lastIndexOf(';')));

        // fill it!
        scheduleItems = new ArrayList<>();
        for (int i = 0; i < jsonArr.length(); i++)
            scheduleItems.add(new ScheduleItem(jsonArr.getJSONObject(i)));

        sc.close();

        return this;
    }

    public int getId() { return id; }
    public List<Integer> getLecturersId() { return lecturersId; }
    public String getName() { return name; }
    public double getPoints() { return points; }
    public int getGrade() { return grade; }
    public float getAttendPercent() { return attendPercent; }
    public List<Attendance> getAttendances() { return attendances; }
    public String getTeacherName() { return teacherName; }
    public String getSimpleTeacherName() { return simpleTeacherName; }
    public int getStatus() { return status; }
    public List<Task> getTasks() { return tasks; }
    public List<ScheduleItem> getScheduleItems() { return scheduleItems; }
}
