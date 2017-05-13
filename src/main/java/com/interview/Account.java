package com.interview;

public class Account {

    public String id;
    public Integer balance;

    public Account(String id, Integer balance) {
        this.id = id;
        this.balance = balance;
    }

    public Account() {}

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public Integer getBalance() {
        return balance;
    }
    public void setBalance(Integer balance) {
        this.balance = balance;
    }
}
