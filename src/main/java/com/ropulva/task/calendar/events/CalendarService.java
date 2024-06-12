package com.ropulva.task.calendar.events;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.ropulva.task.calendar.configurations.GoogleCalendarConfig;

import jakarta.transaction.Transactional;

import com.google.api.services.calendar.model.EventDateTime;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {
	@Autowired
    private EventRepository eventRepository;

//    @Cacheable("events")
//    public List<Events> getAllEvents() {
//        return eventRepository.findAll();
//    }
//    @Cacheable("events")
//    public Optional<Events> getEventById(String eventId) {
//        return eventRepository.findById(eventId);
//    }

//    @CacheEvict(value = "events", allEntries = true)
    public com.ropulva.task.calendar.events.Events addEventDB(com.ropulva.task.calendar.events.Events event) {
    	boolean exists = eventRepository.existsById(event.getId());
    	if(exists) {
    		throw new IllegalStateException("Error, This event already exists.");
    	}
    	if(event.getStartTime() == null) {
    		throw new IllegalStateException("Error, This event has no start-time.");
    	}
        return eventRepository.save(event);
    }

//    @CacheEvict(value = "events", allEntries = true)
    public void deleteEventDB(String eventId) {
    	boolean exists = eventRepository.existsById(eventId);
    	if(!exists) {
    		throw new IllegalStateException("Error, Event with id " + eventId + " does not exist.");
    	}
        eventRepository.deleteById(eventId);
    }

//    @CacheEvict(value = "events", allEntries = true)
    @Transactional
    public void updateEventDB(String eventId, String summary, String description, String location, LocalDateTime startTime, LocalDateTime endTime) {
    	com.ropulva.task.calendar.events.Events event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalStateException("Event with id " + eventId + " does not exist."));
    	
    	boolean updated = false;
    	
    	if(summary != null && summary.length() > 0) {
    		event.setSummary(summary);
    		updated = true;
    	}
    	if(description != null && description.length() > 0) {
    		event.setDescription(description);
    		updated = true;
    	}
    	if(location != null && location.length() > 0) {
    		event.setLocation(location);
    		updated = true;
    	}
    	if(startTime != null && startTime.getYear() > 0 && endTime != null && endTime.getYear() > 0 && endTime.isAfter(startTime)) {
    		event.setStartTime(startTime);
    		event.setEndTime(endTime);
    		updated = true;
    	}
    	else if((startTime != null && endTime == null) || (startTime == null && endTime != null)) {
    		throw new IllegalStateException("Error, You have to enter both start-time and end-time!!!");
    	}
    	
    	if (updated) {
            eventRepository.save(event);
        }
    }
	
    public String addEvent(String summary, String location, String description, String startDateTime, String endDateTime) throws Exception {
        Calendar service = GoogleCalendarConfig.getCalendarService();

        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(description);
        
        DateTime startDate = new DateTime(startDateTime);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                .setTimeZone("Egypt");
        event.setStart(start);

        DateTime endDate = new DateTime(endDateTime);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDate)
                .setTimeZone("Egypt");
        event.setEnd(end);

        Event createdEvent = service.events().insert("primary", event).execute();
        
        // create Event in database
        LocalDateTime sDate = LocalDateTime.parse(startDateTime); 
        LocalDateTime eDate = LocalDateTime.parse(endDateTime); 
        com.ropulva.task.calendar.events.Events eventDB = new com.ropulva.task.calendar.events.Events(createdEvent.getId(), summary, description, location, sDate, eDate, "primary");
        
        addEventDB(eventDB);

        return "Event created: " + createdEvent.getHtmlLink();
    }
    
    public String deleteEvent(String eventId) throws Exception {
        Calendar service = GoogleCalendarConfig.getCalendarService();
        service.events().delete("primary", eventId).execute();
        // delete from database
        deleteEventDB(eventId);
        return "Event deleted";
    }
    @Transactional
    public String editEvent(String eventId, String summary, String location, String description, String startDateTime, String endDateTime) throws Exception {
        Calendar service = GoogleCalendarConfig.getCalendarService();
        Event event = service.events().get("primary", eventId).execute();
        if(summary != null && summary.length() > 0) event.setSummary(summary);
        if(location != null && location.length() > 0) event.setLocation(location);
        if(description != null && description.length() > 0) event.setDescription(description);

        if(startDateTime != null) {
        	DateTime start = new DateTime(startDateTime);
	        EventDateTime startEventDateTime = new EventDateTime()
	                .setDateTime(start)
	                .setTimeZone("Egypt");
	        event.setStart(startEventDateTime);
        }
        
        if(endDateTime != null) {
        	DateTime end = new DateTime(endDateTime);
	        EventDateTime endEventDateTime = new EventDateTime()
	                .setDateTime(end)
	                .setTimeZone("Egypt");
	        event.setEnd(endEventDateTime);
        }

        Event updatedEvent = service.events().update("primary", eventId, event).execute();
        
        // update event in database
        LocalDateTime sDate = startDateTime != null ? LocalDateTime.parse(startDateTime) : null; 
        LocalDateTime eDate = endDateTime != null ? LocalDateTime.parse(endDateTime): null;
        updateEventDB(eventId, summary, description, location, sDate, eDate);

        return "Event updated: " + updatedEvent.getHtmlLink();
    }

    public List<Event> getEvents() throws Exception {
        Calendar service = GoogleCalendarConfig.getCalendarService();
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        return events.getItems();
    }
}
