package appdscw2sub;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Sales {

    private final Integer Year, QTR, Quantity;
    private final String Region, Vehicle;

    private final SimpleStringProperty vehicle, region;
    private final SimpleIntegerProperty quantity, year, qtr;

    public Sales(Integer year, Integer qtr, String region, String vehicle, Integer quantity) {
        this.Year = year;
        this.QTR = qtr;
        this.Region = region;
        this.Vehicle = vehicle;
        this.Quantity = quantity;

        this.year = new SimpleIntegerProperty(year);
        this.qtr = new SimpleIntegerProperty(qtr);
        this.region = new SimpleStringProperty(region);
        this.vehicle = new SimpleStringProperty(vehicle);
        this.quantity = new SimpleIntegerProperty(quantity);
    }
    
    //getters setters
    @Override
    public String toString() {
        return String.format("%s%s%s%s%s", ("Year:" + Year + " "), ("QTR:" + QTR + " "), (Region != null ? ("Region:" + Region + " ") : ""), (Vehicle != null ? ("Vehicle:" + Vehicle + " ") : ""), ("Quantity:" + Quantity));
    }

    public Integer getYear() {
        return Year;
    }

    public Integer getQTR() {
        return QTR;
    }

    public String getRegion() {
        return Region;
    }

    public String getVehicle() {
        return Vehicle;
    }

    public Integer getQuantity() {
        return Quantity;
    }

    
    
    //property constructors for table
    public SimpleStringProperty vehicleProp() {
        return vehicle;
    }

    public SimpleStringProperty regProp() {
        return region;
    }

    public SimpleIntegerProperty quantProp() {
        return quantity;
    }

    public SimpleIntegerProperty yrProp() {
        return year;
    }

    public SimpleIntegerProperty qtrProp() {
        return qtr;
    }

}
