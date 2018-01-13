package com.oziomek.craver.resource;

import com.oziomek.craver.persistence.model.Message;
import com.oziomek.craver.service.MessageService;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/messages")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class MessageResource {
    private MessageService messageService = new MessageService();

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<Message> getXMLMessages() {
        return messageService.getAllMessages();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
        public Response getJSONMessages() {
        List<Message> messages = messageService.getAllMessages();
        return Response.ok(messages)
                .build();
    }

    private String getUriSelf(UriInfo uriInfo) {
        URI uri = uriInfo.getAbsolutePathBuilder().build();
        return uri.toString();
    }

    private String getUriProfile(UriInfo uriInfo, Message message) {
        URI uri = uriInfo.getBaseUriBuilder()
                .path(ProfileResource.class)
                .path(message.getAuthor())
                .build();
        return uri.toString();
    }

    private String getUriComments(UriInfo uriInfo, Message message) {
        URI uri = uriInfo.getBaseUriBuilder()
                .path(MessageResource.class)
                .path(MessageResource.class, "getCommentResource")
                .path(CommentResource.class)
                .resolveTemplate("messageId", message.getId())
                .build();
//        URI uri = uriInfo.getAbsolutePathBuilder()
//                .path("comments")
//                .build();
        return uri.toString();
    }

    @POST
    public Response addMessage(Message message, @Context UriInfo uriInfo) {
        Message newMessage = messageService.addMessage(message);
        message.addLink(getUriSelf(uriInfo), "self");
        message.addLink(getUriProfile(uriInfo, message), "profile");
        message.addLink(getUriComments(uriInfo, message), "comments");

        if (newMessage != null) {
            return Response.status(Response.Status.CREATED)
                    .build();
        } else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @GET
    @Path("/{messageId}")
    public Response getMessageById(@PathParam("messageId") long id, @Context UriInfo uriInfo) {
        Message message = messageService.getMessage(id);
        message.addLink(getUriSelf(uriInfo), "self");
        message.addLink(getUriProfile(uriInfo, message), "profile");
        message.addLink(getUriComments(uriInfo, message), "comments");
        return Response.ok(message)
                .build();
    }

    @PUT
    @Path("/{messageId}")
    public Response updateMessage(@PathParam("messageId") long id, Message message, @Context UriInfo uriInfo) {
        message.setId(id);
        Message updatedMessage = messageService.updateMessage(message);
        updatedMessage.addLink(getUriSelf(uriInfo), "self");
        updatedMessage.addLink(getUriProfile(uriInfo, message), "profile");
        updatedMessage.addLink(getUriComments(uriInfo, message), "comments");
        return Response.ok(updatedMessage)
                .build();
    }

    @DELETE
    @Path("/{messageId}")
    public Response deleteMessage(@PathParam("messageId") long id) {
        Message deletedMessage = messageService.removeMessage(id);
        if (deletedMessage != null) {
            return Response.ok()
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

//    @GET
//    @Path("/{messageId}/comments")
//    public CommentResource getCommentResource(@PathParam("messageId") long messageId) {
//        return new CommentResource();
//    }
}
