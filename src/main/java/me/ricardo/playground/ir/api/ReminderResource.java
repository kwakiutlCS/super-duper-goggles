package me.ricardo.playground.ir.api;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
    public List<ReminderDto> getReminders(@NotNull @HeaderParam("user") String user) {
        return service.getReminders(user)
        		      .stream()
        		      .map(ReminderAdapter::fromService)
        		      .collect(Collectors.toList());
    }
    
    @GET
    @Path("/{id}")
    public ReminderDto getReminder(@NotNull @HeaderParam("user") String user, @PathParam("id") long id) {
    	return ReminderAdapter.fromService(service.getReminder(id, user));
    }
    
    @POST
    @Transactional
    public Response createReminder(@NotNull @HeaderParam("user") String user, @Valid ReminderDto reminder) {
    	reminder.setUser(user);
    	ReminderDto result = ReminderAdapter.fromService(service.createReminder(ReminderAdapter.toService(reminder)));
    	
    	return Response.created(UriBuilder.fromResource(ReminderResource.class).path("/{id}").build(result.getId()))
    			       .entity(result)
    			       .build();
    }

    @PUT
    @Transactional
    @Path("/{id}")
	public ReminderDto updateReminder(@NotNull @HeaderParam("user") String user, @PathParam("id") long id, @Valid ReminderDto reminder) {
    	reminder.setUser(user);
		return ReminderAdapter.fromService(service.updateReminder(id, user, ReminderAdapter.toService(reminder)));
	}

    @DELETE
    @Transactional
    @Path("/{id}")
	public Response deleteReminder(@NotNull @HeaderParam("user") String user, @PathParam("id") long id) {
		service.deleteReminder(id, user);
		return Response.noContent().build();
	}

    @GET
    @Path("/{id}/schedule")
	public List<Long> getSchedule(@NotNull @HeaderParam("user") String user,
			                      @PathParam("id") long id,
			                      @NotNull @Min(0) @QueryParam("start") Long start,
			                      @Min(1) @QueryParam("end") Long end,
			                      @Min(1) @QueryParam("limit") Long limit) {
    	if ((end == null && limit == null) || (end != null && end <= start)) {
    		throw new ConstraintViolationException(Set.of());
    	}
    
    	List<Long> interval = end == null ? List.of(start) : List.of(start, end);
    
    	return service.getSchedule(id, user, interval, limit);
	}
}