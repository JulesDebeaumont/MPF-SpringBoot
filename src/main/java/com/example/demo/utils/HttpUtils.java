package com.example.demo.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Arrays;

public class HttpUtils {
    public static void ensureStatusCodeIsOk(int responseCode) throws Exception {
        if (Arrays.asList(200, 201).contains(responseCode)) {
            throw new Exception("Http response is not ok : " + responseCode);
        }
    }

    public static String getConnectionResponseString(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder responseHttpString = new StringBuilder();
        while ((inputLine = reader.readLine()) != null) {
            responseHttpString.append(inputLine);
        }
        reader.close();
        return responseHttpString.toString();
    }

    public static byte[] getConnectionResponseBytes(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        connection.disconnect();
        return byteArrayOutputStream.toByteArray();
    }
}
