package main;

public class Main {

    public static void main(String[] args) {
        JSONTickets jsonTickets = new JSONTickets();
        jsonTickets.readFile();
        jsonTickets.outputOfResult();
    }

}
