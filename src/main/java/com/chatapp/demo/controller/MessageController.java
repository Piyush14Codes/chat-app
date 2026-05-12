package com.chatapp.demo.controller;

import com.chatapp.demo.dto.MessageRequestDTO;
import com.chatapp.demo.dto.MessageResponseDTO;
import com.chatapp.demo.enums.MessageStatus;
import com.chatapp.demo.model.Message;
import com.chatapp.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public MessageResponseDTO sendMessage(@RequestBody MessageRequestDTO messageRequestDTO) {

        Message message = new Message();

        String userId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        message.setSenderId(Long.parseLong(userId));
        message.setReceiverId(messageRequestDTO.getReceiverId());
        message.setContent(messageRequestDTO.getContent());

        Message savedMessage = messageService.sendMessage(message);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setId(savedMessage.getId());
        response.setSenderId(savedMessage.getSenderId());
        response.setReceiverId(savedMessage.getReceiverId());
        response.setContent(savedMessage.getContent());
        response.setTimestamp(savedMessage.getTimestamp());
        response.setStatus(savedMessage.getStatus());

        return response;
    }

    @GetMapping("/conversation")
    public List<MessageResponseDTO> getConversation(@RequestParam Long user1,
                                                    @RequestParam Long user2) {

        List<Message> messages = messageService.getConversation(user1,user2);

        return messages.stream().map( msg -> {
            MessageResponseDTO dto = new MessageResponseDTO();
            dto.setId(msg.getId());
            dto.setSenderId(msg.getSenderId());
            dto.setReceiverId(msg.getReceiverId());
            dto.setTimestamp(msg.getTimestamp());
            dto.setContent(msg.getContent());
            return dto;
        }).toList();
    }
}
