package it.smartcommunitylab.cartella.asl.model.users;

public enum ASLAuthError {

	OK("Ok"),
    USER_NOT_FOUND("User not found"),
    WRONG_ROLE("Wrong role"),
    WRONG_ENTITY_ID("Wrong entity id"),
	MISSING_ENTITY_ID("Missing entity id");
	
	private String message;
	
	private ASLAuthError(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return message;
	}
	
}
