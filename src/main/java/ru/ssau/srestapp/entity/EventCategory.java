package ru.ssau.srestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEventCategory;

    @Column(nullable = false, length = 100, unique = true)
    private String eventCategoryName;

    @Column(columnDefinition = "TEXT")
    private String eventCategoryDescription;

    @Column(length = 7)
    private String colorCode;
}
