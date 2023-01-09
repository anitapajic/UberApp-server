package org.Tim19.UberApp.controller;

import org.Tim19.UberApp.dto.PaginatedData.UserPaginatedDTO;
import org.Tim19.UberApp.dto.ReviewDTO;
import org.Tim19.UberApp.model.*;
import org.Tim19.UberApp.service.ReviewService;
import org.Tim19.UberApp.service.RideService;
import org.Tim19.UberApp.service.UserService;
import org.Tim19.UberApp.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/review")
@CrossOrigin(value = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private RideService rideService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyAuthority('PASSENGER')")
    @PostMapping(value = "/{rideId}/vehicle")
    public ResponseEntity postVehicleReview(@PathVariable Integer rideId, @RequestBody ReviewDTO reviewDTO){

        Optional<Ride> ride = rideService.findOneById(rideId);
        if(ride.isEmpty()){
            return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
        }

        reviewDTO.setVehicle(ride.get().getDriver().getVehicle().getId());
        reviewDTO.setRide(rideId);
        reviewDTO = reviewService.saveVehicle(reviewDTO);

        return new ResponseEntity<>(reviewDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(value = "/vehicle/{id}")
    public ResponseEntity getVehicleReviews(@PathVariable Integer id){
        Optional<Vehicle> vehicle = vehicleService.findOne(id);
        if(vehicle.isEmpty()){
            return new ResponseEntity<>("Vehicle does not exist!", HttpStatus.NOT_FOUND);
        }

        Set<Review> reviews = reviewService.findByVehicleId(id);

        //TODO: mapiraj review na reviewDTO
        Set<ReviewDTO> reviewDTOS = new HashSet<>();

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", reviews.size());
        response.put("results", reviews);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('PASSENGER')")
    @PostMapping(value ="/{rideId}/driver" )
    public ResponseEntity postDriverReview(@PathVariable Integer rideId, @RequestBody ReviewDTO reviewDTO){

        Optional<Ride> ride = rideService.findOneById(rideId);
        if(ride.isEmpty()){
            return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
        }
        reviewDTO.setDriver(ride.get().getDriver().getId());
        reviewDTO.setRide(rideId);
        reviewDTO = reviewService.saveDriver(reviewDTO);

        return new ResponseEntity<>(reviewDTO, HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(value = "/driver/{id}")
    public ResponseEntity getDriverReviews(@PathVariable Integer id){
        User driver = userService.findOneById(id);
        if(driver == null){
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }

        Set<Review> reviews = reviewService.findByDriverId(id);

        //TODO: mapiraj review na reviewDTO
        Set<ReviewDTO> reviewDTOS = new HashSet<>();

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", reviews.size());
        response.put("results", reviews);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(value = "{rideId}")
    public ResponseEntity getRideReviews(@PathVariable Integer rideId){

        Optional<Ride> ride = rideService.findOneById(rideId);
        if(ride.isEmpty()){
            return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
        }

        Set<Review> reviews = reviewService.findByRideId(rideId);

        //TODO: mapiraj review na reviewDTO
        Set<ReviewDTO> reviewDTOS = new HashSet<>();

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", reviews.size());
        response.put("results", reviews);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
