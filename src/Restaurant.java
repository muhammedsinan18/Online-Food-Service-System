package src;

import java.util.ArrayList;

public class Restaurant {
    private int income;
    private int outcome;
    private ArrayList<Order> orders; // Linkedlist şeklinde depolanacak
    private ArrayList<Order> ordersPrepared; // Linkedlist şeklinde depolanacak
    private float score;
    private int numberofScores;
    private Menu menu;
    private ArrayList<Worker> workers;
    private ArrayList<Customer> customers;


    public void Restaurant() {
        
    }

    public void inputScore(int score) {
        if( score > 5 || score < 0 )
            return;
        
        float temp =  this.score*numberofScores;
        this.score = (temp+score)/(++numberofScores);         
    }

    public void getOrder( Order obj ){
        orders.add(obj);
    }

    public void sendChef( Order order) {

    }

    public void sendCourier() {

    }

    public void directingInterface() {

    }

    public String menu() {
        return "empty";
    }

}
