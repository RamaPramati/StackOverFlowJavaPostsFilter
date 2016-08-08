Description:

This project is for getting all java questions with thier accepted
answers and most voted answers which are having "how" in their title
from stackoverflow.com

Prerequisites:

Get all posts in stackoverflow.com from
https://archive.org/details/stackexchange. All posts are in the form of
one large (arround 50GB) xml file (Posts.xml). We can use *xml\_split* for
splitting the above file if needed.

Posts.xml file format:

It contains all questions and answers related of stackoverflow posts. To
differentiate between question and answers they used *PostTypeId*. If it
is “1” then it is a question, in case of “2” it is an answer.

We can map each question to its accepted answer by using
*AcceptedAnswerId*. And for getting most voted answer for any question
we can use the *Score* and *ParentId* of an answer.

&lt;row Id="11" PostTypeId="1" AcceptedAnswerId="1248"....Body =
“”....Title="How can relative time be calculated in C\#?"
Tags="&lt;c\#&gt;&lt;datetime&gt;&lt;datediff&gt;&lt;relative-time-span&gt;".....&gt;

&lt;row Id="12" PostTypeId="2" ParentId="11" .....Score="278"
Body="”....&gt;

***Project's** **Input:***

The Posts.xml file which is downloaded from above link.

Project's Ouput:

Output file contains a list of JSON objects. Each JSON object contains a
java questions with accepted answer and most voted answer.

We can cross verify our results using
*http://api.stackexchange.com/2.2/questions/&lt;postId&gt;/answers?order=desc&sort=activity&site=stackoverflow*

application.properties file setup:
// path for Posts.xml file
stackOverFlowPostsDirectory = &lt;input directory path&gt;

filteredPostsFilePath = &lt;output file path&gt;

Code Overview:

Here we have 4 classes,

*1. StackOverFlowJavaPostsFilter:* It is main class. Responsible for
getting all the java questions which are having “how” in their title
from stackoverflow.com with thier accepted answer and most voted answer.

Mainly its doing 3 steps,

a\. Getting all files from given input directory.

b\. Initiates parsing on each and every file with
*StackOverFlowPostsParser*.

c\. After completion of parsing it initiates writing questions with their
required answers into output file with *JavaPostsFileWriter*.

*2. StackOverFlowPostsParser:* Responsible for parsing the given file
and getting question's details, accepted answers and most voted answers.

While parsing it is doing following steps for every parsed post,

a\. If the post is a question (*PostTypeId="1"*) it asks the
*JavaPostsFilter* to check whether the given question is related to java
by giving its details (Tag, Title). If then it saves the quesiton's
details (Id, Title, Body) in to a *javaPosts* map. And it also adds the
quesiton's AccepetdAnswerId to *acceptedAnswerIds* list.

b\. If post is an answer (*PostTypeId="2"*) and acceptedAnswerIds contains
the post id (Id) then it saves the post id (Id) with the answer (Body) into
*javaPostsAnswers* map.

c\. If post is an answer and acceptedAnswerIds not contains that post
Id (Id), then it saves the post's question id (ParentId) with its
score (Score) and answer (Body) into *javaMostVotedAnswers* map. While
saving, it checks if there is an entry already exists in the map with the
current post's question id(ParentId), if then it checks both entry's scores
and overwrites it if the new one's score is more than the existed one's
score.

*3. JavaPostsFileWriter:* Responsible for writing questions with
required answers in the output file.

It creates a JSON object for each question's details in *javaPosts* map, particular question's accepted answer taken from *javaPostsAnswers* map, and particular question's most voted answer taken from *javaMostVotedAnswers* map (if any). Then it writes each resulted JSON object into the given output file.

*4. JavaPostsFilter:* Responsible for filtering java questions which are
having “how” in their title.

It checks whether the given Tag attribute contains “java” and the given
Title attribute contains “how”, If then it returns true else false.


