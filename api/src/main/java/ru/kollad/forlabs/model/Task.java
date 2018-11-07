package ru.kollad.forlabs.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import ru.kollad.forlabs.api.API;
import ru.kollad.forlabs.api.exceptions.UnsupportedForlabsException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Task {
    private static final String ATTACHMENT_URL = "https://forlabs.ru/studies/${studyId}/tasks/${id}";
    private static final String MESSAGES_URL = "https://forlabs.ru/studies/${studyId}/tasks/${id}/responses";

    private int id;
    private int studyId;
    private int status;
    private String name;
    private String content;
    private String instructions;
    private double cost;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    private Assignment assignment;
    private List<Attachment> attachments;
    private List<Message> messages;


    public Task(JSONObject json) throws ParseException, JSONException {
        id = json.optInt("id", 0);
        name = json.optString("name", "");
        content = json.optString("content", "");
        instructions = json.optString("instructions", "");
        JSONObject pivot = json.getJSONObject("pivot");
        studyId = pivot.optInt("study_id", 0);
        cost = pivot.optDouble("cost", 0);
        status = pivot.optInt("status", 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        createdAt = sdf.parse(json.optString("created_at", "1970-01-01 00:00:00"));
        updatedAt = sdf.parse(json.optString("updated_at", "1970-01-01 00:00:00"));
        deletedAt = sdf.parse(json.optString("deleted_at", "1970-01-01 00:00:00"));
    }

    public int getId() { return id; }
    public int getStudyId() { return studyId; }
    public int getStatus() { return status; }
    public String getName() { return name; }
    public String getContent() { return content; }
    public String getInstructions() { return instructions; }
    public double getCost() { return cost; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public Date getDeletedAt() { return deletedAt; }
    public Assignment getAssignment() { return assignment; }

    public List<Attachment> getAttachments() {
        return attachments;
    }
    public List<Attachment> fetchAttachments(Parser p, Cookies cookies) throws IOException, UnsupportedForlabsException, JSONException {
        HttpURLConnection con = (HttpURLConnection)
                new URL(createAttachmentUrl()).openConnection();
        con.setDoInput(true);
        cookies.putTo(con);
        con.addRequestProperty("User-Agent", API.USER_AGENT);
        StringBuilder response = new StringBuilder();
        Scanner sc = new Scanner(con.getInputStream(), "utf-8");
        while (sc.hasNextLine()) {
            response.append(sc.nextLine());
            response.append("\n");
        }
        sc.close();
        cookies.replaceBy(con);

        Document doc = p.parseInput(response.toString(), con.getURL().toString());
        Elements elements = doc.getElementsByClass("container-fluid");
        if (elements.size() == 0)
            throw new UnsupportedForlabsException();

        sc = new Scanner(elements.get(0).child(0).childNode(0).toString());
        sc.nextLine(); sc.nextLine();

        String jsonStr = sc.nextLine();
        sc.close();
        JSONArray jsonArr = new JSONObject(jsonStr.substring(jsonStr.indexOf('{'), jsonStr.lastIndexOf(';')))
                .getJSONArray("attachments");

        attachments = new ArrayList<>();
        for (int i = 0; i < jsonArr.length(); i++)
            attachments.add(new Attachment(jsonArr.getJSONObject(i)));

        return attachments;
    }

    public List<Message> getMessages() {
        return messages;
    }
    public List<Message> fetchMessages(Parser p, Cookies cookies) throws IOException, ParseException, JSONException {
        HttpURLConnection con = (HttpURLConnection)
                new URL(MESSAGES_URL.replace("${studyId}", Integer.toString(studyId))
                        .replace("${id}", Integer.toString(id))).openConnection();
        con.setDoInput(true);
        cookies.putTo(con);
        con.addRequestProperty("User-Agent", API.USER_AGENT);
        StringBuilder response = new StringBuilder();
        Scanner sc = new Scanner(con.getInputStream(), "utf-8");
        while (sc.hasNextLine())
            response.append(sc.nextLine());
        sc.close();
        cookies.replaceBy(con);

        JSONArray mesArr = new JSONArray(response.toString());
        messages = new ArrayList<>();
        for (int i = 0; i < mesArr.length(); i++)
            messages.add(new Message(mesArr.getJSONObject(i)));

        return messages;
    }

    public void setAssignment(Assignment assignment) { this.assignment = assignment; }

    public String createAttachmentUrl() {
        return ATTACHMENT_URL.replace("${studyId}", Integer.toString(studyId))
                .replace("${id}", Integer.toString(id));
    }
    public String createMessageUrl() {
        return MESSAGES_URL.replace("${studyId}", Integer.toString(studyId))
                .replace("${id}", Integer.toString(id));
    }

    public static class Assignment {
        public static final int STATUS_DEBT = 1;
        public static final int STATUS_SENT = 2;
        public static final int STATUS_SUCCESS = 3;
        public static final int STATUS_GOT_ANSWER = 6;
        public static final int STATUS_HAS_ANSWER = 7;
        public static final int STATUS_DEFAULT = 0;

        private int id;
        private int taskId;
        private int status;

        private Date lastRepliedAt;
        private Date updatedAt;
        private Assessment assessment;

        public Assignment(JSONObject json) throws ParseException, JSONException {
            id = json.optInt("id", 0);
            taskId = json.optInt("task_id", 0);
            status = json.optInt("status", 0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            lastRepliedAt = sdf.parse(json.optString("last_replied_at", "1970-01-01 00:00:00"));
            updatedAt = sdf.parse(json.optString("updated_at", "1970-01-01 00:00:00"));

            if (!json.isNull("assessment"))
                assessment = new Assessment(json.getJSONObject("assessment"));
        }

        public int getId() { return id; }
        public int getTaskId() { return taskId; }
        public int getStatus() { return status; }
        public Date getLastRepliedAt() { return lastRepliedAt; }
        public Date getUpdatedAt() { return updatedAt; }
        public Assessment getAssessment() { return assessment; }
    }
    public static class Assessment {
        private int id;
        private int taskId;
        private int type;
        private double credits;
        private String comment;

        private String cause;
        private int status;
        private Date createdAt;
        private Date lastRepliedAt;

        private Date updatedAt;

        public Assessment(JSONObject json) throws ParseException, JSONException {
            id = json.optInt("id", 0);
            type = json.optInt("type", 0);
            credits = json.optDouble("credits", 0);
            comment = json.optString("comment", "");
            cause = json.optString("cause", "");
            createdAt = new SimpleDateFormat("yyyy-MM-dd").parse(json.optString("date", "1970-01-01"));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            JSONObject creditable = json.getJSONObject("creditable");
            taskId = creditable.optInt("task_id", 0);
            lastRepliedAt = sdf.parse(creditable.optString("last_replied_at", "1970-01-01 00:00:00"));
            updatedAt = sdf.parse(creditable.optString("updated_at", "1970-01-01 00:00:00"));
            status = creditable.optInt("status", 0);
        }
        public int getId() { return id; }
        public int getTaskId() { return taskId; }
        public int getType() { return type; }
        public double getCredits() { return credits; }
        public String getComment() { return comment; }
        public String getCause() { return cause; }
        public int getStatus() { return status; }
        public Date getCreatedAt() { return createdAt; }
        public Date getLastRepliedAt() { return lastRepliedAt; }
        public Date getUpdatedAt() { return updatedAt; }
    }
}
