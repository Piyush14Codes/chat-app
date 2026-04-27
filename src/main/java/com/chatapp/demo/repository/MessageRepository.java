package com.chatapp.demo.repository;

import com.chatapp.demo.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {
    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(Long sender1 , Long receiver1,
                                                                     Long sender2 , Long receiver2);
}
