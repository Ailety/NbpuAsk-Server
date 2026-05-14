package me.Ailety.NbpuAsk.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import me.Ailety.NbpuAsk.model.Conversation;

import java.util.List;

@Mapper
public interface ConversationDao {

    void createConversation(Conversation conversation);

    Conversation getConversation(@Param("conversationUserId") Long conversationUserId,
                                 @Param("conversationId") String conversationId);

    List<Conversation> getConversations(Long conversationUserId);

    void setConversation(Conversation conversation);

    void deleteConversation(@Param("conversationUserId") Long conversationUserId,
                            @Param("conversationId") String conversationId);

    void deleteConversations(Long conversationUserId);

}
