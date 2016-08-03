package com.stackoverflowjavapostsfilter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by ramakrishnas on 28/7/16.
 */
public class StackOverFlowJavaPostsFilter {

    private static final Logger LOGGER = Logger.getLogger(StackOverFlowJavaPostsFilter.class.getName());

    static Properties applicationProperties = new Properties();

    private static PrintWriter javaPostsWriter = null;
    static boolean isRatelimitReached = false;

    public static void main(String args[]) {
        JavaPostsFilter javaPostsFilter = new JavaPostsFilter();

        try {
            System.out.println(new File(".").getAbsolutePath());
            applicationProperties.load(new FileInputStream("application.properties"));
            File stackOverFlowPostsFolder = new File(applicationProperties.getProperty("stackOverFlowPostsDirectory"));
            StackOverFlowJavaPostsFilter.javaPostsWriter = new PrintWriter(StackOverFlowJavaPostsFilter.applicationProperties.
                    getProperty("filteredPostsFilePath"));

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            File[] postsFiles = stackOverFlowPostsFolder.listFiles();

            if (postsFiles == null) {
                LOGGER.info("No files in the given directory");
                return;
            }

            for (File postsFile : postsFiles) {
                Document postsDocument = dBuilder.parse(postsFile);
                postsDocument.getDocumentElement().normalize();
                NodeList postsDetails = postsDocument.getElementsByTagName("row");

                for (int postDetailsIndex = 0; postDetailsIndex < postsDetails.getLength(); postDetailsIndex++) {
                    if (isRatelimitReached) {
                        System.exit(1);
                    }
                    Node post = postsDetails.item(postDetailsIndex);
                    if (post.getNodeType() == Node.ELEMENT_NODE) {
                        Element postElement = (Element) post;
                        if ((javaPostsFilter.isJavaPost(postElement)))
                            JavaPostsFileWriter.writeToFile(postElement, javaPostsWriter);
                    }
                }
            }
        } catch (SAXException saxException) {
            LOGGER.info("Failed to parse xml..." + saxException.getMessage());
        } catch (FileNotFoundException fileNotFoundException) {
            LOGGER.info("File not found..." + fileNotFoundException.getMessage());
        } catch (ParserConfigurationException parserConfigurationException) {
            LOGGER.info("Failure in parser configuration...." + parserConfigurationException.getMessage());
        } catch (IOException ioException) {
            LOGGER.info("" + ioException.getMessage());
        } finally {
            javaPostsWriter.close();
        }
    }
}