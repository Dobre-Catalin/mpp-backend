package org.example.backendmpp.Model;

import jakarta.persistence.*;
import org.slf4j.Logger;

@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String make;
    private String model;
    private int year;
    private String color;
    private int mileage;
    private int accidents;
    private int engineCapacity;
    private String engineType;
    private int price;
    private String about;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;


    public Car() {
    }

    public Car(String make, String model, int year, String color, int mileage, int accidents, int engineCapacity, String engineType, int price, String about, Location location) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.mileage = mileage;
        this.accidents = accidents;
        this.engineCapacity = engineCapacity;
        this.engineType = engineType;
        this.price = price;
        this.about = about;
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int getAccidents() {
        return accidents;
    }

    public void setAccidents(int accidents) {
        this.accidents = accidents;
    }

    public int getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(int engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void updateCar(Car car) {
        this.make = car.make;
        this.model = car.model;
        this.year = car.year;
        this.color = car.color;
        this.mileage = car.mileage;
        this.accidents = car.accidents;
        this.engineCapacity = car.engineCapacity;
        this.engineType = car.engineType;
        this.price = car.price;
        this.about = car.about;
    }

    @Override
    public String toString() {
        return "Car{" +
                "make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", color='" + color + '\'' +
                ", mileage=" + mileage +
                ", accidents=" + accidents +
                ", engineCapacity=" + engineCapacity +
                ", engineType='" + engineType + '\'' +
                ", price=" + price +
                ", about='" + about + '\'' +
                '}';
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
