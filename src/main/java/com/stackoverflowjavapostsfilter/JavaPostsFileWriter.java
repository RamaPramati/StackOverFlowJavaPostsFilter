package com.stackoverflowjavapostsfilter;

import org.json.simple.JSONObject;

import java.util.Map;

/**
 * Writes posts with required answers to StackOverFlowJavaPosts.txt file
 *
 * Created by ramakrishnas on 29/7/16.
 */
class JavaPostsFileWriter {

    static void writePostsWithRequiredAnswersToFile() {

        for (Map.Entry<String, Map<String,String>> javaPost: StackOverFlowPostsParser.javaPosts.entrySet()) {
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
