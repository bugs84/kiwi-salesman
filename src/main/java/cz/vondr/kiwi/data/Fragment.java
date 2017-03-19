package cz.vondr.kiwi.data;

public class Fragment {
    public Flight arrival;
    public Flight departure;
    public int price;

    public Fragment(Flight arrival, Flight departure, int price) {
        this.arrival = arrival;
        this.departure = departure;
        this.price = price;
    }
}
