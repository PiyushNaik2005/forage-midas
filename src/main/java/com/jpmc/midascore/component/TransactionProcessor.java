package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class TransactionProcessor {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final TransactionValidator transactionValidator;
    private final IncentiveClient incentiveClient;

    public TransactionProcessor(UserRepository userRepository,
                                TransactionRecordRepository transactionRecordRepository,
                                TransactionValidator transactionValidator,
                                IncentiveClient incentiveClient) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.transactionValidator = transactionValidator;
        this.incentiveClient = incentiveClient;
    }

    public void process(Transaction transaction) {
        if (!transactionValidator.isValid(transaction)) {
            return;
        }

        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        Incentive incentive = incentiveClient.getIncentive(transaction);
        float incentiveAmount = incentive != null ? incentive.getAmount() : 0f;

        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        userRepository.save(sender);
        userRepository.save(recipient);

        transactionRecordRepository.save(new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount));
    }
}