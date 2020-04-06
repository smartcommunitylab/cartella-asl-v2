package it.smartcommunitylab.cartella.asl.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.cartella.asl.exception.ASLCustomException;

@Component
public class HttpsUtils {

	/** USER AGENT. **/
	private String USER_AGENT = "Mozilla/5.0";
	/** HTTP Client. **/
	private HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());

	/**
	 * Http GET.
	 * 
	 * @param url
	 * @param contentType
	 * @param accept
	 * @param auth
	 * @param secure
	 * @return
	 * @throws Exception
	 */
	public String sendGET(String url, String contentType, String accept, String auth, int readTimeout)
			throws Exception {

		URL obj = new URL(url);
		StringBuffer response = new StringBuffer();
		boolean secure = false;
		if (url.toLowerCase().startsWith("https://")) {
			secure = true;
		}
		// HTTPS.
		if (secure) {
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");
			// read timeout.
			if (readTimeout != -1) {
				con.setReadTimeout(readTimeout);
			}
			// add request header
			con.setRequestProperty("User-Agent", this.USER_AGENT);
			if (accept != null && !(accept.isEmpty())) {
				con.setRequestProperty("Accept", accept);
			}
			if (contentType != null && !(contentType.isEmpty())) {
				con.setRequestProperty("Content-Type", contentType);
			}
			if (auth != null && !(auth.isEmpty())) {
				con.setRequestProperty("Authorization", auth);
			}
			
			
			// redirection checking
			boolean redirect = false;
			if (con.getResponseCode() >= 300 && con.getResponseCode() <= 307 && con.getResponseCode() != 306) {
				do {
					redirect = false; // reset the value each time
					String loc = con.getHeaderField("Location"); // get location of
																	// the redirect
					if (loc == null) {
						redirect = false;
						continue;
					}
					con = (HttpsURLConnection) new URL(null, loc, new sun.net.www.protocol.https.Handler()).openConnection();
					con.setRequestProperty("Accept", "application/json");
					con.setRequestProperty("Content-Type", "application/json");
					
					if (auth != null && !(auth.isEmpty())) {
						con.setRequestProperty("Authorization", auth);
					}
					
					if (con.getResponseCode() != 500) { // 500 = fail
						if (con.getResponseCode() >= 300 && con.getResponseCode() <= 307
								&& con.getResponseCode() != 306) {
							redirect = true;
						}
						// I do special handling here with cookies
						// if you need to bring a session cookie over to the
						// redirected page, this is the place to grab that info
					}
				} while (redirect);
			}

			if (con.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + con.getResponseCode());
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			if (con.getResponseCode() >= 400) {
				throw new ASLCustomException(con.getResponseCode(), con.getResponseMessage());
			}

		} else {
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");
			
			// read timeout.
			if (readTimeout != -1) {
				con.setReadTimeout(readTimeout);
			}
			// add request header
			if (accept != null && !(accept.isEmpty())) {
				con.setRequestProperty("Accept", accept);
			}
			if (contentType != null && !(contentType.isEmpty())) {
				con.setRequestProperty("Content-Type", contentType);
			}
			if (auth != null && !(auth.isEmpty())) {
				con.setRequestProperty("Authorization", auth);
			}
			
			// redirection checking
			boolean redirect = false;
			if (con.getResponseCode() >= 300 && con.getResponseCode() <= 307 && con.getResponseCode() != 306) {
				do {
					redirect = false; // reset the value each time
					String loc = con.getHeaderField("Location"); // get location of
																	// the redirect
					if (loc == null) {
						redirect = false;
						continue;
					}
					con = (HttpURLConnection) new URL(null, loc, new sun.net.www.protocol.https.Handler()).openConnection();
					con.setRequestProperty("Accept", "application/json");
					con.setRequestProperty("Content-Type", "application/json");
					
					if (auth != null && !(auth.isEmpty())) {
						con.setRequestProperty("Authorization", auth);
					}

					if (con.getResponseCode() != 500) { // 500 = fail
						if (con.getResponseCode() >= 300 && con.getResponseCode() <= 307
								&& con.getResponseCode() != 306) {
							redirect = true;
						}
						// I do special handling here with cookies
						// if you need to bring a session cookie over to the
						// redirected page, this is the place to grab that info
					}
				} while (redirect);
			}

			if (con.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + con.getResponseCode());
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			if (con.getResponseCode() >= 400) {
				throw new ASLCustomException(con.getResponseCode(), con.getResponseMessage());
			}
		}

		return response.toString();
	}

	// HTTP POST request
	public String sendPOSTSAA(String url, String accept, String contentType, String token, String json) throws Exception {

		String result = null;
		StringRequestEntity requestEntity = new StringRequestEntity(json, contentType, "UTF-8");
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestEntity(requestEntity);
		postMethod.addRequestHeader("SAA_TOKEN", token);
		try {
			int statusCode = httpClient.executeMethod(postMethod);
			if (statusCode != 200) {
				throw new ASLCustomException(statusCode, postMethod.getResponseBodyAsString());
			}
			result = "ok";
		} finally {
			postMethod.releaseConnection();
		}
		return result;

	}
}
