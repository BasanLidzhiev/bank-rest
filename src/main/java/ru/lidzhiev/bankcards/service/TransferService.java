package ru.lidzhiev.bankcards.service;

import ru.lidzhiev.bankcards.dto.TransferRequestDto;
import ru.lidzhiev.bankcards.entity.Transaction;

/**
 * Интерфейс для сервисов, обеспечивающих переводы средств между банковскими картами.
 * Определяет контракт метода для выполнения денежного перевода.
 */
public interface TransferService {
    /**
     * Выполняет перевод средств согласно переданному запросу.
     * Метод производит снятие указанной суммы с одной карты и зачисление на другую.
     * Если операция успешна, сохраняется соответствующая транзакция.
     *
     * @param transferRequest объект с данными о переводе (карта-отправитель, карта-получатель, сумма).
     * @return объект транзакции, отражающей совершённый перевод.
     */
    Transaction transfer(TransferRequestDto transferRequest);
}
