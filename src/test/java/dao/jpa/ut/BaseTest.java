package dao.jpa.ut;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import ibm.ra.customer.CustomerResource;
import ibm.ra.integration.dao.CustomerDAO;

public class BaseTest {
	protected static CustomerResource serv;
	protected static CustomerDAO dao;
	
	// Delete the DB files
	static void deleteDir(File file) {
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            deleteDir(f);
	        }
	    } 
	    file.delete();
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 serv = new CustomerResource();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		deleteDir(new File("./custdb"));
	}
}
