package cz.vondr.kiwi.data;

public class Flight {
    public short destination;
    public int price; //Price je unsignedShort :( - da se tu nejaky byte usetrit
    public short from;
    public short day;

    public Flight(short to, int price, short from, short day) {
        this.destination = to;
        this.price = price;
        this.from = from;
        this.day = day;
    }
}
