package com.example.demo.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.example.demo.entity.Record;
import com.example.demo.exception.DuplicateReferencesException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Component
public class FileReaderServiceImpl implements FileReaderService {

	private static final Logger logger = LoggerFactory.getLogger(FileReaderServiceImpl.class);
	
	@Value("${spring.application.outputFilePath}")
	public String outputFilePath;
	
	/** Handles stream of input csv file and 
	 * throws duplicate exception if duplicate references found.
	 * @param stream its input file data stream
	 * @return  success message
	 * @throws IOException 
	 * **/

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String handleCSV(Stream stream) throws IOException {
		
		List<List<String>> input = (List<List<String>>) stream.skip(1).map(l -> Arrays.asList(((String) l).split(",")))
				.collect(Collectors.toList());
		logger.debug("list of splitted values list ::FileReaderServiceImpl "+ input);
		
		List<List<String>> duplicateReferences = input.stream()
				.filter(n -> input.stream().filter(x -> x.get(0).equals(n.get(0))).count() > 1)
				.collect(Collectors.toList());
		logger.debug("list of duplicateReference records ::FileReaderServiceImpl "+ duplicateReferences);
		if (duplicateReferences.size() > 0) 
			throw new DuplicateReferencesException("Duplicate References Found from Input file");
		
		/* Collect unmatched start and mutation amount with end balance
		 * if records found writing csv report at output location 
		 */
		
		List<Map<String, String>> listRecords = new ArrayList<>();
		List<List<String>> amountDiffRecords = input.stream().filter(e -> {
			BigDecimal amount = new BigDecimal(e.get(3)).add(new BigDecimal(e.get(4)));
			boolean diff = amount.equals(new BigDecimal(e.get(5)));
			if (!diff) {
				Map<String, String> failedRecords = new LinkedHashMap();
				failedRecords.put("Reference", e.get(0));
				failedRecords.put("Description", e.get(2));
				listRecords.add(failedRecords);
			}
			return !diff;
		}).collect(Collectors.toList());

		if (listRecords.size() > 0) {
			logger.debug("list unmatched end amount records ::FileReaderServiceImpl "+ listRecords.size());
			createOutputCSVFile(listRecords);
			return "Successfully Created Output csv report file";
		}

		return "Success";
	}
	/** Handles stream of input xml file and 
	 * throws duplicate exception if duplicate references found.
	 * @param  its input file path string
	 * @return  success message
	 * @throws IOException 
	 * **/
	@SuppressWarnings("rawtypes")
	@Override
	public String handleXML(String filePath) throws IOException {
		List<Record> input = readRecordsFromXML(filePath);
		List<Record> duplicateRecords = input.stream().collect(Collectors.groupingBy(Record::getReference))
				.entrySet().stream().filter(e -> e.getValue().size() > 1).flatMap(e -> e.getValue().stream())
				.collect(Collectors.toList());
		
		if (duplicateRecords.size() > 0)
			throw new DuplicateReferencesException("Duplicate References Found from Input file");

		List<Map<String, String>> listRecords = new ArrayList<>();
		List<Record> amountDiffRecords = input.stream().filter(e -> {
			BigDecimal amount = new BigDecimal(e.getStartBalance()).add(new BigDecimal(e.getMutation()));
			boolean diff = amount.equals(new BigDecimal(e.getEndBalance()));
			if (!diff) {
				Map<String, String> failedRecords = new LinkedHashMap();
				failedRecords.put("Reference", String.valueOf(e.getReference()));
				failedRecords.put("Description", e.getDescription());
				listRecords.add(failedRecords);
			}
			return !diff;
		}).collect(Collectors.toList());
		
		if(listRecords.size()>0)
			createOutputCSVFile(listRecords);

		return "ok";
	}
	/** Create output csv report with reference and non matched end balance transaction description
	 * @param list of map objects 
	 * @return void
	 * @throws IOException 
	 *  **/

	private void createOutputCSVFile(List<Map<String, String>> listRecords) throws IOException {
		File file = new File(outputFilePath);
		// Create a File and append if it already exists.
		try(Writer writer = new FileWriter(file, true);) {
			
		// Copy List of Map Object into CSV format at specified File location.
		CsvSchema schema = null;
		CsvSchema.Builder schemaBuilder = CsvSchema.builder();
		if (listRecords != null && !listRecords.isEmpty()) {
			for (String col : listRecords.get(0).keySet()) {
				schemaBuilder.addColumn(col);
			}
			schema = schemaBuilder.build().withLineSeparator("\r").withHeader();
		}
		CsvMapper mapper = new CsvMapper();
		
			mapper.writer(schema).writeValues(writer).writeAll(listRecords);
			writer.flush();
		} 

	}
	/**
	 * Read records from input xml file and make record object
	 * @param filePath
	 * @return list of record objects
	 */
	private List<Record> readRecordsFromXML(String filePath) {
		//String filePath = "C:\\\\Users\\\\Venkat Seshadri\\\\Desktop\\\\assignment - BE\\\\records.xml";
		logger.debug("configured output file location ::FileReaderServiceImpl::readRecordsFromXML "+ filePath);
		File xmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		List<Record> empList = new ArrayList<Record>();
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("record");
			// now XML is loaded as Document in memory, lets convert it to Object List
			logger.debug("no of nodeLists  ::FileReaderServiceImpl::readRecordsFromXML "+ nodeList.getLength());
			for (int i = 0; i < nodeList.getLength(); i++) {
				empList.add(getEmployee(nodeList.item(i)));
			}
			

		} catch (SAXException | ParserConfigurationException | IOException e1) {
			logger.debug(":FileReaderServiceImpl::readRecordsFromXML "+ e1.getStackTrace());
		}
		return empList;

	}

	private Record getEmployee(Node node) {
		Record emp = new Record();
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			emp.setReference(Long.valueOf(element.getAttribute("reference")));
			emp.setAccountNumber(getTagValue("accountNumber", element));
			emp.setDescription(getTagValue("description", element));
			emp.setStartBalance(getTagValue("startBalance", element));
			emp.setMutation(getTagValue("mutation", element));
			emp.setEndBalance(getTagValue("endBalance", element));
		}

		return emp;
	}

	private String getTagValue(String tag, Element element) {
		NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodeList.item(0);
		return node.getNodeValue();
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
}
