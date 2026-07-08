package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class TransactionValidator {

    private final UserRepository userRepository;

    public TransactionValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isValid(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender == null || recipient == null) {
            return false;
        }

        return sender.getBalance() >= transaction.getAmount();
    }
}