package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.FileReaderService;

@RestController
public class FileReadController {

	private static final Logger logger = LoggerFactory.getLogger(FileReadController.class);
	@Autowired
	private Environment environment;
	@Autowired
	 FileReaderService filRreaderService;
	@Value("${spring.application.inputFilePath}")
	public String inputFile;
	
	private String[] validFormates= {"csv","xml"};
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/processFile")
	public ResponseEntity processCustomerRecords() throws IOException {
		logger.debug(" input file location :: FileReadController:  "+inputFile);
		String fileEx = StringUtils.substringAfter(inputFile,".");
		Path path = Paths.get(inputFile);
		logger.debug(" file extension :: FileReadController:  "+fileEx);
		if(!ArrayUtils.contains(validFormates, fileEx))
			return new ResponseEntity(fileEx+" Formate Not Supporting :: Supportin formates(csv,xml)",HttpStatus.UNPROCESSABLE_ENTITY);
		
		Stream<String> stream = Files.lines(path) ;
		switch (fileEx.toUpperCase()) {
		case "CSV":
			  filRreaderService.handleCSV(stream);
			 
		case "XML":
			 filRreaderService.handleXML(inputFile);
		
		}
		return new ResponseEntity<>("CSV Report Created at configured output location",HttpStatus.OK);
	}
}
