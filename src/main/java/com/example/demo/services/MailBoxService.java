package com.example.demo.services;

import com.example.demo.utils.HttpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailBoxService extends ApplicationService {
    private final String zimbraUrl = "https://mail.chu-reims.fr/";
    private final String zimbraDomain = "mail.chu-reims.fr";
    private final String zimbraAuthToken = "ZM_AUTH_TOKEN";

    public MailBoxService() {}

    public MailBoxResponseToken getMailBoxAuthToken(String email, String password) {
        MailBoxResponseToken response = new MailBoxResponseToken();
        try {
            URL url = new URL(this.zimbraUrl + "service/soap");
            HttpURLConnection  connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            String requestBody = "{\"Header\": {\"context\": {\"_jsns\":\"urn:zimbra\",\"userAgent\":{\"name\":\"curl\",\"version\":\"7.54.0\"}}},\"Body\": {\"AuthRequest\": {\"_jsns\":\"urn:zimbraAccount\",\"account\":{\"_content\":\"\"" + email + "\"\",\"by\":\"name\"},\"password\": \"\"" + password + "\"\"}}}";
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
            int responseHttpCode = connection.getResponseCode();
            HttpUtils.ensureStatusCodeIsOk(responseHttpCode);
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
        MailBoxResponseToken responseGetCrsf = this.getZimbraSessionCrsfToken(authToken);
        if (!responseGetCrsf.getIsSuccess()) {
            response.setErrors(responseGetCrsf.getErrors());
            return response;
        }
        if (mailCount > 100) {
            mailCount = 100;
        }
        try {
            URL url = new URL(this.zimbraUrl + "service/soap/SearchRequest");
            HttpURLConnection  connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Cookie", this.getCookieForDomain(authToken));
            String requestBody = this.getPayloadMailSearchInFolder(folderName, mailCount, responseGetCrsf.token);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
            int responseHttpCode = connection.getResponseCode();
            HttpUtils.ensureStatusCodeIsOk(responseHttpCode);
            String responseHttpString = HttpUtils.getConnectionResponseString(connection);
            response.setMailPreviews(new ObjectMapper().readValue(responseHttpString, ZimbraResponseMailPreview.class).getBody().getSearchResponse().getM());
            response.setSuccess();
            return response;
        } catch(Exception e) {
            response.addErrors(e.getMessage());
            return response;
        }
    }

    public MailBoxResponseBlob getMailAttachmentBlob(String mailId, String part, String authToken) {
        MailBoxResponseBlob response = new MailBoxResponseBlob();
        try {
            URL url = new URL(this.zimbraUrl + "service/home/~/?auth=co&loc=fr_FR&id=" + mailId + "&part=" + part);
            HttpURLConnection  connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", this.getCookieForDomain(authToken));
            int responseHttpCode = connection.getResponseCode();
            HttpUtils.ensureStatusCodeIsOk(responseHttpCode);
            response.setBlob(HttpUtils.getConnectionResponseBytes(connection));
            response.setSuccess();
            return response;
        } catch(Exception e) {
            response.addErrors(e.getMessage());
            return response;
        }
    }

    public MailBoxResponseMail getMailById(String mailId, String authToken) {
        MailBoxResponseMail response = new MailBoxResponseMail();
        MailBoxResponseToken responseGetCrsf = this.getZimbraSessionCrsfToken(authToken);
        if (!responseGetCrsf.getIsSuccess()) {
            response.setErrors(responseGetCrsf.getErrors());
            return response;
        }
        try {
            URL url = new URL(this.zimbraUrl + "service/soap/GetMsgRequest");
            HttpURLConnection  connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Cookie", this.getCookieForDomain(authToken));
            String requestBody = this.getPayloadMailFind(mailId, responseGetCrsf.token);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
            int responseHttpCode = connection.getResponseCode();
            HttpUtils.ensureStatusCodeIsOk(responseHttpCode);
            String responseHttpString = HttpUtils.getConnectionResponseString(connection);
            response.setMail(new ObjectMapper().readValue(responseHttpString, ZimbraResponseMail.class).getBody().getGetMsgResponse().getM().get(0));
            response.setSuccess();
            return response;
        } catch(Exception e) {
            response.addErrors(e.getMessage());
            return response;
        }
    }

    private MailBoxResponseToken getZimbraSessionCrsfToken(String authToken) {
        MailBoxResponseToken response = new MailBoxResponseToken();
        try {
            URL url = new URL(this.zimbraUrl);
            HttpURLConnection  connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", this.getCookieForDomain(authToken));
            int responseHttpCode = connection.getResponseCode();
            HttpUtils.ensureStatusCodeIsOk(responseHttpCode);
            String responseString = HttpUtils.getConnectionResponseString(connection);
            String regex = "@\"window.csrfToken\\s*=\\s*\"\"(.*?)\"\"\"";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(responseString);
            String crsfToken = matcher.group(0).split("\"")[1];
            if (crsfToken == null) {
                response.addErrors("crsfToken not found in http response");
                return response;
            }
            response.setToken(crsfToken);
            response.setSuccess();
            return response;
        } catch (Exception e) {
            response.addErrors(e.getMessage());
            return response;
        }
    }

    private String getPayloadMailSearchInFolder(String folderName, int mailCount, String crsfToken) {
        String mailInFolderFilter = "in:" + folderName;
        return "{\"Header\":{\"context\":{\"_jsns\":\"urn:zimbra\",\"csrfToken\":\"" + crsfToken + "\"}},\"Body\":{\"SearchRequest\":{\"_jsns\":\"urn:zimbraMail\",\"sortBy\":\"dateDesc\",\"header\":[{\"n\":\"List-ID\"},{\"n\":\"X-Zimbra-DL\"},{\"n\":\"IN-REPLY-TO\"}],\"tz\":{\"id\":\"Europe/Brussels\"},\"locale\":{\"_content\":\"fr_FR\"},\"offset\":0,\"limit\":" + mailCount + ",\"query\":\"" + mailInFolderFilter + "\",\"types\":\"message\",\"recip\":\"0\",\"needExp\":1}}}";
    }

    private String getPayloadMailFind(String mailId, String crsfToken) {
        return "{\"Header\":{\"context\":{\"_jsns\":\"urn:zimbra\",\"csrfToken\":\"" + crsfToken + "\"}},\"Body\":{\"GetMsgRequest\":{\"_jsns\":\"urn:zimbraMail\",\"m\":{\"id\":\"" + mailId + "\",\"html\":1,\"needExp\":1,\"header\":[{\"n\":\"List-ID\"},{\"n\":\"X-Zimbra-DL\"},{\"n\":\"IN-REPLY-TO\"}],\"max\":250000}}}}";
    }

    private String getCookieForDomain(String authToken) {
        return this.zimbraAuthToken + "=" + authToken;
    }

    // Service responses
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
        public List<ZimbraMailPreview> getMailPreviews() {
            return mailPreviews;
        }

        public void setMailPreviews(List<ZimbraMailPreview> mailPreview) {
            this.mailPreviews = mailPreview;
        }

        private List<ZimbraMailPreview> mailPreviews = new ArrayList<ZimbraMailPreview>();
    }

    private static class MailBoxResponseBlob extends ResponseService {
        public byte[] getBlob() {
            return blob;
        }

        public void setBlob(byte[] blob) {
            this.blob = blob;
        }

        private byte[] blob;

    }

    private static class MailBoxResponseMail extends ResponseService {
        public ZimbraMail getMail() {
            return mail;
        }

        public void setMail(ZimbraMail mail) {
            this.mail = mail;
        }

        private ZimbraMail mail;
    }

    // Utils
    private static class ZimbraMail {
        private int s;
        private long d;
        private String l;
        private String cid;

        public int getS() {
            return s;
        }

        public void setS(int s) {
            this.s = s;
        }

        public long getD() {
            return d;
        }

        public void setD(long d) {
            this.d = d;
        }

        public String getL() {
            return l;
        }

        public void setL(String l) {
            this.l = l;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public int getRev() {
            return rev;
        }

        public void setRev(int rev) {
            this.rev = rev;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFr() {
            return fr;
        }

        public void setFr(String fr) {
            this.fr = fr;
        }

        public List<ZimbraMailExpeditor> getE() {
            return e;
        }

        public void setE(List<ZimbraMailExpeditor> e) {
            this.e = e;
        }

        public String getSu() {
            return su;
        }

        public void setSu(String su) {
            this.su = su;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public long getSd() {
            return sd;
        }

        public void setSd(long sd) {
            this.sd = sd;
        }

        public List<ZimbraMailMp> getMp() {
            return mp;
        }

        public void setMp(List<ZimbraMailMp> mp) {
            this.mp = mp;
        }

        private int rev;
        private String id;
        private String fr;
        private List<ZimbraMailExpeditor> e;
        private String su;
        private String mid;
        private long sd;
        private List<ZimbraMailMp> mp;

        private static class ZimbraMailExpeditor {
            private String a;

            public String getA() {
                return a;
            }

            public void setA(String a) {
                this.a = a;
            }

            public String getD() {
                return d;
            }

            public void setD(String d) {
                this.d = d;
            }

            public String getP() {
                return p;
            }

            public void setP(String p) {
                this.p = p;
            }

            public String getT() {
                return t;
            }

            public void setT(String t) {
                this.t = t;
            }

            private String d;
            private String p;
            private String t;
        }

        private static class ZimbraMailMp {
            private String part;

            public String getPart() {
                return part;
            }

            public void setPart(String part) {
                this.part = part;
            }

            public String getCt() {
                return ct;
            }

            public void setCt(String ct) {
                this.ct = ct;
            }

            public int getS() {
                return s;
            }

            public void setS(int s) {
                this.s = s;
            }

            public boolean isBody() {
                return body;
            }

            public void setBody(boolean body) {
                this.body = body;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getCd() {
                return cd;
            }

            public void setCd(String cd) {
                this.cd = cd;
            }

            public String getFilename() {
                return filename;
            }

            public void setFilename(String filename) {
                this.filename = filename;
            }

            public String getCi() {
                return ci;
            }

            public void setCi(String ci) {
                this.ci = ci;
            }

            public List<ZimbraMailMp> getMp() {
                return mp;
            }

            public void setMp(List<ZimbraMailMp> mp) {
                this.mp = mp;
            }

            private String ct;
            private int s;
            private boolean body;
            private String content;
            private String cd;
            private String filename;
            private String ci;
            private List<ZimbraMailMp> mp;
        }
    }

    private static class ZimbraResponseMail {
        private Object header;

        public ZimbraResponseBody getBody() {
            return body;
        }

        public void setBody(ZimbraResponseBody body) {
            this.body = body;
        }

        public Object getHeader() {
            return header;
        }

        public void setHeader(Object header) {
            this.header = header;
        }

        private ZimbraResponseBody body;

        private static class ZimbraResponseBody {
            public ZimbraGetMsgResponse getGetMsgResponse() {
                return getMsgResponse;
            }

            public void setGetMsgResponse(ZimbraGetMsgResponse getMsgResponse) {
                this.getMsgResponse = getMsgResponse;
            }

            private ZimbraGetMsgResponse getMsgResponse;

            private static class ZimbraGetMsgResponse {
                public List<ZimbraMail> getM() {
                    return m;
                }

                public void setM(List<ZimbraMail> m) {
                    this.m = m;
                }

                private List<ZimbraMail> m;
            }
        }
    }

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

    private static class ZimbraResponseMailPreview {
        private Object header;

        public Object getHeader() {
            return header;
        }

        public void setHeader(Object header) {
            this.header = header;
        }

        public ZimbraResponsePreviewBody getBody() {
            return body;
        }

        public void setBody(ZimbraResponsePreviewBody body) {
            this.body = body;
        }

        private ZimbraResponsePreviewBody body;

        private static class ZimbraResponsePreviewBody {
            public ZimbraSearchResponse getSearchResponse() {
                return searchResponse;
            }

            public void setSearchResponse(ZimbraSearchResponse searchResponse) {
                this.searchResponse = searchResponse;
            }

            private ZimbraSearchResponse searchResponse;

            private static class ZimbraSearchResponse {
                private String sortBy;
                private int offset;

                public List<ZimbraMailPreview> getM() {
                    return m;
                }

                public void setM(List<ZimbraMailPreview> m) {
                    this.m = m;
                }

                public String getSortBy() {
                    return sortBy;
                }

                public void setSortBy(String sortBy) {
                    this.sortBy = sortBy;
                }

                public int getOffset() {
                    return offset;
                }

                public void setOffset(int offset) {
                    this.offset = offset;
                }

                private List<ZimbraMailPreview> m;
            }
        }
    }
}
