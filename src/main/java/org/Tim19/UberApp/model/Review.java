package org.Tim19.UberApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@NoArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ManyToOne(cascade ={CascadeType.ALL},
            fetch = FetchType.LAZY)
    private User user;

    private String comment;

    private Integer rating;

    @JsonIgnore
    @ManyToOne(cascade ={CascadeType.ALL},
            fetch = FetchType.LAZY)
    private Ride ride;

    public Review(Integer id, User user, String comment, Integer rating, Ride ride) {
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.rating = rating;
        this.ride = ride;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }
}
