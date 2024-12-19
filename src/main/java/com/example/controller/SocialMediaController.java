package com.example.controller;
import org.springframework.web.bind.annotation.*;
import com.example.entity.Account;
import com.example.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;
import com.example.service.AccountService;
import com.example.service.MessageService;
import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private MessageService messageService;

    @PostMapping("/register")
    public ResponseEntity<Account> registerAccount(@RequestBody Account acc) {
        String username = acc.getUsername().strip();
        String password = acc.getPassword().strip();
        if (username.length() < 1 || password.length() < 4) {
            return ResponseEntity.status(400).body(null);
        }
        Account existingAccount = accountRepository.findAccountByUsername(username);
        if (existingAccount != null) {
            return ResponseEntity.status(409).body(null);
        }
        Account newAccount = new Account();
        newAccount.setUsername(username);
        newAccount.setPassword(password);
        newAccount = accountService.addAccount(newAccount);
        return ResponseEntity.status(200).body(newAccount);
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account acc) {
        Account login = accountRepository.findAccountByUsernameAndPassword(acc.getUsername(), acc.getPassword());
        if (login != null) {
            return ResponseEntity.status(200).body(login);
        }
        return ResponseEntity.status(401).body(null);
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message msg) {
        String msgTxt = msg.getMessageText().trim();
        int postedBy = msg.getPostedBy();
        Account acc = accountRepository.findAccountByAccountId(postedBy);
        if (msgTxt.length() == 0 || msgTxt.length() > 255 || acc == null) {
            return ResponseEntity.status(400).body(null);
        }
        Message newMessage = new Message();
        newMessage.setMessageText(msgTxt);
        newMessage.setPostedBy(postedBy);
        newMessage.setTimePostedEpoch(msg.getTimePostedEpoch());
        newMessage = messageService.addMessage(newMessage);
        return ResponseEntity.status(200).body(newMessage);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.status(200).body(messageService.getAllMessages());
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable int messageId) {
        return ResponseEntity.status(200).body(messageRepository.findMessageByMessageId(messageId));
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable int messageId) {
        Message msg = messageRepository.findMessageByMessageId(messageId);
        if (msg == null) {
            return ResponseEntity.status(200).body(null);
        }
        messageService.removeMessage(msg);
        return ResponseEntity.status(200).body(1);
    }

    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessage(@PathVariable int messageId, @RequestBody Message message) {
        Message msg = messageRepository.findMessageByMessageId(messageId);
        String msgTxt = message.getMessageText(); 
        if (msg == null || msgTxt.strip().length() == 0 || msgTxt.strip().length() > 255) {
            return ResponseEntity.status(400).body(null);
        }
        msg.setMessageText(msgTxt);
        messageRepository.save(msg);
        return ResponseEntity.status(200).body(1);
    }

    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getAllMessagesByUserId(@PathVariable int accountId) {
        return ResponseEntity.status(200).body(messageRepository.findMessagesByPostedBy(accountId));
    }

}