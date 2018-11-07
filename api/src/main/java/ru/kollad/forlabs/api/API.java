package ru.kollad.forlabs.api;

import android.webkit.MimeTypeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kollad.forlabs.api.exceptions.IncorrectCredentialsException;
import ru.kollad.forlabs.api.exceptions.UnsupportedForlabsException;
import ru.kollad.forlabs.model.*;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class API {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0";
    private static final String LOGIN_URL = "https://forlabs.ru/login";
    private static final String LOGIN_FORM_MASK = "_token=${token}&email=${email}&password=${password}";
    private static final String STUDIES_URL = "https://forlabs.ru/studies";
    private static final String UPLOAD_URL = "https://forlabs.ru/upload";
    private static final String UPLOAD_MASK = "?flowChunkNumber=1&flowChunkSize=1048576&" +
            "flowCurrentChunkSize=${size}&flowTotalSize=${size}&flowIdentifier=${size}-${filename2}&" +
            "flowFilename=${filename}&flowRelativePath=${filename}&flowTotalChunks=1";

    private Cookies cookies;
    private static Parser p = Parser.htmlParser();

    private StudentInfo studentInfo;
    private Studies studies;

    public API() {}

    public void login(String email, String password) throws Exception {
        // get forlabs login page
        HttpURLConnection con = (HttpURLConnection) new URL(LOGIN_URL).openConnection();
        con.addRequestProperty("User-Agent", USER_AGENT);
        con.setDoInput(true);
        if (con.getResponseCode() == 403) {
            Scanner err = new Scanner(con.getErrorStream());
            while (err.hasNextLine())
                System.out.println(err.nextLine());
            err.close();
            return;
        }
        Scanner sc = new Scanner(con.getInputStream(), "utf-8");
        StringBuilder response = new StringBuilder();
        while (sc.hasNextLine()) {
            response.append(sc.nextLine());
            response.append("\n");
        }
        sc.close();

        // parse that thing
        Document doc = p.parseInput(response.toString(), LOGIN_URL);

        // get elements with token
        Elements elements = doc.body().getElementsByAttributeValue("name", "_token");
        if (elements.size() == 0)
            throw new UnsupportedForlabsException();
        String token = elements.get(0).attr("value");

        // read cookies
        cookies = new Cookies(con);

        // post some credentials
        con = (HttpURLConnection) new URL(LOGIN_URL).openConnection();
        con.setRequestMethod("POST");
        con.addRequestProperty("User-Agent", USER_AGENT);
        con.setDoInput(true);
        con.setDoOutput(true);
        cookies.putTo(con);
        con.setInstanceFollowRedirects(false);
        OutputStream os = con.getOutputStream();
        os.write(LOGIN_FORM_MASK.replace("${token}", URLEncoder.encode(token, "utf-8"))
                                .replace("${email}", URLEncoder.encode(email, "utf-8"))
                                .replace("${password}", URLEncoder.encode(password, "utf-8"))
                                .getBytes(StandardCharsets.US_ASCII));
        os.close();

        // get redirection
        String redirection = con.getHeaderField("Location");
        if (redirection.contains("login"))
            throw new IncorrectCredentialsException();
        cookies.replaceBy(con);

        // get dashboard
        con = (HttpURLConnection) new URL(redirection).openConnection();
        con.addRequestProperty("User-Agent", USER_AGENT);
        con.setDoInput(true);
        cookies.putTo(con);
        sc = new Scanner(con.getInputStream(), "utf-8");
        response = new StringBuilder();
        while (sc.hasNextLine()) {
            response.append(sc.nextLine());
            response.append("\n");
        }
        sc.close();
        doc = p.parseInput(response.toString(), redirection);
        studentInfo = new StudentInfo(doc);

        cookies.replaceBy(con);
    }

    public Studies getStudies() throws IOException, UnsupportedForlabsException {
        if (studies != null) return studies;

        HttpURLConnection con = (HttpURLConnection) new URL(STUDIES_URL).openConnection();
        con.addRequestProperty("User-Agent", USER_AGENT);
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

        Document doc = p.parseInput(response.toString(), STUDIES_URL);
        Elements elements = doc.getElementsByClass("col-sm-6");
        if (elements.size() == 0)
            throw new UnsupportedForlabsException();
        studies = new Studies(elements);

        return studies;
    }
    public StudentInfo getStudentInfo() { return studentInfo; }

    public Study fetchStudy(Study study) throws IOException, UnsupportedForlabsException, ParseException, JSONException {
        study.fetch(p, cookies);
        return study;
    }

    public List<Attachment> getTaskAttachments(Task task) throws IOException, UnsupportedForlabsException, JSONException {
        return task.fetchAttachments(p, cookies);
    }
    public List<Message> getTaskMessages(Task task) throws IOException, ParseException, JSONException {
        return task.fetchMessages(p, cookies);
    }

    private static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public Attachment uploadAttachment(Task t, File f) throws IOException, UnsupportedForlabsException, JSONException {
        return uploadAttachment(t, f.getName(), f.length(), getMimeType(f.toURI().toString()), new FileInputStream(f));
    }
    public Attachment uploadAttachment(Task t, String fileName, long fileLength, String mimeType, InputStream is)
            throws IOException, UnsupportedForlabsException, JSONException {
        // get fucking token
        HttpURLConnection con = (HttpURLConnection) new URL(t.createAttachmentUrl()).openConnection();
        con.addRequestProperty("User-Agent", USER_AGENT);
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

        // parse that thing
        Document doc = p.parseInput(response.toString(), LOGIN_URL);

        // get elements with token
        Elements elements = doc.body().getElementsByAttributeValue("name", "_token");
        if (elements.size() == 0)
            throw new UnsupportedForlabsException();
        String token = elements.get(0).attr("value");

        StringBuilder filename2 = new StringBuilder(fileName);
        for (int i = filename2.length() - 1; i >= 0; i--)
            if (!Character.isLetterOrDigit(filename2.charAt(i)))
                filename2.deleteCharAt(i);

        con = (HttpURLConnection) new URL(UPLOAD_URL + UPLOAD_MASK.replace("${size}", Long.toString(fileLength))
                .replace("${filename}", fileName).replace("${filename2}", filename2.toString())).openConnection();
        con.addRequestProperty("User-Agent", USER_AGENT);
        con.addRequestProperty("X-CSRF-Token", token);
        con.setDoInput(true);
        cookies.putTo(con);
        con.getResponseCode();
        cookies.replaceBy(con);

        con = (HttpURLConnection) new URL(UPLOAD_URL).openConnection();
        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.addRequestProperty("User-Agent", USER_AGENT);
        con.addRequestProperty("X-CSRF-Token", token);
        cookies.putTo(con);

        String multipartDivider = "----------------";
        Random r = new Random();
        for (int i = 0; i < 16; i++)
            multipartDivider += Integer.toHexString(r.nextInt(16));
        con.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + multipartDivider);

        OutputStream os = con.getOutputStream();
        PrintStream ps = new PrintStream(os, true, "utf-8");

        // multipart thing
        // write boundary
        ps.print("--");
        ps.println(multipartDivider);

        // flow chunk number
        ps.print("Content-Disposition: form-data; name=\"flowChunkNumber\"\n\n1\n--");
        ps.println(multipartDivider);

        // flow chunk size
        ps.print("Content-Disposition: form-data; name=\"flowChunkSize\"\n\n1048576\n--");
        ps.println(multipartDivider);

        // flow current chunk size
        ps.print("Content-Disposition: form-data; name=\"flowCurrentChunkSize\"\n\n");
        ps.print(fileLength);
        ps.print("\n--");
        ps.println(multipartDivider);

        // flow total size
        ps.print("Content-Disposition: form-data; name=\"flowTotalSize\"\n\n");
        ps.print(fileLength);
        ps.print("\n--");
        ps.println(multipartDivider);

        // flow identifier
        ps.print("Content-Disposition: form-data; name=\"flowIdentifier\"\n\n");
        ps.print(filename2);
        ps.print("\n--");
        ps.println(multipartDivider);

        // flow filename
        ps.print("Content-Disposition: form-data; name=\"flowFilename\"\n\n");
        ps.print(fileName);
        ps.print("\n--");
        ps.println(multipartDivider);

        // flow relative path
        ps.print("Content-Disposition: form-data; name=\"flowRelativePath\"\n\n");
        ps.print(fileName);
        ps.print("\n--");
        ps.println(multipartDivider);

        // flow total chunks
        ps.print("Content-Disposition: form-data; name=\"flowTotalChunks\"\n\n1\n--");
        ps.println(multipartDivider);

        // file
        ps.print("Content-Disposition: form-data; name=\"file\"; filename=\"");
        ps.print(fileName);
        ps.print("\"\nContent-Type: ");
        ps.print(mimeType != null ? mimeType : "application/octet-stream");
        ps.print("\n\n");

        byte[] buf = new byte[4096];
        int read;
        while ((read = is.read(buf, 0, 4096)) > 0)
            os.write(buf, 0, read);
        is.close();

        // write last boundary
        ps.print("\n--");
        ps.print(multipartDivider);
        ps.println("--");
        ps.close();

        response = new StringBuilder();
        sc = new Scanner(con.getInputStream(), "utf-8");
        while (sc.hasNextLine()) {
            response.append(sc.nextLine());
            response.append("\n");
        }
        sc.close();
        cookies.replaceBy(con);

        return new Attachment(new JSONObject(response.toString()).getJSONObject("attachment"));
    }

    public void sendMessageToTask(Task t, String message, List<Attachment> attachments) throws IOException, UnsupportedForlabsException, JSONException {
        // get fucking token
        HttpURLConnection con = (HttpURLConnection) new URL(t.createAttachmentUrl()).openConnection();
        con.addRequestProperty("User-Agent", USER_AGENT);
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

        // parse that thing
        Document doc = p.parseInput(response.toString(), LOGIN_URL);

        // get elements with token
        Elements elements = doc.body().getElementsByAttributeValue("name", "_token");
        if (elements.size() == 0)
            throw new UnsupportedForlabsException();
        String token = elements.get(0).attr("value");

        con = (HttpURLConnection) new URL(t.createMessageUrl()).openConnection();
        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.addRequestProperty("Content-Type", "application/json;charset=utf-8");
        con.addRequestProperty("User-Agent", USER_AGENT);
        con.addRequestProperty("X-Requested-With", "XMLHttpRequest");
        con.addRequestProperty("X-XSRF-Token", cookies.get("XSRF-TOKEN"));
        con.addRequestProperty("X-CSRF-Token", token);
        cookies.putTo(con);

        JSONObject json = new JSONObject();
        json.put("message", message);
        JSONArray attachmentsArr = new JSONArray();
        for (Attachment a: attachments)
            attachmentsArr.put(a.toJson());
        json.put("attachments", attachmentsArr);
        OutputStream os = con.getOutputStream();
        os.write(json.toString().getBytes(StandardCharsets.UTF_8));
        os.close();

        cookies.replaceBy(con);
    }
}
