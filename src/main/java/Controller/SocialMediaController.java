package Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.*;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        
        app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::retrieveAllMessagesHandler);
        app.get("/messages/{message_id}", this::retrieveMessagesByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountIdHandler);



        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    private void registerHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Account acc = om.readValue(ctx.body(), Account.class);
        Account addedAcc = accountService.addAccount(acc);

        if(addedAcc == null) {
            ctx.status(400);
        }
        else if (addedAcc.getUsername() == null || addedAcc.getUsername().isBlank() || addedAcc.getPassword().length() < 4) {
            ctx.status(400);
        }else {
           ctx.json(om.writeValueAsString(addedAcc));
           ctx.status(200);
        }
    }

    private void loginHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Account acc = om.readValue(ctx.body(), Account.class);
        Account addedAcc = accountService.checkAccount(acc);

        if(addedAcc == null) {
            ctx.status(401);
        } else {
            ctx.json(om.writeValueAsString(addedAcc));
            ctx.status(200);
        }

    }

    private void createMessageHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Message msg = om.readValue(ctx.body(), Message.class);
        Message createdMessage = messageService.createMsg(msg);

        if(createdMessage == null) {
            ctx.status(400);
        } else if (createdMessage.getMessage_text() == null || createdMessage.getMessage_text().length() >= 255 || createdMessage.getMessage_text().isBlank()) {
            ctx.status(400);
        }else {
            ctx.json(om.writeValueAsString(createdMessage));
            ctx.status(200);
        }
    }

    private void retrieveAllMessagesHandler(Context ctx) {
        List<Message> msgs = messageService.getAllMsgs();
        ctx.json(msgs);
        ctx.status(200);
    }

    private void retrieveMessagesByIdHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message msg = messageService.getMsgById(messageId);
        if (msg != null) {
            ctx.json(msg);
        } else {
            ctx.result("");
        }
        ctx.status(200);
    }

    private void deleteMessageHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
    
        Message deletedMessage = messageService.deleteMessage(messageId);
    
        if (deletedMessage != null) {
            ctx.json(deletedMessage);
        } else {
            ctx.result("");
        }
        ctx.status(200);
    }

    private void updateMessageHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        ObjectMapper om = new ObjectMapper();

        try {
            JsonNode requestBody = om.readTree(ctx.body());
            String newMessageText = requestBody.get("message_text").asText();

            if (newMessageText == null || newMessageText.isBlank() || newMessageText.length() > 255) {
                ctx.status(400);
                return;
            }

            Message updatedMessage = messageService.updateMessage(messageId, newMessageText);

            if (updatedMessage != null) {
                ctx.json(updatedMessage);
                ctx.status(200);
            } else {
                ctx.status(400);
            }
        } catch (Exception e) {
            ctx.status(400);
        }

    }

    private void getMessagesByAccountIdHandler(Context ctx) {
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));
    
        List<Message> messages = messageService.getMessagesByAccountId(accountId);
    
        ctx.json(messages);
        ctx.status(200);
    }
    

    



}