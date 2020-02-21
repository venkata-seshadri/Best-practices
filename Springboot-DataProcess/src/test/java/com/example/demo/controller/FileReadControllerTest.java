package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import com.example.demo.service.FileReaderServiceImpl;
@RunWith(SpringJUnit4ClassRunner.class)

public class FileReadControllerTest {
		
			
			protected MockMvc mvc;
		   
		   @InjectMocks
		   FileReadController fileReadController;
		   @InjectMocks
		   FileReaderServiceImpl fileReaderServiceImpl;
		   @Before
		public void setUp() throws IOException {
			   MockitoAnnotations.initMocks(this);
			   File file = new ClassPathResource("testFiles/records.csv").getFile();
			   fileReadController.inputFile=file.getAbsolutePath();
			   fileReadController.filRreaderService= fileReaderServiceImpl;
			   this.mvc = MockMvcBuilders.standaloneSetup(fileReadController).build();
		}
		   @Test(expected=NestedServletException.class)
		public void processCustomerRecordTest() throws Exception {
			   
			   MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/processFile")).andReturn();
			   System.out.println(mvcResult.getResponse().getContentAsString());
		}
		   @Test
			public void filenamePatternTest() throws Exception {
			   File file = new ClassPathResource("testFiles/records.csv").getFile();
			   fileReadController.inputFile=file.getAbsolutePath()+"e";
				   MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/processFile")).andReturn();
				   assertEquals(422, mvcResult.getResponse().getStatus());
			}


}
