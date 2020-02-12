package examples;

import java.util.Map;

import exceptions.JiraAPIException;
import taskAPI.EditIssueSummary;
import taskAPI.JiraIssue;

public class Examples {
	public static void main(String[] args) throws JiraAPIException {
		
		/**
		 * To run examples make sure to substitute below
		 * values with your own.
		 */
		String host = "<your-project>.atlassian.net";
		String issueIdOrName = "{issueIdOrKey}";
		String username = "email@example.com";
		String authToken = "<api_token>";

		JiraIssue ji = new JiraIssue(host, issueIdOrName, username, authToken);
		
		
		Map<String, String> mm = ji.getRichText();
		
		
		for(String s : mm.keySet()) {
			System.out.println(s + " : " + mm.get(s));
		}
		
		

		EditIssueSummary es = new EditIssueSummary(host, issueIdOrName, username, authToken);
		
		System.out.println(es.updateIssueSummary("Some random new issue text"));
		
	}
}
