package exceptions;

public class JiraAPIException extends Exception {

	private static final long serialVersionUID = -8780445038946183190L;

	public JiraAPIException(String errorMessage) {
		super(errorMessage);
	}

}
