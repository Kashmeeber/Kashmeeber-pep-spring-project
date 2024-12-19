package com.example.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.Account;
import com.example.repository.AccountRepository;
import com.example.entity.Message;
import com.example.repository.MessageRepository;
import java.util.List;

@Service
public class MessageService {
    MessageRepository messageRepository;
    AccountRepository accountRepository; 

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }   

    public Message addMessage(Message msg) {
        return messageRepository.save(msg);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public List<Message> getAllMessagesByUser(int id) {
        Account acc = accountRepository.findAccountByAccountId(id);
        if (acc != null) {
            return messageRepository.findMessagesByPostedBy(id);
        }
        return null;
    }
    
    public void removeMessage(Message msg) {
        messageRepository.delete(msg);
    }
}
