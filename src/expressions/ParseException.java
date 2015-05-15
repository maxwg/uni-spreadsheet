package expressions;

public class ParseException extends Exception {
	String message;
	/**
	 * 
	 */
	private static final long serialVersionUID = 7358800878887711116L;
	public ParseException(String s){
		super(s);
		message=s;
	}
	public String getMessage(){
		return message;
	}
}
