package com.chatapp.demo.service;

import com.chatapp.demo.enums.MessageStatus;
import com.chatapp.demo.model.Message;
import com.chatapp.demo.model.User;
import com.chatapp.demo.repository.MessageRepository;
import com.chatapp.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.RuntimeErrorException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService implements IMessageService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Message sendMessage(Message message) {

        //Validate sender
        User sender = userRepository.findById(message.getSenderId()).
                orElseThrow(() -> new RuntimeException("Sender not found"));

        //Validate receiver
        User receiver = userRepository.findById(message.getReceiverId()).
                orElseThrow(() -> new RuntimeException("Receiver not found"));

        //Validate content
        if(message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new RuntimeException("Message content cannot be empty");
        }

        //set time-stamp
        message.setTimestamp(LocalDateTime.now());

        //set status
        message.setStatus(MessageStatus.SENT);

        //save message
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getConversation(Long user1 , Long user2) {

        //validate user 1
        User u1 = userRepository.findById(user1).
                orElseThrow(() -> new RuntimeException("User not found"));

        //validate user 2
        User u2 = userRepository.findById(user2).
                orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
                user1, user2,
                user2 , user1);
    }

    public Message markMessageAsDelivered(Long messageId, Long userId) {

        Message message = messageRepository.findById(messageId).
                orElseThrow(() -> new RuntimeException("Message not found"));

        if(!message.getReceiverId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        message.setStatus(MessageStatus.DELIVERED);

        return messageRepository.save(message);
    }

    public Message markMessageAsRead(Long messageId, Long userId) {

        Message message = messageRepository.findById(messageId).
                orElseThrow(() -> new RuntimeException("Message not found"));

        if(!message.getReceiverId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        message.setStatus(MessageStatus.READ);

        return messageRepository.save(message);
    }
}
