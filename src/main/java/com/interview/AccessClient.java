package com.interview;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import javax.ws.rs.core.MultivaluedMap; // the locations of these classes are weird

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessClient {
    private static final Logger logger = LoggerFactory.getLogger(AccessClient.class);
    static Client client = null; // must be non-null and unchangeable
    final static String applicationServiceURI = "http://localhost:8080/api/";
    final static Random generator = new Random(System.currentTimeMillis());

    public static void main(String[] args) {
        int nummodified = 0;
        int balance = 0;
        List<Account> accts = null;
        Account account = null;
        logger.info("Starting the test client");
        System.out.println("Class Path is " + System.getProperty("java.class.path"));
        initializeClient();
        try {
            logger.info("Testing the health methods");
            System.out.println( getInterviewVersion() );
            System.out.println( getInterviewPostgresVersion() );
            System.out.println( sendInterviewPing() );

            account = getInterviewAccount( "1" );
            if (account == null) {
                System.out.println( "account 1 was not found" );
            } else {
                System.out.println( "Found the following:\n\tID:\t"+ account.getId() + "\tBalance:\t" + account.getBalance());
            }
            
            account = getInterviewAccount( "4" );
            if (account == null) {
                System.out.println( "account 4 was not found" );
            } else {
                System.out.println( "Found the following:\n\tID:\t"+ account.getId() + "\tBalance:\t" + account.getBalance());
            }
            
            System.out.println("Account 3 has balance: " + getInterviewAccountBalance("3"));

            logger.info("dump all the accounts");
            accts = getInterviewAccount();
            if (accts == null) {
                System.out.println( "No accounts were found" );
            } else {
                System.out.println( "Found the following:");
                for(Account a: accts) {
                    System.out.println("\tID:	" + a.getId() + "\tBalance:\t" + a.getBalance());
                }
            }

            logger.info("methods to modify accounts");
            System.out.println( "transfer of 5 cents from account 1 to account 4 has " +
                    makeTransfer( "1", "4", 5 ) );

            System.out.println("Resetting account, balance = 500\n" + setInterviewAccountBalance(new Integer(generator.nextInt(25)).toString(), generator.nextInt(2500)) + " records modified");

            System.out.println("Updating account -- " + addInterviewAccountBalance("2", generator.nextInt(100) - 50).toString() + " records modified");

            logger.info("dump all the accounts");
            accts = getInterviewAccount();
            if (accts == null) {
                System.out.println( "No accounts were found" );
            } else {
                System.out.println( "Found the following:");
                for(Account a: accts) {
                    System.out.println("\tID:	" + a.getId() + "\tBalance:\t" + a.getBalance());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println( "Something went wrong!" );
        }
    }

    /* here is where the client api starts */
    static public void initializeClient() {
        DefaultClientConfig defaultClientConfig = new DefaultClientConfig();
        defaultClientConfig.getClasses().add(JacksonJsonProvider.class);
        client = Client.create(defaultClientConfig);
    }

    static public String sendInterviewPing() throws RuntimeException {
        String output = "";

        try {
            WebResource webResource = client.resource( applicationServiceURI + "ping" );
            ClientResponse response = webResource.accept( "application/json" )
                    .get( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : "
                        + response.getStatus() );
            }
            output = response.getEntity( String.class );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.replaceAll( "\"", "" );
    }

    static public String getInterviewVersion() throws RuntimeException {
        String output = "";

        try {
            WebResource webResource = client.resource( applicationServiceURI + "version" );
            ClientResponse response = webResource.accept( "application/json" )
                    .get( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : "
                        + response.getStatus() );
            }
            output = response.getEntity( String.class );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    static public String getInterviewPostgresVersion() throws RuntimeException {
        String output = "";

        try {
            WebResource webResource = client.resource( applicationServiceURI + "postgres" );
            ClientResponse response = webResource.accept( "application/json" )
                    .get( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : "
                        + response.getStatus() );
            }
            output = response.getEntity( String.class );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.replaceAll( "\"", "" );
    }

    static public List<Account> getInterviewAccount() throws RuntimeException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Account> accounts = new ArrayList<>();
        String jsonobjinstr = null;

        try {
            WebResource webResource = client.resource( applicationServiceURI + "accounts" );
            ClientResponse response = webResource.accept( "application/json" )
                    .get( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : "
                        + response.getStatus() );
            }
            jsonobjinstr = response.getEntity( String.class );
            List<LinkedHashMap<Integer, String>> list = mapper.readValue(jsonobjinstr, List.class); // this is totally broken
            // but I think it's a matter of missing "-marks
            for(LinkedHashMap<Integer, String> s: list) {
                String id = s.get("id");
                Object balance = s.get("balance");
                accounts.add(new Account(id, (Integer) balance) );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }

    static public Account getInterviewAccount(String id) throws RuntimeException {
        logger.debug("argument: {}", id);
        Account account = null;

        if (id == null) {
            return null;
        }
        if (id.equals( "" )) {
            return null;
        }

        try {
            WebResource webResource = client.resource( applicationServiceURI + "accounts" + "/" + id);
            ClientResponse response = webResource.accept( "application/json" )
                    .get( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : "
                        + response.getStatus() );
            }
            account = response.getEntity( Account.class );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return account;
    }

    static public Integer getInterviewAccountBalance(String id) throws RuntimeException {
        logger.debug("argument: {}", id);
        if (id == null) {
            return 0;
        }
        if (id.equals( "" )) {
            return 0;
        }

        try {
            WebResource webResource = client.resource( applicationServiceURI + "accounts/balance/" + id );
            ClientResponse response = webResource.accept( "application/json" )
                    .get( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : "
                        + response.getStatus() );
            }
            Integer balance = response.getEntity( Integer.class );
            if (balance == null) {
                return 0;
            }
            return balance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // returns number of records updated -- should be at most 1
    static public Integer setInterviewAccountBalance(String acct, int amount) throws RuntimeException {
        logger.debug("arguments: {}, {}", acct, amount);
        if (acct == null) {
            System.err.println( "Account is null" );
            return 0;
        }
        if (acct.equals( "" )) {
            System.err.println( "Account is zero-length string" );
            return 0;
        }
        if (amount < 0) { // allowed to zero account but not render negative
            System.err.println( "amount must >= 0" );
            return 0;
        }

        try {
            WebResource webResource = client.resource( applicationServiceURI + "accounts/balance/" + acct );
            MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add( "amount", Integer.toString( amount ) );
            ClientResponse response = webResource.queryParams( queryParams ).accept( "application/json" )
                    .post( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : "
                        + response.getStatus() );
            }
            return Integer.parseInt( response.getEntity( String.class ) );

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // returns number of database records updated
    static public Integer addInterviewAccountBalance(String acct, int amount) throws RuntimeException {
        logger.debug("arguments: {}, {}", acct, amount);
        if (acct == null) {
            System.err.println( "Account is null" );
            return 0;
        }
        if (acct.equals( "" )) {
            System.err.println( "Account is zero-length string" );
            return 0;
        }
        if (amount == 0) {
            System.err.println( "amount must != 0" ); // this probably could be sent to server
            // but why send pointless transaction
            return 0;
        }

        try {
            WebResource webResource = client.resource( applicationServiceURI + "accounts/addtobalance/" + acct );
            MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add( "amount", Integer.toString( amount ) );
            ClientResponse response = webResource.queryParams( queryParams ).accept( "application/json" )
                    .post( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : " + response.getStatus() );
            }
            return Integer.parseInt( response.getEntity( String.class ) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    static public String makeTransfer(String src, String dst, int amount) throws RuntimeException {
        logger.debug("arguments: {}, {}, {}", src, dst, amount);
        if (src == null) {
            System.err.println( "Account is null" );
            return "Failed";
        }
        if (src.equals( "" )) {
            System.err.println( "Account is zero-length string" );
            return "Failed";
        }
        if (dst == null) {
            System.err.println( "Account is null" );
            return "Failed";
        }
        if (dst.equals( "" )) {
            System.err.println( "Account is zero-length string" );
            return "Failed";
        }
        if (amount == 0) {
            System.err.println( "amount must != 0" ); // this probably could be sent to server
            // but why send pointless transaction
            return "Failed";
        }

        try {
            WebResource webResource = client.resource( applicationServiceURI + "accounts/transfer" );
            MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add( "srcid", src );
            queryParams.add( "dstid", dst );
            queryParams.add( "amount", new Integer(amount).toString());
            ClientResponse response = webResource.queryParams( queryParams ).accept( "application/json" )
                    .post( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : " + response.getStatus() );
            }
            return response.getEntity( String.class ).replaceAll( "\"", "" ); // de-jsonize
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Failed";
    }

    static public void setInterviewAccountBalanceV2(String acct, int amount) throws RuntimeException {
    }

    static public void addInterviewAccountBalanceV2(String acct, int amount) throws RuntimeException {
    }

    static public void makeTransferV2(String src, String dst, int amount) throws RuntimeException {
    }
}
