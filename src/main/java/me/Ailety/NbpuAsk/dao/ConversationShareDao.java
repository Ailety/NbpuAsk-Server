package me.Ailety.NbpuAsk.dao;

import me.Ailety.NbpuAsk.model.ConversationShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConversationShareDao {

    ConversationShare getActiveShareByConversationId(@Param("conversationId") String conversationId,
                                                     @Param("ownerUserId") Long ownerUserId);

    ConversationShare getActiveShareByShareId(@Param("shareId") String shareId);

    List<ConversationShare> getActiveSharesByOwnerUserId(@Param("ownerUserId") Long ownerUserId);

    void upsertShare(ConversationShare conversationShare);

    void cancelShare(@Param("conversationId") String conversationId,
                     @Param("ownerUserId") Long ownerUserId);

    void deleteSharesByConversation(@Param("conversationId") String conversationId,
                                    @Param("ownerUserId") Long ownerUserId);

    void deleteSharesByOwnerUserId(@Param("ownerUserId") Long ownerUserId);

    void incrementVisitCount(@Param("shareId") String shareId,
                             @Param("lastVisitedTime") String lastVisitedTime);
}
