package org.example.backendmpp.Repository;

import org.example.backendmpp.Model.Car;
import org.example.backendmpp.Model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {
    Page<Car> findAll(Pageable pageable);

    Page<Car> findCarsByLocationIsIn(Collection<Location> location, Pageable pageable);
    Page<Car> findCarsByLocationIsInAndSalePriceIsNull(Collection<Location> location, Pageable pageable);

    List<Car> findCarsBySalePriceIsNullAndLocationIsIn(Collection<Location> locations);

    List<Car> findCarsBySalePriceIsNotNullAndLocation(Location location);

    List<Car> findCarsByLocationAndSalePriceIsNull(Location location);

    List<Car> findCarsBySalePriceNotNull();

    List<Car> findCarsBySalePriceIsNull();

    List<Car> findCarsBySalePriceIsNotNull();
}
