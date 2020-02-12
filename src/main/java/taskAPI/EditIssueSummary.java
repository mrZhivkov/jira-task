package taskAPI;

import exceptions.JiraAPIException;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

/**
 * Simple class to update the summary of an issue.
 * First the class is initialized with host, issueId,
 * username and authToken. To update the summary one must
 * call the updateIssueSummary method.
 * @author momchil
 *
 */
public class EditIssueSummary {

	private String protocol;
	private String apiEndpoint;
	private String host;
	private String issueIdOrName;
	private String username;
	private String authToken;
	
	private JSONObject responseObj;
	/**
	 * Constructor for EditIssueSummary.
	 * Constructor and update method can be overloaded to handle 
	 * different types of authentication.
	 * 
	 * @param host          The host name without slashes e.g.:
	 *                      your-domain.atlassian.net
	 * @param issueIdOrName The name or ID of the issue.
	 * @param username      Your Jira email address/username.
	 * @param authToken     The authentication token generated for your user.
	 */
	public EditIssueSummary(String host, String issueIdOrName, String username, String authToken) {
		this.protocol = "https://";
		this.apiEndpoint = "/rest/api/3/issue/";
		this.host = host;
		this.issueIdOrName = issueIdOrName;
		this.username = username;
		this.authToken = authToken;

	}
	
	/**
	 * Getter for response from API endpoint.
	 * @return the JSON representation of the response.
	 * @throws JiraAPIException if response is null.
	 */
	public JSONObject getResponse() throws JiraAPIException{
		
		if (responseObj == null) {
			throw new JiraAPIException("Response object is null.");
		}
		
		return this.responseObj;
	}
	/**
	 * This method makes a put request to the API endpoint.
	 * Updates the summary of an issue.
	 * @param newSummaryText the text to set the value of the summary to.
	 * @return The response status code returned by the API.
	 */
	public int updateIssueSummary(String newSummaryText) {
		JSONObject payload = new JSONObject();
		JSONObject summaryObj = new JSONObject();
		JSONArray summaryArray = new JSONArray();
		JSONObject newSummary = new JSONObject();

		newSummary.put("set", newSummaryText);
		summaryArray.put(newSummary);
		summaryObj.put("summary", summaryArray);
		payload.put("update", summaryObj);

		HttpResponse<JsonNode> response = Unirest.
										  put(protocol + host + apiEndpoint + issueIdOrName).
										  header("Accept", "application/json").
										  header("Content-Type", "application/json").
										  basicAuth(username, authToken).
										  body(payload).asJson();
		
		
		int statusCode = response.getStatus();
		responseObj = response.getBody().getObject();
		return statusCode;
	}

}
