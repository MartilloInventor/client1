package com.interview;

import com.interview.Account;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap; // the locations of these classes are weird

import java.util.List;


public class AccessClient {
    final static Client client = Client.create(); // must be non-null and unchangeable
    final static String applicationServiceURI = "http://localhost:8080/api/";

    public static void main(String[] args) {
        List<Account> accts = null;
        Account account = null;
        try {
            System.out.println( getInterviewVersion() );
            System.out.println( getInterviewPostgresVersion() );
            System.out.println( sendInterviewPing() );
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println( "Something went wrong!" );
        }
        accts = getInterviewAccount();
        if (accts == null) {
            System.out.println( "No accounts were found" );
        } else {
            System.out.println( "Found the following:\n\t" + accts.toString() );
        }
        account = getInterviewAccount( "1" );
        if (account == null) {
            System.out.println( "account 1 was not found" );
        } else {
            System.out.println( "Found the following:\n\t" + account.toString() );
        }
        account = getInterviewAccount( "4" );
        if (account == null) {
            System.out.println( "account 4 was not found" );
        } else {
            System.out.println( "Found the following:\n\t" + account.toString() );
        }
        System.out.println( "transfer of 5 cents from account 1 to account 4 has " +
                makeTransfer( "1", "4", 5 ) );
        accts = getInterviewAccount();
        if (accts == null) {
            System.out.println( "No accounts were found" );
        } else {
            System.out.println( "Found the following:\n\t" + accts.toString() );
        }
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
            WebResource webResource = client.resource( applicationServiceURI + "postgress" );
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
        List<Account> accounts = null;

        try {
            WebResource webResource = client.resource( applicationServiceURI + "account" );
            ClientResponse response = webResource.accept( "application/json" )
                    .get( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : "
                        + response.getStatus() );
            }
            accounts = response.getEntity( new GenericType<List<Account>>() {
            } );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }

    static public Account getInterviewAccount(String id) throws RuntimeException {
        Account account = null;

        if (id == null) {
            return null;
        }
        if (id.equals( "" )) {
            return null;
        }

        try {
            WebResource webResource = client.resource( applicationServiceURI + "account" );
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
        Account account = null;
        if (id == null) {
            return 0;
        }
        if (id.equals( "" )) {
            return 0;
        }

        try {
            WebResource webResource = client.resource( applicationServiceURI + "account" + "/" + id );
            ClientResponse response = webResource.accept( "application/json" )
                    .get( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : "
                        + response.getStatus() );
            }
            account = response.getEntity( Account.class );
            if (account == null) {
                return 0;
            }
            return account.getBalance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // returns number of records updated -- should be at most 1
    static public Integer setInterviewAccountBalance(String acct, int amount) throws RuntimeException {
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
            WebResource webResource = client.resource( applicationServiceURI + "account" + "/" + acct );
            MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add( "amount", Integer.toString( amount ) );
            ClientResponse response = webResource.queryParams( queryParams ).accept( "application/json" )
                    .post( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : "
                        + response.getStatus() );
            }
            return Integer.getInteger( response.getEntity( String.class ) );

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // returns number of database records updated
    static public Integer addInterviewAccountBalance(String acct, int amount) throws RuntimeException {
        if (acct == null) {
            System.err.println( "Account is null" );
            return 0;
        }
        if (acct.equals( "" )) {
            System.err.println( "Account is zero-length string" );
            return 0;
        }
        if (amount != 0) {
            System.err.println( "amount must != 0" ); // this probably could be sent to server
            // but why send pointless transaction
            return 0;
        }

        try {
            WebResource webResource = client.resource( applicationServiceURI + "addtobalance" + "/" + acct );
            MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add( "amount", Integer.toString( amount ) );
            ClientResponse response = webResource.queryParams( queryParams ).accept( "application/json" )
                    .post( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : " + response.getStatus() );
            }
            return Integer.getInteger( response.getEntity( String.class ) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    static public String makeTransfer(String src, String dst, int amount) throws RuntimeException {
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
        if (amount != 0) {
            System.err.println( "amount must != 0" ); // this probably could be sent to server
            // but why send pointless transaction
            return "Failed";
        }

        try {
            WebResource webResource = client.resource( applicationServiceURI + "maketransfer" );
            MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add( "amount", Integer.toString( amount ) );
            queryParams.add( "srcid", src );
            queryParams.add( "dstid", dst );
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
