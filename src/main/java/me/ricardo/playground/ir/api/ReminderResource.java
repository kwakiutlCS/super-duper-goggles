package me.ricardo.playground.ir.api;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import me.ricardo.playground.ir.domain.entities.Reminder;
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
    public Optional<Reminder> getReminder(@PathParam("id") long id) {
    	return service.getReminder(id);
    }
    
    @POST
    @Transactional
    public Response createReminder(Reminder reminder) throws URISyntaxException {
    	Reminder result = service.createReminder(reminder);
    	
    	return Response.created(UriBuilder.fromResource(ReminderResource.class).path("/{id}").build(result.getId()))
    			       .entity(result)
    			       .build();
    }
}