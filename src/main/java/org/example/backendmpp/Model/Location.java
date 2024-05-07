package org.example.backendmpp.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Location {
    @Column(unique = true)
    private String name;
    private String address;
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Location() {
    }

    public Location(String name, String address) {
        this.name = name;
        this.address = address;
        this.cars = List.of();
    }

    public Location(String name, String address, List<Car> cars) {
        this.name = name;
        this.address = address;
        this.cars = cars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void removeCar(Car car) {
        cars.remove(car);
    }

    public void updateLocation(Location location) {
        this.name = location.getName();
        this.address = location.getAddress();
    }

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
