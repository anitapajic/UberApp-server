package org.Tim19.UberApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Rejection {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ManyToOne
    private User user;

    private String reason;

    private LocalDateTime timeOfRejection;

    @JsonIgnore
    @ManyToOne
    private Ride ride;


    @Override
    public String toString() {
        return "Rejection{" +
                "id=" + id +
                ", user=" + user +
                ", reason='" + reason + '\'' +
                ", timeOfRejection=" + timeOfRejection +
                '}';
    }
}
