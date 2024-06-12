package com.ropulva.task.calendar.events;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.services.calendar.model.Event;

@RestController
public class CalendarController {
    @Autowired
    private CalendarService calendarService;

    @GetMapping("/createEvent")
    //summary, String location, String Description, DateTime startDateTime, DateTime endDateTime
    public String createEvent(
    		@RequestParam String summary,
    		@RequestParam String location,
    		@RequestParam String description,
    		@RequestParam String startDate,
    		@RequestParam String endDate) {
        try {
            return calendarService.addEvent(summary, location, description, startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating event: " + e.getMessage();
        }
    }
    
    @GetMapping("/deleteEvent/{eventId}")
    public String deleteEvent(@PathVariable String eventId) {
        try {
            return calendarService.deleteEvent(eventId);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error deleting event: " + e.getMessage();
        }
    }

    @GetMapping("/editEvent/{eventId}")
    public String editEvent(@PathVariable String eventId,
                            @RequestParam(required = false) String summary,
                            @RequestParam(required = false) String location,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) String startDateTime,
                            @RequestParam(required = false) String endDateTime) {
        try {
            return calendarService.editEvent(eventId, summary, location, description, startDateTime, endDateTime);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error editing event: " + e.getMessage();
        }
    }

    @GetMapping("/getEvents")
    public List<Event> getEvents() {
        try {
            return calendarService.getEvents();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
