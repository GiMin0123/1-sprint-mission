package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    void setDependencies(UserService userService, ChannelService channelService);
    Message createMessage(Message message);
    Optional<Message> readMessage(Message message);
    List<Message> readAll();
    Message updateByAuthor(User user, Message message);
    boolean deleteMessage(Message message);
    void deleteMessageByChannel(Channel channel);
    void deleteMessageByUser(User user);
}