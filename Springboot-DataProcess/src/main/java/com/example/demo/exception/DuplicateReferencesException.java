package com.example.demo.exception;

public class DuplicateReferencesException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7168236105313984913L;

	/**
	 * @param message
	 * @param errors
	 */
	public DuplicateReferencesException(String message) {

		super(message);

	}

}
