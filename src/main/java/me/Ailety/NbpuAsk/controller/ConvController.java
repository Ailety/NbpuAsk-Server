package me.Ailety.NbpuAsk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import me.Ailety.NbpuAsk.model.Conversation;
import me.Ailety.NbpuAsk.model.ConversationShare;
import me.Ailety.NbpuAsk.model.ModelRequest;
import me.Ailety.NbpuAsk.model.User;
import me.Ailety.NbpuAsk.service.AuthService;
import me.Ailety.NbpuAsk.service.ConvService;
import me.Ailety.NbpuAsk.util.Result;
import me.Ailety.NbpuAsk.util.ResultCodeEnum;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/conversation")
public class ConvController {

    @Autowired(required = false)
    ConvService convService;

    @Autowired
    AuthService authService;

    // 创建新的对话
    @PostMapping(value = "/create")
    public Result<?> createConv(@RequestHeader("Authorization") String authHeader) {

        User authenticatedUser = authService.getAuthenticatedUser(authHeader);
        if (authenticatedUser == null)
            return Result.failure(ResultCodeEnum.TOKEN_VERIFY_FAIL);

        try {
            Conversation newConv = convService.createService(authenticatedUser.getUserId());
            if (newConv != null) return Result.success(newConv);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(ResultCodeEnum.CONV_CREATE_EXCEPTION);
        }

        return Result.failure(ResultCodeEnum.CONV_CREATE_EXCEPTION);

    }

    // 获取指定用户的指定对话
    @PostMapping(value = "/get")
    public Result<?> getConv(@RequestHeader("Authorization") String authHeader,
                             @RequestBody Conversation conversation) {
        User authenticatedUser = authService.getAuthenticatedUser(authHeader);
        if (authenticatedUser == null)
            return Result.failure(ResultCodeEnum.TOKEN_VERIFY_FAIL);

        try {
            Conversation conv = convService.getService(authenticatedUser.getUserId(),
                    conversation.getConversationId());
            if (conv != null) return Result.success(conv);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(ResultCodeEnum.CONV_GET_EXCEPTION);
        }

        return Result.failure(ResultCodeEnum.CONV_NOT_FOUND);

    }

    // 公开分享页读取指定对话，只允许读取未删除的对话，不需要登录
    @GetMapping(value = "/shared/{conversationId}")
    public Result<?> getSharedConv(@PathVariable String conversationId) {
        if (conversationId == null || conversationId.trim().isEmpty()) {
            return Result.failure(ResultCodeEnum.PARAMS_IS_INVALID);
        }

        try {
            Object sharedConversation = convService.getSharedService(conversationId.trim());
            if (sharedConversation != null) return Result.success(sharedConversation);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(ResultCodeEnum.CONV_GET_EXCEPTION);
        }

        return Result.failure(ResultCodeEnum.CONV_NOT_FOUND);
    }

    @PostMapping(value = "/share")
    public Result<?> shareConv(@RequestHeader("Authorization") String authHeader,
                               @RequestBody ConversationShare conversationShare) {
        if (conversationShare == null
                || conversationShare.getConversationId() == null
                || conversationShare.getConversationId().trim().isEmpty()) {
            return Result.failure(ResultCodeEnum.PARAMS_IS_INVALID);
        }

        User authenticatedUser = authService.getAuthenticatedUser(authHeader);
        if (authenticatedUser == null) {
            return Result.failure(ResultCodeEnum.TOKEN_VERIFY_FAIL);
        }

        try {
            ConversationShare share = convService.shareService(
                    authenticatedUser.getUserId(),
                    conversationShare.getConversationId(),
                    conversationShare.getShareId()
            );
            if (share != null) return Result.success(share);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(ResultCodeEnum.CONV_SET_EXCEPTION);
        }

        return Result.failure(ResultCodeEnum.CONV_NOT_FOUND);
    }

    @PostMapping(value = "/share/cancel")
    public Result<?> cancelShareConv(@RequestHeader("Authorization") String authHeader,
                                     @RequestBody ConversationShare conversationShare) {
        if (conversationShare == null
                || conversationShare.getConversationId() == null
                || conversationShare.getConversationId().trim().isEmpty()) {
            return Result.failure(ResultCodeEnum.PARAMS_IS_INVALID);
        }

        User authenticatedUser = authService.getAuthenticatedUser(authHeader);
        if (authenticatedUser == null) {
            return Result.failure(ResultCodeEnum.TOKEN_VERIFY_FAIL);
        }

        try {
            convService.cancelShareService(authenticatedUser.getUserId(), conversationShare.getConversationId());
            return Result.success();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(ResultCodeEnum.CONV_SET_EXCEPTION);
        }
    }

    @PostMapping(value = "/shares")
    public Result<?> getShares(@RequestHeader("Authorization") String authHeader) {
        User authenticatedUser = authService.getAuthenticatedUser(authHeader);
        if (authenticatedUser == null) {
            return Result.failure(ResultCodeEnum.TOKEN_VERIFY_FAIL);
        }

        try {
            List<ConversationShare> shares = convService.getShareService(authenticatedUser.getUserId());
            return Result.success(shares);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(ResultCodeEnum.CONV_GET_EXCEPTION);
        }
    }

