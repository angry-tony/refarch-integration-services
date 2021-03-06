 package ibm.ra.util;

import ibm.caseserv.itests.CustomerRestClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import po.dto.model.CustomerAccount;
import po.dto.model.ProductDTO;

public class CustomerCSVReader {
    
	protected static CustomerRestClient client = new CustomerRestClient();
	
	public static void main(String... args) { 
		List<CustomerAccount> cl =null;
		CustomerCSVReader tool= new CustomerCSVReader();
		if (args.length >0) {
			cl = tool.readCustomersFromCSV(args[0]); 
		} else {
			cl = tool.readCustomersFromCSV("./dataset/customer.csv"); 
		}
		
		for (CustomerAccount c : cl) {
			System.out.println("Upload "+c.getName()+" age:"+c.getAge()+" account "+c.getAccountNumber());
			tool.uploadToDB2ViaREST(c);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public  void uploadToDB2ViaREST(CustomerAccount c){		
		try {
			client.executeCustomerPost("api/customers",c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read the given csv file and create a list of customer accounts to be persisted later. 
	 * The missing fields are populated using randomized values
	 * @param fileName
	 * @return
	 */
    public  List<CustomerAccount>  readCustomersFromCSV(String fileName){
    	List<CustomerAccount> cl = new ArrayList<CustomerAccount>();
    	Path pathToFile = Paths.get(fileName); 
    	// create an instance of BufferedReader 

    	try 
    	{ 
    		BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII);
    		// read the first line from the text file 
    		String line = br.readLine(); 
    		line = br.readLine(); 
    		// loop until all lines are read 
    		while (line != null) { 
    			// use string.split to load a string array with the values from 
    			// each line of the file, using a comma as the delimiter 
    			String[] attributes = line.split(","); 
    			CustomerAccount aRow = createCustomer(attributes); 
    			cl.add(aRow);
    			// read next line before looping // if end of file reached, line would be null 
    			line = br.readLine(); 
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return cl;
    }
    
    public void saveCustomerCSV(List<CustomerAccount> l){
    	FileWriter fstream;
    	try {    
    	 fstream = new FileWriter("customer-out.csv");
    	 BufferedWriter out = new BufferedWriter(fstream);
    	 for (CustomerAccount ca : l) {
    		 out.write(ca.toCsvString()+"\n");
    	 }
    	     
    	 out.close();
    	}
    	 catch (IOException e) {
    	         
    	 e.printStackTrace();
    	}
    }
    
    private static String[] mstatus= new String[]{"Single","Married","Widowed"};
    private static String[] zipcodes= new String[]{"95051","94000","93000","92100","87430","43000","6000","6200"};
    static Random rand = new Random();
    
    private  CustomerAccount createCustomer(String[] attributes){
    	CustomerAccount ca = new CustomerAccount();
    	ca.setId(Long.decode(attributes[0]));
    	ca.setLastName("Name_"+attributes[0]);
    	ca.setName(ca.getLastName());
    	ca.setFirstName("firstName_"+attributes[0]);
    	ca.setName(ca.getLastName()+" "+ca.getFirstName());
    	ca.setEmailAddress(ca.getLastName()+"@email.com");
    	ca.setType("Person");
    	ca.setGender(attributes[1]);
    	ca.setStatus(attributes[2]);
    	ca.setChildren(Integer.parseInt(attributes[3]));
    	ca.setEstimatedIncome(Double.parseDouble(attributes[4]));
    	if (attributes[5] == "T") {
    		ca.setCarOwner(true);
		} else {
			ca.setCarOwner(false);
		}
    	ca.setAge(Double.parseDouble(attributes[6]));
    	
    	if (attributes[7] == null || attributes[7].isEmpty()){
    		int  n = rand.nextInt(mstatus.length) ;
    		attributes[7]=mstatus[n];
    	}
    	ca.setMaritalStatus(attributes[7]);
    	
    	if (attributes[8] == null || attributes[8].isEmpty()){
    		int  n = rand.nextInt(zipcodes.length) ;
    		attributes[8]=zipcodes[n];
    	}
    	ca.setZipCode(attributes[8]);
    	ca.setAccountNumber("ACCT_"+ca.getId());
    	ca.setLongDistance(Double.parseDouble(attributes[9]));
    	ca.setInternational(Double.parseDouble(attributes[10]));
    	if (ca.getInternational() == 0) {
    		ca.setInternational( 50 * rand.nextDouble());
    	}
    	ca.setLocal(Double.parseDouble(attributes[11]));
        ca.setDropped(Integer.parseInt(attributes[12]));
        ca.setPaymentMethod(attributes[13]);
        ca.setLocalBillType(attributes[14]);
        ca.setLongDistanceBillType(attributes[15]);
        ca.setUsage(Double.parseDouble(attributes[16]));
        ca.setRatePlan(attributes[17]);
        
        ProductDTO p = new ProductDTO();
    	p.setPhoneNumber("650600"+ca.getId().toString());
        if (attributes[18] != null) {
        	p.setName(attributes[18]);
        } else {
        	p.setName("sam");
        }
        ca.getExistingProducts().add(p);
        ca.setChurnClass(attributes[19]);
        if (ca.getChurnClass().equals("T")) {
        	ca.setChurn(0.7 + 0.3 * rand.nextDouble());
        } else {
           ca.setChurn(0.1 + 0.6 * rand.nextDouble());
        }
        ca.setChurnStatus("Assessed");
        ca.setBalance(150 * rand.nextDouble());
    	return ca;
    }
}
