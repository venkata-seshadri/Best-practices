package com.example.demo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.example.demo.exception.DuplicateReferencesException;

@RunWith(SpringJUnit4ClassRunner.class)
//@TestPropertySource("classpath:application-default.properties")

public class FileReaderServiceImplTest {
	@InjectMocks
	private FileReaderServiceImpl fileReaderServiceImpl;
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		//fileReaderServiceImpl = new FileReaderServiceImpl();
	}
	
	@Test(expected=DuplicateReferencesException.class)
	public void handleCSVDuplicateReferenceTest() throws IOException {
		File file = new ClassPathResource("testFiles/duplicateRecords.csv").getFile();
		Path path = Paths.get(file.getAbsolutePath());
		Stream<String> stream = Files.lines(path);
		fileReaderServiceImpl.handleCSV(stream);
	}
	@Test
	public void handleCSVTest() throws IOException {
		File file = new ClassPathResource("testFiles/records.csv").getFile();
		Path path = Paths.get(file.getAbsolutePath());
		Stream<String> stream = Files.lines(path);
		fileReaderServiceImpl.outputFilePath=file.getParent()+"\\output.csv";
		Assert.hasText("Success", fileReaderServiceImpl.handleCSV(stream));
	}
	@Test(expected=DuplicateReferencesException.class)
	public void handleXMLDuplicateReferenceTest() throws IOException {
		File file = new ClassPathResource("testFiles/duplicateRecords.xml").getFile();
		fileReaderServiceImpl.handleXML(file.getAbsolutePath());
	}
	@Test
	public void handleXMLTest() throws IOException {
		File file = new ClassPathResource("testFiles/records.xml").getFile();
		fileReaderServiceImpl.outputFilePath=file.getParent()+"\\output.csv";
		fileReaderServiceImpl.handleXML(file.getAbsolutePath());
	}
	
}
