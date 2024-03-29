package org.Tim19.UberApp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.Tim19.UberApp.model.Driver;
import org.Tim19.UberApp.model.DriverDocument;
import org.Tim19.UberApp.model.Ride;
import org.Tim19.UberApp.model.Vehicle;

import java.util.HashSet;
import java.util.Set;

public class DriverDTO extends UserDTO{

//    @JsonIgnore
//    private Set<Ride> rides = new HashSet<Ride>();
    @JsonIgnore
    private Set<DriverDocument> documents = new HashSet<>();
    private Integer vehicle;

    public DriverDTO(Integer id, String firstname, String lastname, String profilePicture, String telephoneNumber, String email, String address, String password, Boolean active, Boolean blocked, Integer vehicle) {
        super(id, email, firstname, lastname, profilePicture, telephoneNumber, address, password, active, blocked);
        //this.documents = documents;
        this.vehicle = vehicle;
    }
    public DriverDTO() {}

    public DriverDTO(Driver driver){
        this(driver.getId(), driver.getName(), driver.getSurname(), driver.getProfilePicture(), driver.getTelephoneNumber(), driver.getUsername(), driver.getAddress(), driver.getPassword(), driver.getActive(), driver.getBlocked(), driver.getVehicle().getId());
    }

//    public Set<Ride> getRides() {
//        return rides;
//    }

    public Set<DriverDocument> getDocuments() {
        return documents;
    }

    public Integer getVehicle() {
        return vehicle;
    }
}