    // 获取指定用户的全部对话
    @PostMapping(value = "/get-all")
    public Result<?> getConvs(@RequestHeader("Authorization") String authHeader) {

        User authenticatedUser = authService.getAuthenticatedUser(authHeader);
        if (authenticatedUser == null)
            return Result.failure(ResultCodeEnum.TOKEN_VERIFY_FAIL);

        try {
            List<Conversation> convs = convService.getAllService(authenticatedUser.getUserId());
            if (convs != null) return Result.success(convs);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(ResultCodeEnum.CONV_GET_EXCEPTION);
        }

        return Result.failure(ResultCodeEnum.CONV_GET_EXCEPTION);

    }

    // 设置指定用户的指定对话
    @PostMapping(value = "/set")
    public Result<?> setConv(@RequestHeader("Authorization") String authHeader,
                             @RequestBody Conversation conversation) {

        User authenticatedUser = authService.getAuthenticatedUser(authHeader);
        if (authenticatedUser == null)
            return Result.failure(ResultCodeEnum.TOKEN_VERIFY_FAIL);

        try {
            conversation.setConversationUserId(authenticatedUser.getUserId());
            convService.setService(conversation);
            return Result.success();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(ResultCodeEnum.CONV_SET_EXCEPTION);
        }

    }

    // 删除指定用户的指定对话
    @PostMapping(value = "/delete")
    public Result<?> deleteConv(@RequestHeader("Authorization") String authHeader,
                                @RequestBody Conversation conversation) {

        User authenticatedUser = authService.getAuthenticatedUser(authHeader);
        if (authenticatedUser == null)
            return Result.failure(ResultCodeEnum.TOKEN_VERIFY_FAIL);

        try {
            convService.deleteService(authenticatedUser.getUserId(),
                    conversation.getConversationId());
            return Result.success();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(ResultCodeEnum.CONV_DELETE_EXCEPTION);
        }

    }

    // 删除指定用户的全部对话
    @PostMapping(value = "/delete-all")
    public Result<?> deleteAllConvs(@RequestHeader("Authorization") String authHeader) {

        User authenticatedUser = authService.getAuthenticatedUser(authHeader);
        if (authenticatedUser == null)
            return Result.failure(ResultCodeEnum.TOKEN_VERIFY_FAIL);

        try {
            convService.deleteAllService(authenticatedUser.getUserId());
            return Result.success();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(ResultCodeEnum.CONV_DELETE_EXCEPTION);
        }

    }

    @PostMapping(value = "/runs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                 @RequestBody ModelRequest modelRequest,
                                 HttpServletResponse response) throws IOException {

        // 防缓存
        response.setHeader("Cache-Control", "no-cache, no-transform");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");

        User authenticatedUser = authService.getAuthenticatedUser(authHeader);
        if (authenticatedUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            SseEmitter unauthorizedEmitter = new SseEmitter(0L);
            unauthorizedEmitter.complete();
            return unauthorizedEmitter;
        }

        modelRequest.setUserId(authenticatedUser.getUserId());

        // 创建 SseEmitter，参数 0L 表示永不超时
        SseEmitter emitter = new SseEmitter(0L);

        // 调用服务拿到流
        Flux<String> fluxStream = convService.runsService(modelRequest);

        java.util.concurrent.atomic.AtomicBoolean clientDisconnected = new java.util.concurrent.atomic.AtomicBoolean(false);

        fluxStream.subscribe(
                chunk -> {
                    if (clientDisconnected.get()) {
                        return; // 如果前端已断开，不再往网络层推送，但继续消耗流以让后端存库逻辑执行完毕
                    }
                    try {
                        // 包装为 JSON 对象，避免单字符 '\n' 被 SSE 协议截断丢失
                        Map<String, String> data = new HashMap<>();
                        data.put("text", chunk);
                        emitter.send(SseEmitter.event().data(data));
                    } catch (Exception e) {
                        // 捕获 IOException 或 IllegalStateException
                        // 前端断开连接，标记状态但不抛出异常，让 Flux 继续在后台跑完
                        clientDisconnected.set(true);
                        try {
                            emitter.completeWithError(e);
                        } catch (Exception ignored) {}
                    }
                },
                error -> {
                    if (!clientDisconnected.get()) {
                        try {
                            sendStreamError(emitter, error);
                            emitter.complete();
                        } catch (Exception ignored) {}
                    }
                },
                () -> {
                    if (!clientDisconnected.get()) {
                        try {
                            emitter.complete();
                        } catch (Exception ignored) {}
                    }
                }
        );

        // 立即返回 emitter 对象给 Tomcat，保持连接通道敞开
        return emitter;
    }

    private void sendStreamError(SseEmitter emitter, Throwable error) throws IOException {
        ResultCodeEnum resultCode = error instanceof IllegalStateException
                ? ResultCodeEnum.CONV_PENDING_RESPONSE
                : ResultCodeEnum.CONV_RUN_EXCEPTION;
        Map<String, Object> data = new HashMap<>();
        data.put("error", true);
        data.put("code", resultCode.getCode());
        data.put("message", error.getMessage() == null ? resultCode.getMessage() : error.getMessage());
        emitter.send(SseEmitter.event().name("error").data(data));
    }

}
