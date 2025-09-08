package ru.lidzhiev.bankcards.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.lidzhiev.bankcards.dto.CardDto;
import ru.lidzhiev.bankcards.dto.CreateCardDto;
import ru.lidzhiev.bankcards.dto.TransferRequestDto;
import ru.lidzhiev.bankcards.exception.CardOperationException;
import ru.lidzhiev.bankcards.exception.ResourceNotFoundException;

import java.util.List;
/**
 * Интерфейс CardService описывает сервисы для управления банковскими картами.
 * Включает операции по созданию, просмотру, изменению состояния и переводу средств между картами.
 */
public interface CardService {
    /**
     * Создание новой банковской карты.
     * Требует наличия роли ADMIN. Создается новая карта с заданным балансом, сроком действия и владельцем.
     *
     * @param dto      объект DTO с информацией о создаваемой карте.
     * @param username имя пользователя-владельца карты.
     * @return объект DTO созданной карты.
     * @throws CardOperationException если срок действия истек или недостаточно средств.
     */
    CardDto create(CreateCardDto dto, String username);
    /**
     * Пользователь запрашивает блокировку своей карты.
     * Изменяется статус карты на REQUEST_BLOCKED.
     *
     * @param dto      объект DTO с информацией о карте.
     * @param username имя пользователя, запрашивающего блокировку.
     * @return объект DTO обновленной карты.
     * @throws CardOperationException если карта принадлежит другому владельцу.
     */
    CardDto userRequestCardBlock(CardDto dto, String username);
    /**
     * Блокирует карту административным путем.
     * Треубет наличие роли ADMIN. Меняет статус карты на BLOCKED.
     *
     * @param cardId ID карты.
     * @return объект DTO заблокированной карты.
     * @throws ResourceNotFoundException если карта не найдена.
     */
    CardDto blockCard(Long cardId);
    /**
     * Возвращает список карт определенного пользователя.
     *
     * @param username имя пользователя.
     * @param pageable объект пагинации.
     * @return страница объектов DTO карт.
     */
    Page<CardDto> getByUsername(String username, Pageable pageable);
    /**
     * Возвращает информацию о конкретной карте по её ID.
     *
     * @param id ID карты.
     * @return объект DTO найденной карты.
     * @throws ResourceNotFoundException если карта не найдена.
     */
    CardDto getById(Long id);
    /**
     * Обновляет статус карты администраторским действием.
     * Требуется роль ADMIN. Устанавливает новый статус карты.
     *
     * @param id     ID карты.
     * @param status новый статус карты.
     * @return объект DTO обновленной карты.
     * @throws ResourceNotFoundException если карта не найдена.
     * @throws CardOperationException    если указанный статус недействителен.
     */
    CardDto adminUpdateCardStatus(Long id, String status);
    /**
     * Удаляет карту администраторским действием.
     * Требуется роль ADMIN.
     *
     * @param id ID удаляемой карты.
     * @throws ResourceNotFoundException если карта не найдена.
     */
    void deleteCard(Long id);
    /**
     * Возвращает полный список всех карт (для администраторов).
     * Доступно только пользователям с ролью ADMIN.
     *
     * @return список объектов DTO всех карт.
     */
    List<CardDto> getAllCards();
    /**
     * Выполняет денежный перевод между двумя картами.
     * Осуществляется транзакционный перевод средств с одной карты на другую.
     *
     * @param dto       объект DTO с деталями перевода.
     * @param username  имя пользователя владельца карт.
     * @throws CardOperationException если возникают проблемы с переводом (недостаточно средств, неправильный владелец и др.).
     */
    void transfer(TransferRequestDto dto, String username);
}
