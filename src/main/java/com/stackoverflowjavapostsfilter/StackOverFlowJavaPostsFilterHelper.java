package com.stackoverflowjavapostsfilter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Created by ramakrishnas on 1/8/16.
 */
class StackOverFlowJavaPostsFilterHelper {

    private static final Logger LOGGER = Logger.getLogger(StackOverFlowJavaPostsFilterHelper.class.getName());

    private static HttpClient client = new HttpClient(new SimpleHttpConnectionManager());

    private static GetMethod getResponseAsString(String url) {
        GetMethod method = new GetMethod(url);
        try {
            method.setRequestHeader("Accept-Encoding", "false");
            client.executeMethod(method);
        } catch (IOException ioException) {
            LOGGER.info("IO Exception " + ioException.getMessage());
            return null;
        }
        if (method.getStatusCode() == 200)
            return method;
        else {
            LOGGER.info("Request failed with status:" + method.getStatusCode());
            return null;
        }
    }

    private static String getDecodedString(GetMethod method) {
        BufferedReader bufferedReader;
        String answersJson = "";
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(method.getResponseBody()));
            bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                answersJson += line;
            return answersJson;
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            LOGGER.info("Unsupported encoded exception" + unsupportedEncodingException.getMessage());
        } catch (IOException ioException) {
            LOGGER.info("IO exception" + ioException.getMessage());
        }
        return answersJson;
    }

    static List<String> getAcceptedAnswerForPost(String postId) {
        GetMethod encodedAnswersResponse = getResponseAsString("https://api.stackexchange.com/2.2/questions/"
                + postId + "/answers?order=desc&sort=activity&site=stackoverflow&filter=!9YdnSMKKT&key=" +
                StackOverFlowJavaPostsFilter.applicationProperties.getProperty("key"));
        JSONObject jsonResponse = new JSONObject(getDecodedString(encodedAnswersResponse));
        JSONArray answersJson = (JSONArray) jsonResponse.get("items");
        int mostVotedAnswerIndex = 0;
        int maxVotesForAnswer = 0;
        int acceptedAnswerIndex = 0;
        List<String> requiredAnswers = new ArrayList<String>();
        int noOfAnswers = answersJson.length();
        for (int answersIndex = 0; answersIndex < noOfAnswers; ++answersIndex) {
            JSONObject answerJson = answersJson.getJSONObject(answersIndex);
            if (maxVotesForAnswer < answerJson.getInt("score")) {
                maxVotesForAnswer = answerJson.getInt("score");
                mostVotedAnswerIndex = answersIndex;
            }
            if (answerJson.getBoolean("is_accepted")) {
                acceptedAnswerIndex = answersIndex;
                requiredAnswers.add(answerJson.getString("body"));
            }
        }
        if (jsonResponse.getInt("quota_remaining") == 0) {
            LOGGER.info("Reached per day quota limit...");
            StackOverFlowJavaPostsFilter.isRatelimitReached = true;
        }
        if (mostVotedAnswerIndex != acceptedAnswerIndex)
            requiredAnswers.add(answersJson.getJSONObject(mostVotedAnswerIndex).getString("body"));
        return requiredAnswers;
    }
}
