package com.stackoverflowjavapostsfilter;

import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Writes posts with required answers to StackOverFlowJavaPosts.txt file
 * <p>
 * Created by ramakrishnas on 29/7/16.
 */
class JavaPostsFileWriter {

    private static final Logger LOGGER = Logger.getLogger(StackOverFlowJavaPostsFilter.class.getName());

    static void writePostsWithRequiredAnswersToFile() {

        for (Map.Entry<String, Map<String, String>> javaPost : StackOverFlowPostsParser.javaPosts.entrySet()) {
            JSONObject javaPostWithRequiredAnswers = new JSONObject();
            Map<String, String> postDetails = javaPost.getValue();
            javaPostWithRequiredAnswers.put("Post Id", postDetails.get("Post Id"));
            javaPostWithRequiredAnswers.put("Title", postDetails.get("Title"));
            javaPostWithRequiredAnswers.put("Description", postDetails.get("Description"));
            JSONObject acceptedAnswerJson = getCodeSnippetsSeparatedAnswer(StackOverFlowPostsParser.javaPostsAnswers.get
                    (postDetails.get("AcceptedAnswerId")));
            javaPostWithRequiredAnswers.put("Accepted Answer", acceptedAnswerJson);
            if (StackOverFlowPostsParser.javaMostVotedAnswers.containsKey(postDetails.get("Post Id"))) {
                JSONObject mostVotedAnswerJson = getCodeSnippetsSeparatedAnswer(StackOverFlowPostsParser.javaMostVotedAnswers.get
                        (postDetails.get("Post Id")).getKey());
                javaPostWithRequiredAnswers.put("Most voted Answer", mostVotedAnswerJson);
            }
            StackOverFlowJavaPostsFilter.javaPostsWriter.println(javaPostWithRequiredAnswers.toString() + "\n");
        }
    }

    private static JSONObject getCodeSnippetsSeparatedAnswer(String answerWithCode) {
        String answer = "";
        int codeSnippetCount = 0;
        JSONObject answerJson = new JSONObject();
        JSONArray codeSnippets = new JSONArray();
        while (answerWithCode != null && answerWithCode.contains("<code>")) {
            int codeSnippetStartingIndex = answerWithCode.indexOf("<code>");
            int codeSnippetLastIndex = answerWithCode.indexOf("</code>");
            if (codeSnippetLastIndex < codeSnippetStartingIndex)
                break;
            codeSnippets.put(codeSnippetCount, answerWithCode.substring(codeSnippetStartingIndex, codeSnippetLastIndex + 6));
            answer = answer.concat(answerWithCode.substring(0, codeSnippetStartingIndex));
            answerWithCode = answerWithCode.substring(codeSnippetLastIndex + 7);
            codeSnippetCount++;
        }
        if (answerWithCode != null)
            answer = answer.concat(answerWithCode);
        answerJson.put("code Snippets", codeSnippets);
        answerJson.put("Answer", answer);
        return answerJson;
    }
}
