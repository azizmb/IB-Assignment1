Infinitely Beta Programming Assignment
======================================

Problem Statement:
------------------

1. Create a web application with a page that always displays two sections:
       (a) form
       (b) report

2. The form should have an URL entry text box and a submit button.

3. User submits the form. If the URL is blank or not an HTTP scheme, the form should be redisplayed with an appropriate message.

4. The URL provided should be fetched behind the scenes. URL fetching should be immediate unless the domain has been hit in the last 5 seconds, in which case the program should delay to ensure that no domain receives a request from this application more than twice in 5 seconds.

5. The URL should be fetched over HTTP behind the scenes. If the result is not HTML, the form should be redisplayed with an appropriate message.

6. The HTML should be parsed for headings (H1, H2, etc.) and a report should be created, consisting of a table listing all heading texts that contain at least 4 English vowels. The table should have two columns; in the first column is the heading level (H1 is 1, H2 is 2, etc.) and in the second column is the text of the heading with any internal HTML tags stripped or escaped. The table rows should be sorted from most vowels to fewest. The table may optionally have a heading row.

7. The report detailed in #6 is the report mentioned in #1. The report is global so another user updating the report updates the one you see in your browser upon refresh as well.

8. The page should have some minor styling for presentation.



Instructions:
-------------

Install Requirements:

This application runs into errors when using Ubuntu's default java-jdk.
	
To change to Sun's jdk:
	
    sudo apt-get install sun-java6-jdk
    sudo update-java-alternatives -s java-6-sun

Install Play framework:

Instructions here: http://www.playframework.org/documentation/1.0.1/install

Install Play Scala plugin:

    play install scala

Run application:

Navigate to project folder

    play install scala


	


Keypoints of solution:
----------------------

- Solution is implemented using two actors.
    - UrlParseActor is used to parse urls. This actor also maintains a map with the time when the url was fetched.
    - ReportActor is used to maintain the global report.


Screenshots:
------------
![screen1](http://i53.tinypic.com/2e16n0x.png)
![screen2](http://i55.tinypic.com/15o8d91.png)
![screen3](http://i52.tinypic.com/34zyrkp.png)



