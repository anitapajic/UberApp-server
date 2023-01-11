package org.Tim19.UberApp.controller;

import org.Tim19.UberApp.dto.PaginatedData.CreateRideBodyPaginatedDTO;
import org.Tim19.UberApp.dto.PaginatedData.PanicPaginatedDTO;
import org.Tim19.UberApp.dto.PaginatedData.UserPanicPaginatedDTO;
import org.Tim19.UberApp.dto.RideDTO;
import org.Tim19.UberApp.model.Driver;
import org.Tim19.UberApp.model.Passenger;
import org.Tim19.UberApp.model.Rejection;
import org.Tim19.UberApp.model.Ride;
import org.Tim19.UberApp.service.DriverService;
import org.Tim19.UberApp.service.PassengerService;
import org.Tim19.UberApp.service.RideService;
import org.Tim19.UberApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@RestController
@RequestMapping(value="/api/ride")
@CrossOrigin(value = "*")
public class RideController {

    @Autowired
    private RideService rideService;
    @Autowired
    private DriverService driverService;
    @Autowired
    private UserService userService;
    @Autowired
    private PassengerService passengerService;

    //CREATING A RIDE  /api/ride
    @PreAuthorize("hasAnyAuthority('PASSENGER')")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<RideDTO> createRide(@RequestBody CreateRideBodyPaginatedDTO rideDTO) {


        Driver driver = rideService.findFreeDriver();
        Ride ride = new Ride();

        ride.setPanic(false);
        ride.setDriver(driver);
        ride.setStartTime(LocalDateTime.now());
        ride.setTotalCost(480.00);
        ride.setLocations(rideDTO.getLocations());
        ride.setEstimatedTimeInMinutes(7);
        ride.setStatus("PENDING");
        ride.setBabyTransport(rideDTO.isBabyTransport());
        ride.setPetTransport(rideDTO.isPetTransport());
        ride.setVehicleType(rideDTO.getVehicleType());
        for (Passenger p: rideDTO.getPassengers()) {
            Passenger p2 = (Passenger) userService.findOneById(p.getId());
            ride.addPassenger(p2);
        }

        ride = rideService.save(ride);

        return new ResponseEntity<>(new RideDTO(ride),  HttpStatus.CREATED);
    }

    //ACTIVE RIDE FOR DRIVER  /api/ride/driver/{driverId}/active
    @GetMapping(value="/driver/{driverId}/active")
    @PreAuthorize("hasAnyAuthority('DRIVER', 'ADMIN')")
    public ResponseEntity<RideDTO> activeRideForDriver(@PathVariable Integer driverId) {

        Driver driver = driverService.findOne(driverId);

        if (driver == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Set<Ride> ridesOfDriver = driver.getRides();
        Ride activeRide = new Ride();

        for (Ride r : ridesOfDriver){
            if (r.getStatus().equals("ACTIVE")){
                activeRide = r;
            }
        }

        return new ResponseEntity<>(new RideDTO(activeRide),HttpStatus.OK);
    }

    //ACTIVE RIDE FOR PASSENGER  /api/ride/passenger/{passengerId}/active
    @PreAuthorize("hasAnyAuthority('PASSENGER', 'ADMIN')")
    @GetMapping(value="/passenger/{passengerId}/active")
    public ResponseEntity<RideDTO> activeRideForPassenger(@PathVariable Integer passengerId) {

        Passenger passenger = passengerService.findOne(passengerId);

        if (passenger == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Set<Ride> ridesOfPassenger = rideService.findAllByPassengerId(passengerId);
        Ride activeRide = new Ride();

        for (Ride r : ridesOfPassenger){
            if (r.getStatus().equals("STARTED")){
                activeRide = r;
            }
        }


        return new ResponseEntity<>(new RideDTO(activeRide),HttpStatus.OK);
    }

    //RIDE DETAILS  api/ride/{id}
    @PreAuthorize("hasAnyAuthority('PASSENGER', 'ADMIN', 'DRIVER')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<RideDTO> getRide(@PathVariable Integer id) {

        Ride ride = rideService.findOneRideById(id);

        // ride must exist
        if (ride == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new RideDTO(ride), HttpStatus.OK);
    }

    //CANCEL EXISTING RIDE  /api/ride/{id}/withdraw
    @PreAuthorize("hasAnyAuthority('PASSENGER', 'DRIVER')")
    @PutMapping(value="/{id}/withdraw")
    public ResponseEntity<RideDTO> cancelRide(@PathVariable Integer id) {

        Ride ride = rideService.findOneRideById(id);

        // ride must exist
        if (ride == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ride.setStatus("CANCELED");
        rideService.save(ride);

        return new ResponseEntity<>(new RideDTO(ride),HttpStatus.OK);
    }

    //PANIC PROCEDURE FOR THE RIDE  /api/ride/{id}/panic
    @PreAuthorize("hasAnyAuthority('PASSENGER', 'DRIVER')")
    @PutMapping(value="/{id}/panic", consumes = "application/json")
    public ResponseEntity<PanicPaginatedDTO> panicRide(@RequestBody String reason,
                                                       @PathVariable Integer id) {

        Ride ride = rideService.findOneRideById(id);

        // ride must exist
        if (ride == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ///TODO: Dobaviti usera preko tokena (ovo dole ne radi)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserPanicPaginatedDTO user = (UserPanicPaginatedDTO) auth.getDetails();


        int randomNumber = new Random().nextInt(9000) + 1000;
        PanicPaginatedDTO panic = new PanicPaginatedDTO(randomNumber, user, ride, LocalDateTime.now(), reason);



        return new ResponseEntity<>(panic, HttpStatus.OK);
    }

    //ACCEPT RIDE  /api/ride/{id}/accept
    @PreAuthorize("hasAnyAuthority('DRIVER')")
    @PutMapping(value="/{id}/accept")
    public ResponseEntity<RideDTO> acceptRide(@PathVariable Integer id) {

        Ride ride = rideService.findOneRideById(id);

        // ride must exist
        if (ride == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ride.setStatus("ACCEPTED");
        rideService.save(ride);

        return new ResponseEntity<>(new RideDTO(ride), HttpStatus.OK);
    }

    //END THE RIDE  /api/ride/{id}/end
    @PreAuthorize("hasAnyAuthority('DRIVER')")
    @PutMapping(value="/{id}/end")
    public ResponseEntity<RideDTO> endRide(@PathVariable Integer id) {

        Ride ride = rideService.findOneRideById(id);

        // ride must exist
        if (ride == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ride.setStatus("FINISHED");
        rideService.save(ride);

        return new ResponseEntity<>(new RideDTO(ride), HttpStatus.OK);
    }

    //CANCEL RIDE WITH AN EXPLANATION  /api/ride/{id}/cancel
    @PreAuthorize("hasAnyAuthority('PASSENGER')")
    @PutMapping(value="/{id}/cancel", consumes = "application/json")
    public ResponseEntity<RideDTO> cancelRideWithExpl(@PathVariable Integer id,
                                                               @RequestBody String explanation) {
        Rejection rejection = new Rejection();
        Set<Rejection> rejections = new HashSet<>();

        Ride ride = rideService.findOneRideById(id);

        // ride must exist
        if (ride == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ///TODO : Videti zasto ispisuje da je rejection null
        rejection.setTimeOfRejection(LocalDateTime.now());
        rejection.setReason(explanation);
        rejections.add(rejection);

        ride.setRejection(rejections);
        ride.setStatus("REJECTED");

        rideService.save(ride);

        return new ResponseEntity<>(new RideDTO(ride), HttpStatus.OK);
    }

}
