package com.stackoverflowjavapostsfilter;

import org.w3c.dom.Element;

/**
 * Created by ramakrishnas on 29/7/16.
 */
class JavaPostsFilter {
    boolean isJavaPost(Element postElement) {
        String postTags = postElement.getAttribute("Tags");
        String postTitle = postElement.getAttribute("Title");
        return postTags.contains("<java>") && (postTitle.toLowerCase().contains("how"));
    }
}
