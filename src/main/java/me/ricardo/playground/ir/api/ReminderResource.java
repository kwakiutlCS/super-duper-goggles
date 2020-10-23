package me.ricardo.playground.ir.api;

import java.util.List;
import java.util.stream.Collectors;

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

import me.ricardo.playground.ir.api.adapter.ReminderAdapter;
import me.ricardo.playground.ir.api.entity.ReminderDto;
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
    public List<ReminderDto> getReminders() {
        return service.getReminders()
        		      .stream()
        		      .map(ReminderAdapter::fromService)
        		      .collect(Collectors.toList());
    }
    
    @GET
    @Path("/{id}")
    public ReminderDto getReminder(@PathParam("id") long id) {
    	return ReminderAdapter.fromService(service.getReminder(id));
    }
    
    @POST
    @Transactional
    public Response createReminder(ReminderDto reminder) {
    	ReminderDto result = ReminderAdapter.fromService(service.createReminder(ReminderAdapter.toService(reminder)));
    	
    	return Response.created(UriBuilder.fromResource(ReminderResource.class).path("/{id}").build(result.getId()))
    			       .entity(result)
    			       .build();
    }

    @PUT
    @Transactional
    @Path("/{id}")
	public ReminderDto updateReminder(@PathParam("id") long id, ReminderDto reminder) {
		return ReminderAdapter.fromService(service.updateReminder(id, ReminderAdapter.toService(reminder)));
	}

    @DELETE
    @Transactional
    @Path("/{id}")
	public Response deleteReminder(@PathParam("id") long id) {
		service.deleteReminder(id);
		return Response.noContent().build();
	}
}