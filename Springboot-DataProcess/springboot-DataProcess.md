# Springboot-DataProcess
![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)
# Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

# Prerequisites
For building and running the application you need:

JDK 1.8
Maven 3

## Running the application locally
There are several ways to run a Spring Boot application on your local machine. One way is to execute the main method in the com.example.demo.DemoApplication class from your IDE.
Alternatively you can use the Spring Boot Maven plugin like so:
**mvn spring-boot:run**

# Project Description:
 
**Input parameters:**

> spring.application.inputFilePath=<local location>records.csv
> spring.application.outputFilePath= <local location>records.csv

**Validations:**

> Input file must be either csv or xml else return 422 status code with Formate Not Supporting :: Supportin formates(csv,xml) message
> Throws DuplicateReferencesException if input file has duplicate references
> Output file location must not be empty else throws IOException with 404 status code

**Test Assumption:**
* As per the test description , we have to validate duplicate references by throwing exception and un matched end balance with start and mutation balance will consider as failed records .
* CSV report file will create with failed records at output file location.


## Functionality:

* Springboot-DataProcess application process csv and xml formate files using streams API since its faster and more convinient .
 Below are the main classes of processing records and exception handlers.
> **FileReadController :** Method: GET , Mapping url: /processFile , Controller checks input file formate and switch to respective process methods.
> **FileReaderServiceImpl:** handleCSV(), handleXML() are the main methods where records stream procces for collecting duplicate and unmatched records.
> **ResponseExceptionHandler:** DuplicateReferencesException,IOException 


License
----


