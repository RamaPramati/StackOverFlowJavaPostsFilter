package com.stackoverflowjavapostsfilter;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ramakrishnas on 29/7/16.
 */
class JavaPostsFileWriter {
    static Map<String, Map<String,String>> javaPosts = new HashMap<String, Map<String,String>>();

    static void writePostsWithRequiredAnswersToFile() {

        for (Map.Entry<String, Map<String,String>> javaPost: javaPosts.entrySet()) {
            JSONObject javaPostWithRequiredAnswers = new JSONObject();
            Map<String,String> postDetails = javaPost.getValue();
            javaPostWithRequiredAnswers.put("Post Id", postDetails.get("Post Id"));
            javaPostWithRequiredAnswers.put("Title", postDetails.get("Title"));
            javaPostWithRequiredAnswers.put("Descrpition", postDetails.get("Description"));
            javaPostWithRequiredAnswers.put("Accepted Answer", StackOverFlowPostsParser.javaPostsAnswers.get(postDetails.get("AcceptedAnswerId")));
            if (StackOverFlowPostsParser.javaMostVotedAnswers.containsKey(postDetails.get("Post Id")))
                javaPostWithRequiredAnswers.put("Most voted Answer", StackOverFlowPostsParser.javaMostVotedAnswers.get(postDetails.get("Post Id")).getKey());
            StackOverFlowJavaPostsFilter.javaPostsWriter.println(javaPostWithRequiredAnswers.toString() + "\n");
        }
    }
}
