package ru.ssau.srestapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(UserInterestId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInterest {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User idUser;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_event_category", nullable = false)
    private EventCategory idEventCategory;
}
