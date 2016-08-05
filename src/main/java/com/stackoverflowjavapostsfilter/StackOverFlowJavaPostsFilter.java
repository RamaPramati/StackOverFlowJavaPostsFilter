package com.stackoverflowjavapostsfilter;

import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by ramakrishnas on 28/7/16.
 */
public class StackOverFlowJavaPostsFilter {

    private static final Logger LOGGER = Logger.getLogger(StackOverFlowJavaPostsFilter.class.getName());

    static Properties applicationProperties = new Properties();

    static PrintWriter javaPostsWriter;
    static PrintWriter javaPostsQuestionsWriter;

    public static void main(String args[]) {
        JavaPostsFilter javaPostsFilter = new JavaPostsFilter();

        try {
            applicationProperties.load(new FileInputStream("application.properties"));

            File stackOverFlowPostsFolder = new File(applicationProperties.getProperty("stackOverFlowPostsDirectory"));

            javaPostsWriter = new PrintWriter(applicationProperties.getProperty("filteredPostsFilePath"));
            javaPostsQuestionsWriter = new PrintWriter(applicationProperties.getProperty("filteredQuestionsFilePath"));

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            File[] postsFiles = stackOverFlowPostsFolder.listFiles();
            List<String> acceptedAnswerIds = new ArrayList<String>();
            List<String> javaPostsIds = new ArrayList<String>();

            if (postsFiles == null) {
                LOGGER.info("No files in the given directory");
                return;
            }
            javaPostsQuestionsWriter.println("[");

            for (File postsFile : postsFiles) {
                LOGGER.info("Processing file " + postsFile);
                Document postsDocument = dBuilder.parse(postsFile);
                postsDocument.getDocumentElement().normalize();
                NodeList postsDetails = postsDocument.getElementsByTagName("row");

                for (int postDetailsIndex = 0; postDetailsIndex < postsDetails.getLength(); postDetailsIndex++) {
                    Node post = postsDetails.item(postDetailsIndex);
                    if (post.getNodeType() == Node.ELEMENT_NODE) {
                        Element postElement = (Element) post;
                        if ("1".equals(postElement.getAttribute("PostTypeId")) && (javaPostsFilter.isJavaPost(postElement))) {
                            JavaPostsFileWriter.writeToFile(postElement, 1);
                            acceptedAnswerIds.add(postElement.getAttribute("AcceptedAnswerId"));
                            javaPostsIds.add(postElement.getAttribute("Id"));
                        } else if (acceptedAnswerIds.contains(postElement.getAttribute("Id"))) {
                            JavaPostsFileWriter.writeToFile(postElement, 2);
                        } else if (javaPostsIds.contains(postElement.getAttribute("ParentId"))) {
                            JavaPostsFileWriter.writeToFile(postElement, 3);
                        }
                    }
                }
            }
            javaPostsQuestionsWriter.print("]");
            javaPostsQuestionsWriter.close();
            JavaPostsFileWriter.writePostsWithRequiredAnswersToFile();

        } catch (SAXException saxException) {
            LOGGER.info("Failed to parse xml..." + saxException.getMessage());
        } catch (FileNotFoundException fileNotFoundException) {
            LOGGER.info("File not found..." + fileNotFoundException.getMessage());
        } catch (ParserConfigurationException parserConfigurationException) {
            LOGGER.info("Failure in parser configuration...." + parserConfigurationException.getMessage());
        } catch (IOException ioException) {
            LOGGER.info("IOException" + ioException.getMessage());
        } catch (ParseException parseException) {
            LOGGER.info("ParseException" + parseException.getMessage());
        } finally {
            javaPostsWriter.close();
        }
    }

}
