package com.example.demo.services;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MailBoxService extends ApplicationService {
    private final String zimbraUrl = "https://mail.chu-reims.fr/";
    private final String zimbraDomain = "mail.chu-reims.fr";
    private final String zimbraAuthToken = "ZM_AUTH_TOKEN";

    public MailBoxService() {}

    public MailBoxResponseToken getMailBoxAuthToken(String email, String password) {
        MailBoxResponseToken response = new MailBoxResponseToken();
        try {
            URL url = new URL(this.zimbraUrl + "/service/soap");
            HttpURLConnection  connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            String requestBody = "{\"Header\": {\"context\": {\"_jsns\":\"urn:zimbra\",\"userAgent\":{\"name\":\"curl\",\"version\":\"7.54.0\"}}},\"Body\": {\"AuthRequest\": {\"_jsns\":\"urn:zimbraAccount\",\"account\":{\"_content\":\"\"" + email + "\"\",\"by\":\"name\"},\"password\": \"\"" + password + "\"\"}}}";
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
            int responseHttpCode = connection.getResponseCode();
            if (responseHttpCode != 200) {
                response.addErrors("Request didnt return 200 : " + responseHttpCode);
                return response;
            }
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            List<String> cookiesHeader = headerFields.get("Set-Cookie");
            if (cookiesHeader != null && cookiesHeader.get(0) != null) {
                response.token = cookiesHeader.get(0);
                response.setSuccess();
            }
        } catch (Exception e) {
            response.addErrors(e.getMessage());
            return response;
        }
        return response;
    }

    public MailBoxResponseMailsPreview getLastMailsByFolderName(String folderName, int mailCount, String authToken) {
        MailBoxResponseMailsPreview response = new MailBoxResponseMailsPreview();
            // TODO
        response.setSuccess();
        return response;
    }

    private static class MailBoxResponseToken extends ResponseService {
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        private String token;
    }

    private static class MailBoxResponseMailsPreview extends ResponseService {
        public List<ZimbraMailPreview> getMailPreview() {
            return mailPreview;
        }

        public void setMailPreview(List<ZimbraMailPreview> mailPreview) {
            this.mailPreview = mailPreview;
        }

        private List<ZimbraMailPreview> mailPreview = new ArrayList<ZimbraMailPreview>();

        private static class ZimbraMailPreview {
            public int s;
            public long d;
            public String l;
            public String cid;
            public String f;
            public int rev;
            public String id;
            public String su;
            public String fr;
            public boolean cm;
            public List<ZimbraMailExpeditor> e;
            public String sf;

            private static class ZimbraMailExpeditor {
                public String a;
                public String d;
                public String p;
                public String t;
            }
        }
    }
}
