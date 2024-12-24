package me.nyaruko166.michosauto.util;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HttpUtil {

    public static String APPLICATION_JSON = "Accept: application/json";
    public static List<String> HoyoLab = List.of(
            "Referer: https://act.hoyolab.com",
            "x-rpc-app_version: 1.5.0",
            "x-rpc-client_type: 5",
            "x-rpc-language: en-us",
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
    );

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
//            .followRedirects(true)
            .build();

    public static String getRequest(String url, @Nullable Headers headers) {
        if (headers == null) headers = new Headers.Builder().build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .headers(headers)
                .build();
        return executeRequest(request);
    }

    public static String postRequest(String url, RequestBody requestBody, Headers headers) {
        if (headers == null) headers = new Headers.Builder().build();

        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(requestBody)
                .build();

        return executeRequest(request);
    }

    public static String putRequest(String url, RequestBody requestBody, @Nullable Headers headers) {
        if (headers == null) headers = new Headers.Builder().build();

        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .put(requestBody)
                .build();

        return executeRequest(request);
    }

    public static String urlParamBuilder(String baseUrl, List<String> params) {

        Map<String, String> mapParams = params.stream()
                                              // Split each string by ":" into key-value pairs
                                              .map(entry -> entry.split(":", 2))
                                              .collect(Collectors.toMap(
                                                      parts -> parts[0].trim(), // Trim key
                                                      parts -> parts[1].trim()  // Trim value
                                              ));

        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();
        for (Map.Entry<String, String> entry : mapParams.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        return urlBuilder.build().toString();
    }

    public static RequestBody requestBodyBuilder(String jsonBody) {

        return RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));

    }

    public static Headers headersBuilder(List<String> lstHeaders) {
        List<String> mergedHeaders = new ArrayList<>(HoyoLab);
        mergedHeaders.addAll(lstHeaders);
        Headers.Builder builder = new Headers.Builder();
        mergedHeaders.forEach(builder::add);
        return builder.build();
    }

    private static String executeRequest(Request request) {
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                // Response from the API
                return response.body().string();
            } else {
                System.out.printf("Failed call api: %s \n", response);
            }
        } catch (IOException e) {
            System.out.printf(e.getMessage());
        }
        return null;
    }

}
