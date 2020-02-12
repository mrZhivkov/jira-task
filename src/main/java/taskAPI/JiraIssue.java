package taskAPI;

import java.util.HashMap;
import java.util.Map;

import exceptions.JiraAPIException;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

/**
 * This is a simple class which has getter for JSON response and a java.util.Map
 * representing paths to rich text fields. 
 * A better fuller Issue Object type could implement or extend the below interface/class:
 * 
 * https://docs.atlassian.com/software/jira/docs/api/7.4.2/com/atlassian/jira/issue/Issue.html
 * https://docs.atlassian.com/software/jira/docs/api/7.4.2/com/atlassian/jira/issue/AbstractIssue.html
 * 
 * @author momchil
 *
 */
public class JiraIssue {

	private JSONObject responseJsonObject;
	private Map<String, String> richText;

	/**
	 * Constructor for JiraIssue object. We could overload constructor if we need
	 * different types of access or anonymous access.
	 * 
	 * @param host          The host name without slashes e.g.:
	 *                      your-domain.atlassian.net
	 * @param issueIdOrName The name or ID of the issue.
	 * @param username      Your Jira email address/username.
	 * @param authToken     The authentication token generated for your user.
	 * @throws JiraAPIException Checks response codes and throws errors accordingly.
	 */
	public JiraIssue(String host, String issueIdOrName, String username, String authToken) throws JiraAPIException {

		String protocol = "https://";
		String apiEndpoint = "/rest/api/3/issue/";

		HttpResponse<JsonNode> response = Unirest.
										  get(protocol + host + apiEndpoint + issueIdOrName).
										  header("Accept", "application/json").
										  basicAuth(username, authToken).
										  asJson();

		responseJsonObject = response.getBody().getObject();

		int responseCode = response.getStatus();
		
		// As per API documentation anything other than 200 is unsuccessful. 
		if (responseCode != 200) {
			throw new JiraAPIException(response.getBody().toPrettyString());
		}

	}

	/**
	 * Private method used to populate the rich text map. The method iterates over
	 * the response and recursively handles nested objects building the path to the
	 * mapped value. My interpretation of rich text field is quite broad. Perhaps a
	 * better approach would be to also check the JSON schema as we are iterating
	 * over every key-value pair.
	 * 
	 * @param obj The start node of the JSON response.
	 */
	private void populateRichText(JSONObject obj, String path) {
		obj.keySet().forEach(key -> {

			Object val = obj.get(key);
			String currentPath = path + "." + key.toString();

			if (val instanceof JSONObject) {

				populateRichText((JSONObject) val, currentPath);

			} else if (val instanceof JSONArray) {

				handleArray((JSONArray) val, currentPath);

			} else {
				if (isRichText(val)) {

					richText.put(currentPath, (String) val);

				}
			}

		});
	}

	/**
	 * Private method that handles JSONArrays.
	 * 
	 * @param arr
	 * @param path
	 */
	private void handleArray(JSONArray arr, String path) {
		// We only want to handle nested objects.
		// Or nested arrays. Other datatypes are skipped.
		int counter = 0;
		for (Object val : arr){
			
			if (val instanceof JSONObject) {
				populateRichText((JSONObject) val, path + "." + counter);
			} else if (val instanceof JSONArray) {
				handleArray((JSONArray) val, path + "." + counter);
			}
			counter ++; // keep track of arr element, make sure we count from 0
		}
	}

	/**
	 * This method is used to check if a key-value mapping is rich text. We need a
	 * better definition of rich text. For now I am assuming every string is rich
	 * text.
	 * 
	 * @param val the key-value mapping
	 * @return boolean indicating if an object is a rich text field or not
	 */
	private static boolean isRichText(Object val) {
		if (val instanceof String) {
			return true;
		}
		return false;
	}

	/**
	 * Getter for rich text fields. The rich text fields are put into a
	 * java.util.Map which can then be accessed by the user. The key represents the
	 * path to the value in the JSON response. Users can split the key String to be
	 * able to extract the value from the JSON object if needed.
	 * 
	 * @return Key-value mapping containing rich text fields.
	 */
	public Map<String, String> getRichText() {
		if (richText == null) {
			richText = new HashMap<>();
			populateRichText(responseJsonObject, "");
		}
		return richText;
	}

	/**
	 * Getter for the JSON response.
	 * 
	 * @return the JSON response from the API end point.
	 */
	public JSONObject getResponse() {
		return this.responseJsonObject;
	}

}
