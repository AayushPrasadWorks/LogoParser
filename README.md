## Overview
 This application is designed to accept a URL for a webpage and 
 crawl the webpage for images, logos, and images that have a 
 possibility of being logos.

## Stack:

Google's Cloud Vision API, a Machine Learning API, For Logo Detection

Jsoup to crawl through webpages for images and their source links.

Java Servlets to perform the HTTP requests needed to get results from the 
web crawl.

For the front end, I used Javascript, HTML, and CSS. I used a bootstrap template for styling the page.


## Thought Process 
The purpose of the application is to be 
able to find the logos found in a webpage and be able to tell what brands are being represented in these images. 
This information can be valuable to companies, 
as it can tell how visible their brand is to the general public.



## Running the Project
Here we will detail how to setup and run this project so you may get started, as well as the requirements needed to do so.

### Requirements
Before beginning, make sure you have the following installed and ready to use
- Maven 3.5 or higher
- Java 8



### Setup

Before doing anything install Maven, On mac use homebrew to install maven,

>`brew install maven`

To start, open a terminal window and navigate to wherever you unzipped to the root directory `imagefinder`. To build the project, run the command:

>`mvn package`

If all goes well you should see some lines that end with "BUILD SUCCESS". 
When you build your project, maven should build it in the `target` directory. 
To clear this, you may run the command:

>`mvn clean`

To run the project, use the following command to start the server:

>`mvn clean test package jetty:run`

You should see a line at the bottom that says "Started Jetty Server". 
Now, if you enter `localhost:8080` into your browser, you should see the `index.html`
front page.



## Notes
1. The test links are accessible in the UI of the project via a button click
   as well as the test links.
2. I used a text file stored in the root of the project to store the Google Cloud Vision API key. For security reasons, I have not included it in the project.
The API can be inputed manually in the servlet code or stored in your own text file.
   
## Things to improve on
1. Add more statistics based on the images gathered by the application i.e (Most frequent Brand, comparing brand placement with the content of the webpage the image is in, etc)
2. Search for specific company logos within a given webpage

