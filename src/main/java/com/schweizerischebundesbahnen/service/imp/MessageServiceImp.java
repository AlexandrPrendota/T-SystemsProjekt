package com.schweizerischebundesbahnen.service.imp;

import com.schweizerischebundesbahnen.model.*;
import com.schweizerischebundesbahnen.repository.ChatRepository;
import com.schweizerischebundesbahnen.repository.MessageRepository;
import com.schweizerischebundesbahnen.repository.UserChatRepository;
import com.schweizerischebundesbahnen.repository.UserRepository;
import com.schweizerischebundesbahnen.service.api.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by aleksandrprendota on 06.05.17.
 */

@Service
public class MessageServiceImp implements MessageService{

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserChatRepository userChatRepository;

    @Override
    public Message postMessage(long from, MessageDTO messageDTO) {

        User sender = userRepository.findOne(from);
        User recipient = userRepository.findOne(messageDTO.getRecipientId());
        Message message = new Message();
        message.setText(messageDTO.getText());
        message.setTimeStamp(messageDTO.getTimeStamp());
        message.setSender(sender);
        UserChat filteredChat = null;
        if(messageDTO.getChatId() != null){
            Chat chat = chatRepository.findOne(messageDTO.getChatId());
            if(chat != null){
                message.setChat(chat);
                return messageRepository.save(message);
            }
        }
        List<UserChat> userChats = userChatRepository.findByUser(sender);
        for (UserChat userChat : userChats) {
            filteredChat = userChatRepository.findByChatAndUser(userChat.getChat(), recipient);
            if(filteredChat != null) {
                break;
            }
        }
        if(filteredChat != null){
            message.setChat(filteredChat.getChat());
        } else {
            Chat chat = chatRepository.save(new Chat());
            UserChat first = new UserChat();
            first.setChat(chat);
            first.setUser(sender);
            userChatRepository.save(first);
            UserChat second = new UserChat();
            second.setUser(recipient);
            second.setChat(chat);
            userChatRepository.save(second);
            message.setChat(chat);
        }
        return messageRepository.save(message);
    }

    @Override
    public List<Message> findByChat(Chat id) {
        return messageRepository.findByChat(id);
    }
}
