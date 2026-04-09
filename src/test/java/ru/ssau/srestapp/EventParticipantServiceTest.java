package ru.ssau.srestapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ssau.srestapp.dto.eventParticipant.EventParticipantResponseDto;
import ru.ssau.srestapp.entity.*;
import ru.ssau.srestapp.exception.*;
import ru.ssau.srestapp.repository.EventParticipantRepository;
import ru.ssau.srestapp.repository.EventRepository;
import ru.ssau.srestapp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventParticipantService — тесты")
class EventParticipantServiceTest {

    @Mock private EventParticipantRepository eventParticipantRepository;
    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @Mock private EmailService emailService;

    @InjectMocks private EventParticipantService eventParticipantService;

    private User testUser;
    private Event testEvent;
    private EventParticipant testParticipant;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setIdUser(1L);
        testUser.setEmail("user@test.com");
        testUser.setFio("Иванов Иван");

        testEvent = new Event();
        testEvent.setIdEvent(10L);
        testEvent.setEventName("Тестовое мероприятие");
        testEvent.setStartTime(LocalDateTime.of(2026, 5, 1, 10, 0));
        testEvent.setMaxParticipants(5);

        testParticipant = new EventParticipant();
        testParticipant.setIdUser(testUser);
        testParticipant.setIdEvent(testEvent);
        testParticipant.setParticipationStatus(ParticipationStatus.REGISTERED);
        testParticipant.setRegistrationDate(LocalDateTime.now());
    }

    // ==================== register() ====================

    @Test
    @DisplayName("register: успешная регистрация нового участника")
    void register_newParticipant_success() throws Exception {
        // Given
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(eventRepository.findById(10L)).willReturn(Optional.of(testEvent));
        given(eventParticipantRepository.findByIdUser_IdUserAndIdEvent_IdEvent(1L, 10L))
                .willReturn(Optional.empty());
        given(eventParticipantRepository.countByIdEvent_IdEventAndParticipationStatus(10L, ParticipationStatus.REGISTERED))
                .willReturn(2L); // меньше чем maxParticipants
        given(eventParticipantRepository.save(any(EventParticipant.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // When
        EventParticipantResponseDto result = eventParticipantService.register(1L, 10L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getParticipationStatus()).isEqualTo(ParticipationStatus.REGISTERED);

        // Проверка отправки email
        verify(emailService).sendParticipationConfirmed(
                eq("user@test.com"), eq("Иванов Иван"), eq("Тестовое мероприятие"),
                any(), any()
        );
    }

    @Test
    @DisplayName("register: участник уже зарегистрирован — ошибка")
    void register_alreadyExists_throwsException() {
        // Given
        testParticipant.setParticipationStatus(ParticipationStatus.REGISTERED);
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(eventRepository.findById(10L)).willReturn(Optional.of(testEvent));
        given(eventParticipantRepository.findByIdUser_IdUserAndIdEvent_IdEvent(1L, 10L))
                .willReturn(Optional.of(testParticipant));

        // When & Then
        assertThatThrownBy(() -> eventParticipantService.register(1L, 10L))
                .isInstanceOf(ParticipantAlreadyExistsException.class);
    }

    @Test
    @DisplayName("register: отменённый участник регистрируется снова")
    void register_cancelledParticipant_reregister() throws Exception {
        // Given
        testParticipant.setParticipationStatus(ParticipationStatus.CANCELLED);
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(eventRepository.findById(10L)).willReturn(Optional.of(testEvent));
        given(eventParticipantRepository.findByIdUser_IdUserAndIdEvent_IdEvent(1L, 10L))
                .willReturn(Optional.of(testParticipant));
        given(eventParticipantRepository.countByIdEvent_IdEventAndParticipationStatus(10L, ParticipationStatus.REGISTERED))
                .willReturn(4L); // есть место
        given(eventParticipantRepository.save(any(EventParticipant.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // When
        EventParticipantResponseDto result = eventParticipantService.register(1L, 10L);

        // Then
        assertThat(result.getParticipationStatus()).isEqualTo(ParticipationStatus.REGISTERED);
        verify(eventParticipantRepository).save(testParticipant); // обновлен существующий
    }

    @Test
    @DisplayName("register: лист ожидания при заполненных местах")
    void register_waitlist_whenFull() throws Exception {
        // Given
        testEvent.setMaxParticipants(2);
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(eventRepository.findById(10L)).willReturn(Optional.of(testEvent));
        given(eventParticipantRepository.findByIdUser_IdUserAndIdEvent_IdEvent(1L, 10L))
                .willReturn(Optional.empty());
        given(eventParticipantRepository.countByIdEvent_IdEventAndParticipationStatus(10L, ParticipationStatus.REGISTERED))
                .willReturn(2L); // мест нет
        given(eventParticipantRepository.save(any(EventParticipant.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // When
        EventParticipantResponseDto result = eventParticipantService.register(1L, 10L);

        // Then
        assertThat(result.getParticipationStatus()).isEqualTo(ParticipationStatus.WAITLISTED);
        verify(emailService, never()).sendParticipationConfirmed(any(), any(), any(), any(), any());
    }

    // ==================== cancelParticipation() ====================

    @Test
    @DisplayName("cancelParticipation: успешная отмена")
    void cancelParticipation_success() throws Exception {
        // Given
        given(eventParticipantRepository.findByIdUser_IdUserAndIdEvent_IdEvent(1L, 10L))
                .willReturn(Optional.of(testParticipant));
        given(eventParticipantRepository.save(any(EventParticipant.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(eventRepository.findById(10L)).willReturn(Optional.of(testEvent));

        // When
        EventParticipantResponseDto result = eventParticipantService.cancelParticipation(1L, 10L, false);

        // Then
        assertThat(result.getParticipationStatus()).isEqualTo(ParticipationStatus.CANCELLED);
        verify(emailService, never()).sendParticipationCancelled(any(), any(), any(), any());
    }

    @Test
    @DisplayName("cancelParticipation: нельзя отменить, если отклонён организатором")
    void cancelParticipation_rejectedByOrganizer_throwsException() {
        // Given
        testParticipant.setParticipationStatus(ParticipationStatus.REJECTED_BY_ORGANIZER);
        given(eventParticipantRepository.findByIdUser_IdUserAndIdEvent_IdEvent(1L, 10L))
                .willReturn(Optional.of(testParticipant));
        // 🔹 Исправление: удалён лишний мок eventRepository.findById()

        // When & Then
        assertThatThrownBy(() -> eventParticipantService.cancelParticipation(1L, 10L, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Нельзя отменить участие, если вы отклонены организатором");
    }

    // ==================== changeStatus() ====================

    @Test
    @DisplayName("changeStatus: смена статуса с отправкой email")
    void changeStatus_withEmailNotification() throws Exception {
        // Given
        given(eventParticipantRepository.findByIdUser_IdUserAndIdEvent_IdEvent(1L, 10L))
                .willReturn(Optional.of(testParticipant));
        given(eventParticipantRepository.save(any(EventParticipant.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // When
        EventParticipantResponseDto result = eventParticipantService.changeStatus(1L, 10L, ParticipationStatus.ATTENDED, true);

        // Then
        assertThat(result.getParticipationStatus()).isEqualTo(ParticipationStatus.ATTENDED);
        verify(emailService).sendParticipationAttended(
                eq("user@test.com"), eq("Иванов Иван"), eq("Тестовое мероприятие"), any()
        );
    }

    // ==================== Read-only методы ====================

    @Test
    @DisplayName("getParticipantsByEvent: возврат списка участников")
    void getParticipantsByEvent_returnsList() {
        // Given
        given(eventParticipantRepository.findByIdEvent_IdEvent(10L))
                .willReturn(List.of(testParticipant));

        // When
        var result = eventParticipantService.getParticipantsByEvent(10L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getRegisteredParticipants: фильтрация по статусу REGISTERED")
    void getRegisteredParticipants_filtersByStatus() {
        // Given
        EventParticipant waitlisted = new EventParticipant();
        waitlisted.setIdUser(testUser);
        waitlisted.setIdEvent(testEvent);
        waitlisted.setParticipationStatus(ParticipationStatus.WAITLISTED);

        given(eventParticipantRepository.findByIdEvent_IdEvent(10L))
                .willReturn(List.of(testParticipant, waitlisted));

        // When
        var result = eventParticipantService.getRegisteredParticipants(10L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getParticipationStatus()).isEqualTo(ParticipationStatus.REGISTERED);
    }

    // ==================== promoteFromWaitlist() ====================

    @Test
    @DisplayName("promoteFromWaitlist: продвижение первого из листа ожидания")
    void promoteFromWaitlist_promotesFirst() throws Exception {
        // Given
        EventParticipant waitlisted = new EventParticipant();
        waitlisted.setIdUser(testUser);
        waitlisted.setIdEvent(testEvent);
        waitlisted.setParticipationStatus(ParticipationStatus.WAITLISTED);
        waitlisted.setRegistrationDate(LocalDateTime.now().minusDays(1)); // первый в очереди

        given(eventParticipantRepository.countByIdEvent_IdEventAndParticipationStatus(10L, ParticipationStatus.REGISTERED))
                .willReturn(1L); // есть место
        given(eventRepository.findById(10L)).willReturn(Optional.of(testEvent));
        given(eventParticipantRepository.findByParticipationStatus(10L, ParticipationStatus.WAITLISTED))
                .willReturn(List.of(waitlisted));
        given(eventParticipantRepository.save(any(EventParticipant.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // When
        eventParticipantService.promoteFromWaitlist(10L, false);

        // Then
        assertThat(waitlisted.getParticipationStatus()).isEqualTo(ParticipationStatus.REGISTERED);
        verify(eventParticipantRepository).save(waitlisted);
    }

    @Test
    @DisplayName("promoteFromWaitlist: нет свободных мест — ничего не делаем")
    void promoteFromWaitlist_noFreeSpaces_doesNothing() throws Exception {
        // Given
        testEvent.setMaxParticipants(2);
        given(eventParticipantRepository.countByIdEvent_IdEventAndParticipationStatus(10L, ParticipationStatus.REGISTERED))
                .willReturn(2L); // мест нет
        given(eventRepository.findById(10L)).willReturn(Optional.of(testEvent));

        // When
        eventParticipantService.promoteFromWaitlist(10L, false);

        // Then
        verify(eventParticipantRepository, never()).save(any());
        verify(eventParticipantRepository, never()).findByParticipationStatus(any(), any());
    }

    // ==================== Вспомогательные методы ====================

    @Test
    @DisplayName("findParticipantOrThrow: участник не найден — ошибка")
    void findParticipantOrThrow_notFound_throwsException() {
        // Given
        given(eventParticipantRepository.findByIdUser_IdUserAndIdEvent_IdEvent(1L, 10L))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventParticipantService.getParticipant(1L, 10L))
                .isInstanceOf(ParticipantNotFoundException.class);
    }
}