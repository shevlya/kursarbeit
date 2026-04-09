package ru.ssau.srestapp.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventParticipantId implements Serializable {
    private Long idUser;
    private Long idEvent;
}
