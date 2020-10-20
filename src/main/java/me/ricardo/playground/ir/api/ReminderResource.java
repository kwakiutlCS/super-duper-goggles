package me.ricardo.playground.ir.api;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.service.ReminderService;

@Path("/reminders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ReminderResource {

	private ReminderService service;
	
	public ReminderResource(ReminderService service) {
		this.service = service;
	}
	
    @GET
    public List<Reminder> getReminders() {
        return service.getReminders();
    }
    
    @GET
    @Path("/{id}")
    public Reminder getReminder(@PathParam("id") long id) {
    	return service.getReminder(id);
    }
    
    @POST
    @Transactional
    public Response createReminder(Reminder reminder) {
    	Reminder result = service.createReminder(reminder);
    	
    	return Response.created(UriBuilder.fromResource(ReminderResource.class).path("/{id}").build(result.getId()))
    			       .entity(result)
    			       .build();
    }

    @PUT
    @Transactional
    @Path("/{id}")
	public Reminder updateReminder(@PathParam("id") long id, Reminder reminder) {
		return service.updateReminder(id, reminder);
	}

    @DELETE
    @Transactional
    @Path("/{id}")
	public Response deleteReminder(@PathParam("id") long id) {
		service.deleteReminder(id);
		return Response.noContent().build();
	}
}