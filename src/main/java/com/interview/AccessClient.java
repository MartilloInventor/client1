package com.interview;
import com.interview.Account;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.List;


public class AccessClient {
    final static Client client = Client.create();

    public static void main(String[] args) {
        System.out.print( getInterviewVersion() );
        System.out.print( getInterviewPostgresVersion() );
    }

    static public String sendInterviewPing() {
        String output = "";

        try {
            WebResource webResource = client
                    .resource( "http://localhost:8080/api/ping");
            ClientResponse response = webResource.accept( "application/json" )
                    .get( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : "
                        + response.getStatus() );
            }
            output = response.getEntity(String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    static public String getInterviewVersion() {
        String output = "";

        try {
            WebResource webResource = client
                    .resource( "http://localhost:8080/api/version");
            ClientResponse response = webResource.accept( "application/json" )
                    .get( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : "
                        + response.getStatus() );
            }
            output = response.getEntity(String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    static public String getInterviewPostgresVersion() {
        String output = "";

        try {
            WebResource webResource = client
                    .resource( "http://localhost:8080/api/postgres");
            ClientResponse response = webResource.accept( "application/json" )
                    .get( ClientResponse.class );
            if (response.getStatus() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : "
                        + response.getStatus() );
            }
            output = response.getEntity(String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    static public List<Account> getInterviewAccount() { return null; }
    static public Account getInterviewAccount(String id) { return null; }
    static public Integer getInterviewAccountBalance(String id) { return 0; }

    static public void setInterviewAccountBalance(String acct, int amount) {
    }

    static public void addInterviewAccountBalance(String acct, int amount) {
    }

    static public void makeTransfer(String src, String dst, int amount) {
    }
}