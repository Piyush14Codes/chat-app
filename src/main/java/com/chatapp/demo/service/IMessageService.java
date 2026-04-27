package com.chatapp.demo.service;

import com.chatapp.demo.model.Message;

import java.util.List;

public interface IMessageService {
    public Message sendMessage(Message message);

    public List<Message> getConversation(Long user1 , Long user2);
}
