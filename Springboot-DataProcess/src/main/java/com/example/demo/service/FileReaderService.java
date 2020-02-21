package com.example.demo.service;

import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;

public interface FileReaderService {

	public String handleCSV(Stream stream) throws IOException;
	public String handleXML(String file) throws IOException;
	
}
