package com.stackoverflowjavapostsfilter;

import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Element;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ramakrishnas on 29/7/16.
 */
class JavaPostsFileWriter {

    static Map<String, String> javaPostsAnswers = new HashMap<String, String>();
    static Map<String, Pair<String, Integer>> javaMostVotedAnswers = new HashMap<String, Pair<String, Integer>>();

    static void writeToFile(Element postElement, int postTypeId) {

        JSONObject javaPost = new JSONObject();
        if (postTypeId == 1) {
            javaPost.put("Post Id", postElement.getAttribute("Id"));
            javaPost.put("Title", postElement.getAttribute("Title"));
            javaPost.put("Description", postElement.getAttribute("Body"));
            javaPost.put("AcceptedAnswerId", postElement.getAttribute("AcceptedAnswerId"));
            StackOverFlowJavaPostsFilter.javaPostsQuestionsWriter.println(javaPost.toString() + ",");
        } else if (postTypeId == 2) {
            javaPostsAnswers.put(postElement.getAttribute("Id"), postElement.getAttribute("Body"));
        } else {
            if (javaMostVotedAnswers.containsKey(postElement.getAttribute("ParentId"))) {
                Pair<String, Integer> tempAnswerScore = javaMostVotedAnswers.get(postElement.getAttribute("ParentId"));
                if (tempAnswerScore.getValue() < Integer.valueOf(postElement.getAttribute("Score")))
                    javaMostVotedAnswers.put(postElement.getAttribute("ParentId"),
                            new Pair<String, Integer>(postElement.getAttribute("Body"), Integer.valueOf(postElement.getAttribute("Score"))));
            } else
                javaMostVotedAnswers.put(postElement.getAttribute("ParentId"),
                        new Pair<String, Integer>(postElement.getAttribute("Body"), Integer.valueOf(postElement.getAttribute("Score"))));
        }
    }

    static void writePostsWithRequiredAnswersToFile() throws IOException, ParseException {


        JSONParser parser = new JSONParser();
        JSONArray javaQuestionsJson = (JSONArray) parser.parse(new FileReader(StackOverFlowJavaPostsFilter.
                applicationProperties.getProperty("filteredQuestionsFilePath")));

        for (Object javaQuestionJson : javaQuestionsJson) {
            JSONObject javaPost = new JSONObject();
            JSONObject javaQuestion = (JSONObject) javaQuestionJson;
            javaPost.put("Post Id", javaQuestion.get("Post Id"));
            javaPost.put("Title", javaQuestion.get("Title"));
            javaPost.put("Descrpition", javaQuestion.get("Description"));
            javaPost.put("Accepted Answer", javaPostsAnswers.get(javaQuestion.get("AcceptedAnswerId")));
            if (javaMostVotedAnswers.containsKey(javaQuestion.get("Post Id")))
                javaPost.put("Most voted Answer", javaMostVotedAnswers.get(javaQuestion.get("Post Id")).getKey());
            StackOverFlowJavaPostsFilter.javaPostsWriter.println(javaPost.toString() + "\n");
        }
    }
}
