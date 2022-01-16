package com.logoFinder.project.serverCode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@WebServlet(name = "ImageFinder", urlPatterns = { "/main" })
public class ImageFinder extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected static final Gson GSON = new GsonBuilder().create();

	private static final String TARGET_URL = "https://vision.googleapis.com/v1/images:annotate?";
	
	//API key for Google Vision API. Insert your own key in a file or hardcode it here
	private static String API_KEY;

	// JSON array denoting the links of images found in a webpage
	private JsonArray images;
	// JSON array denoting the links of logos found in a webpage
	private JsonArray logoList;
	// JSON array denoting the links of images with possible logos in them found in
	// a webpage
	private JsonArray posLogoList;

	private double minScore;

	/*
	 * This method preforms POST requests from the User and returns a JSON
	 * with the image and logo URLS along with other information
	 * 
	 */

	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/json");
		prepApiKey();
		this.images = new JsonArray();
		this.logoList = new JsonArray();
		this.posLogoList = new JsonArray();
		String path = req.getServletPath();
		String url = req.getParameter("url");
		this.minScore = Double.parseDouble(req.getParameter("score"));
		System.out.println("Got request of:" + path + " with query param:" + url);
		// System.out.println(GSON.toJson(getImageSoruces(url).toArray()));
		JsonObject imageInfo = new JsonObject();

		getImageSoruces(url);

		imageInfo.add("logos", this.logoList);
		imageInfo.add("images", this.images);
		imageInfo.add("possLogos", this.posLogoList);
		System.out.println(imageInfo);
		resp.getWriter().print(imageInfo);
		// System.out.println("Image Links: " + listOfImages);
		// System.out.println("Logo Links: " + logoLinks);

	}

	/*
	 * This method is designed to filter any possible pictures found in a webpage
	 * given through a POST request for images, logos and images that could be
	 * logos.
	 * 
	 */
	public void getImageSoruces(String url) {
		Document doc = null;
		System.out.println();

		try {
			doc = Jsoup.connect(url).get();
			Elements links = doc.getElementsByTag("img");

			for (Element img : links) {

				String link = img.attr("src");
				// System.out.println(link);
				if (containsExtension(link)) {
					if (isLogo(link)) {
						// System.out.println("LOGO");
					} else {
						JsonObject urlObject = new JsonObject();
						urlObject.addProperty("url", link);

						this.images.add(urlObject);
						// listOfImages.add(link);
					}

				}

			}
			// return listOfImages;
		} catch (IOException e) {

		}
	}

	/*
	 * This method is designed to see if the given image link can be a
	 * logo or not. It returns a boolean denoting if an image is a logo
	 * 
	 * This method make an api call to the Google Cloud Vision API
	 * and runs logo detection on the provided image sourced by the given link
	 * 
	 */

	private void prepApiKey() {
		API_KEY = "key=";
		File keyFile = new File("api_key.txt");
		try {
			keyFile.createNewFile();
		} catch (IOException e) {
			System.out.println("no api key exists");
			e.printStackTrace();
		}
		Scanner scanner;
		try {
			scanner = new Scanner(keyFile);
			while (scanner.hasNextLine()) {
				API_KEY+=scanner.nextLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(API_KEY.equals("key=")){
			System.out.println("API key not stored");
		}
		
	}

	private boolean isLogo(String link) throws IOException {
		URL serverUrl = new URL(TARGET_URL + API_KEY);
		URLConnection urlConnection = serverUrl.openConnection();
		HttpURLConnection con = (HttpURLConnection) urlConnection;
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setDoOutput(true);
		String reqLink = "\"" + link + "\"";
		BufferedWriter httpRequestBodyWriter = new BufferedWriter(
				new OutputStreamWriter(con.getOutputStream()));
		httpRequestBodyWriter.write("{\"requests\":  [{ \"features\":  [ {\"type\": \"LOGO_DETECTION\""
				+ "}], \"image\": {\"source\": { \"imageUri\":"
				+ reqLink + "}}}]}");
		httpRequestBodyWriter.close();
		// System.out.println("RequestBody: " + httpRequestBodyWriter);

		// System.out.println("Response: " + con.getInputStream());
		if (con.getInputStream() == null) {

			return false;
		}
		Scanner httpResponseScanner = new Scanner(con.getInputStream());
		String resp = " ";
		while (httpResponseScanner.hasNext()) {
			String line = httpResponseScanner.nextLine();
			resp += line;

		}

		return checkImageForLogo(resp, httpResponseScanner, link);

		// String logoProb = logoData.get("score").toString();

	}

	/*
	 * This method is designed to see if the given image link can be a
	 * logo, a possible logo or not depending on the accruacy of a logo being
	 * contained in the
	 * photo.
	 * It return a boolean stating if an image is a logo or a possible logo
	 * If the image has a logo detection accruacy chance equal or greater than the
	 * minimum score given by the user, it will be
	 * classified as a logo.
	 * A possible logo is an image if there is any logo that can be found in the
	 * image
	 * regardless of accuracy
	 * 
	 * 
	 */
	private boolean checkImageForLogo(String resp, Scanner httpResponseScanner, String link) {
		JsonParser parser = new JsonParser();
		JsonElement logoDataTree = parser.parse(resp);

		if (logoDataTree.isJsonObject()) {

			JsonObject responseObject = logoDataTree.getAsJsonObject();

			JsonElement responseLevel = responseObject.get("responses");

			if (responseLevel.isJsonArray()) {
				JsonArray possibleLogos = responseLevel.getAsJsonArray();
				// System.out.println("Logo Data: " + possibleLogos.toString());
				if (possibleLogos.size() == 0) {
					// System.out.println("No Logo Annotations: " + possibleLogos.toString());
					return false;
				}
				JsonElement logos = possibleLogos.get(0);
				// System.out.println("First Element of Data: " + logos);
				if (logos.isJsonObject()) {

					JsonObject logo = logos.getAsJsonObject();

					if (logo.has("logoAnnotations")) {

						JsonElement closestLogo = logo.get("logoAnnotations").getAsJsonArray().get(0);
						// System.out.println("Nested Data" + closestLogo.toString());
						JsonObject logoStats = closestLogo.getAsJsonObject();
						double score = logoStats.get("score").getAsDouble();
						String brand = logoStats.get("description").getAsString();

						if (score >= this.minScore) {
							System.out.println("Score: " + score);
							System.out.println("Brand: " + brand);
							JsonObject urlObject = new JsonObject();
							urlObject.addProperty("url", link);
							urlObject.addProperty("brand", brand);
							this.logoList.add(urlObject);
							return true;
						}

						JsonObject urlObject = new JsonObject();
						urlObject.addProperty("url", link);
						urlObject.addProperty("brand", brand);
						this.posLogoList.add(urlObject);
						return true;
					}
					return false;

				}

				return false;
			}
			httpResponseScanner.close();
			return false;
		} else {
			// System.out.println("Not Good Data");
			httpResponseScanner.close();
			return false;
		}

	}

	/*
	 * This method is designed to see if an image can be openned as a standard link
	 * with https://. This is to ensure that all images can be displayed to the user
	 */
	public boolean containsExtension(String link) {

		if (link.contains("https://")) {
			return true;
		}
		return false;

	}

}
