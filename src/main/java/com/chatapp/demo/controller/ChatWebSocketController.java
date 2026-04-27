package com.chatapp.demo.controller;

import com.chatapp.demo.dto.MessageRequestDTO;
import com.chatapp.demo.model.Message;
import com.chatapp.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    @Autowired
    MessageService messageService;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(MessageRequestDTO request) {

        Message message = new Message();
        message.setSenderId(request.getSenderId());
        message.setReceiverId(request.getReceiverId());
        message.setContent(request.getContent());

        Message saved = messageService.sendMessage(message);

        //send to receiver
        messagingTemplate.convertAndSend(
                "/topics/messages/" + saved.getReceiverId(),
                saved
        );
    }
}
