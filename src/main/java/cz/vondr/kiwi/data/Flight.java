package cz.vondr.kiwi.data;

public class Flight {
    public short destination;
    public int price; //Price je unsignedShort :( - da se tu nejaky byte usetrit

    public Flight(short destination, int price) {
        this.destination = destination;
        this.price = price;
    }
}
