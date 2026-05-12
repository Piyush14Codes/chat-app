package com.chatapp.demo.controller;

import com.chatapp.demo.dto.MessageDeliveredDTO;
import com.chatapp.demo.dto.MessageReadDTO;
import com.chatapp.demo.dto.MessageRequestDTO;
import com.chatapp.demo.model.Message;
import com.chatapp.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

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

    @MessageMapping("/chat.delivered")
    public void markAsDelivered(MessageDeliveredDTO dto, Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        Message updatedMessage = messageService.markMessageAsDelivered(dto.getMessageId(), userId);

        messagingTemplate.convertAndSendToUser(
                updatedMessage.getSenderId().toString(),
                "/queue/status",
                updatedMessage
        );
    }

    @MessageMapping("/chat.read")
    public void markAsRead(MessageReadDTO dto, Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        Message updatedMessage = messageService.markMessageAsRead(dto.getMessageId(), userId);

        messagingTemplate.convertAndSendToUser(
                updatedMessage.getSenderId().toString(),
                "/queue/status",
                updatedMessage
        );
    }
}
