package it.smartcommunitylab.cartella.asl.exception;

public class ASLCustomException extends Exception {

	public String erroMsg;
	public int errorCode;
	public Response body;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ASLCustomException(String msg) {
		super(msg);
	}

	public ASLCustomException(int errorCode, String msg) {
		super(msg);
		this.errorCode = errorCode;
		this.erroMsg = msg;
		this.body = new Response<Void>(errorCode, msg);
	}

	public Response getBody() {
		return body;
	}

}
