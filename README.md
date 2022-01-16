## Overview
 This application is decied to accept a url for a webpage and 
 rawl the webpage for images, logos, and images that have a 
 possiblity of being logos.

## Stack

For the application, I used a variaty of tools.

For logo detection, I used Google's Cloud Vision API, a Machine Learing API,
 to see if an image is a logo, and to identify the brand the logo represents.

I used Jsoup to crawl through webpages for images and their source links.

I used Javax Servlets to preform the HTTP requests needed to get results from the 
web crawl.

For the front end, I used Javascript, HTML, and CSS. I used a bootstrap template for styling the page.


## Thought Process 
 I wanted my application to be 
able to find the logos found in a webpage and be able to tell what brands are being represented in these images. 
This information can be valuable to companies, 
as it can tell how visible their brand is to the general public.
I hope in the future to get more information from the logos. such as the most prevalent brands in the webpage, as well as documenting trends over a series of webpages.


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

If all goes well you should see some lines that ends with "BUILD SUCCESS". 
When you build your project, maven should build it in the `target` directory. 
To clear this, you may run the command:

>`mvn clean`

To run the project, use the following command to start the server:

>`mvn clean test package jetty:run`

You should see a line at the bottom that says "Started Jetty Server". 
Now, if you enter `localhost:8080` into your browser, you should see the `index.html`
 welcome page! If all has gone well to this point, you're ready to begin!



## Important notes
1. The test links are accessible in the UI of the project via a button click
   as well as the test-links.
2. I used a text file stored in the root of the project to store the Google Cloud Vision API key. For security reasons, I have not included it in the project.
The API can be inputed manually in the servlet code or stored in your own text file.
   


