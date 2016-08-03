package com.stackoverflowjavapostsfilter;

import org.json.JSONObject;
import org.w3c.dom.Element;

import java.io.PrintWriter;
import java.util.List;

/**
 * Created by ramakrishnas on 29/7/16.
 */
class JavaPostsFileWriter {

    static void writeToFile(Element postElement, PrintWriter javaPostsWriter) {
        List<String> requiredAnswers = StackOverFlowJavaPostsFilterHelper.getAcceptedAnswerForPost(postElement.
                getAttribute("Id"));

        JSONObject javaPost = new JSONObject();
        javaPost.put("Post Id", postElement.getAttribute("Id"));
        javaPost.put("Title", postElement.getAttribute("Title"));
        javaPost.put("Descrpition", postElement.getAttribute("Body"));

        if (requiredAnswers.size() == 0)
            javaPost.put("No answers found", "");
        else
            javaPost.put("Accepted answer", requiredAnswers.get(0));
        if (requiredAnswers.size() > 1)
            javaPost.put("Most voted answer", requiredAnswers.get(1));

        javaPostsWriter.println(javaPost.toString() + "\n");
        javaPostsWriter.println("========================================================================\n");
    }
}
