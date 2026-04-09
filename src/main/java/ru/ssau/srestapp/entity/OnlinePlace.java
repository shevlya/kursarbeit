package ru.ssau.srestapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@PrimaryKeyJoinColumn(name = "id_place")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnlinePlace extends Place {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String meetingUrl;

    @Column(columnDefinition = "TEXT")
    private String specialNotes;

    @Column(nullable = false)
    private Boolean recording = false;
}
