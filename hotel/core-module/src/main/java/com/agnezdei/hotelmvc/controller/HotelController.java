package com.agnezdei.hotelmvc.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agnezdei.hotelmvc.dto.GuestDTO;
import com.agnezdei.hotelmvc.dto.GuestServiceDTO;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.mapper.GuestMapper;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.model.ServiceCategory;

@RestController
@RequestMapping("/api")
public class HotelController {

    @Autowired
    private HotelAdmin hotelAdmin;

    @PostMapping("/rooms")
    public ResponseEntity<String> addRoom(@RequestParam String number,
                                          @RequestParam RoomType type,
                                          @RequestParam double price,
                                          @RequestParam int capacity,
                                          @RequestParam int stars) throws BusinessLogicException {
        String result = hotelAdmin.addRoom(number, type, price, capacity, stars);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/rooms/{roomNumber}/price")
    public ResponseEntity<String> changeRoomPrice(@PathVariable String roomNumber,
                                                  @RequestParam double newPrice) throws EntityNotFoundException, BusinessLogicException {
        String result = hotelAdmin.changeRoomPrice(roomNumber, newPrice);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/rooms/{roomNumber}/maintenance")
    public ResponseEntity<String> setRoomUnderMaintenance(@PathVariable String roomNumber) throws EntityNotFoundException, BusinessLogicException {
        String result = hotelAdmin.setRoomUnderMaintenance(roomNumber);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/rooms/{roomNumber}/available")
    public ResponseEntity<String> setRoomAvailable(@PathVariable String roomNumber) throws EntityNotFoundException, BusinessLogicException {
        String result = hotelAdmin.setRoomAvailable(roomNumber);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/guests/settle")
    public ResponseEntity<String> settleGuest(@RequestParam String roomNumber,
                                              @RequestBody GuestDTO guestDto,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) throws EntityNotFoundException, BusinessLogicException {
        Guest guest = GuestMapper.toEntity(guestDto);
        String result = hotelAdmin.settleGuest(roomNumber, guest, checkInDate, checkOutDate);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/guests/{roomNumber}/evict")
    public ResponseEntity<String> evictGuest(@PathVariable String roomNumber) throws EntityNotFoundException, BusinessLogicException {
        String result = hotelAdmin.evictGuest(roomNumber);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/services")
    public ResponseEntity<String> addService(@RequestParam String name,
                                             @RequestParam double price,
                                             @RequestParam ServiceCategory category) throws BusinessLogicException {
        String result = hotelAdmin.addService(name, price, category);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/services/{serviceName}/price")
    public ResponseEntity<String> changeServicePrice(@PathVariable String serviceName,
                                                     @RequestParam double newPrice) throws EntityNotFoundException, BusinessLogicException {
        String result = hotelAdmin.changeServicePrice(serviceName, newPrice);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/guests/services")
    public ResponseEntity<String> addServiceToGuest(@RequestParam String guestPassport,
                                                    @RequestParam String serviceName,
                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate serviceDate) throws EntityNotFoundException, BusinessLogicException {
        String result = hotelAdmin.addServiceToGuest(guestPassport, serviceName, serviceDate);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/guests/services/{guestServiceId}")
    public ResponseEntity<String> removeServiceFromGuest(@PathVariable Long guestServiceId) throws EntityNotFoundException, BusinessLogicException {
        String result = hotelAdmin.removeServiceFromGuest(guestServiceId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/guests/{guestId}/services")
    public ResponseEntity<List<GuestServiceDTO>> getGuestServices(@PathVariable Long guestId) throws BusinessLogicException {
        List<GuestServiceDTO> services = hotelAdmin.getGuestServices(guestId);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/guests/by-name/{guestName}/services")
    public ResponseEntity<List<GuestServiceDTO>> getGuestServicesByName(@PathVariable String guestName) throws BusinessLogicException {
        List<GuestServiceDTO> services = hotelAdmin.getGuestServicesByName(guestName);
        return ResponseEntity.ok(services);
    }

    @PostMapping("/export/rooms")
    public ResponseEntity<String> exportRooms(@RequestParam String filePath) {
        String result = hotelAdmin.exportRoomsToCsv(filePath);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/export/guests")
    public ResponseEntity<String> exportGuests(@RequestParam String filePath) {
        String result = hotelAdmin.exportGuestsToCsv(filePath);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/export/services")
    public ResponseEntity<String> exportServices(@RequestParam String filePath) {
        String result = hotelAdmin.exportServicesToCsv(filePath);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/export/bookings")
    public ResponseEntity<String> exportBookings(@RequestParam String filePath) {
        String result = hotelAdmin.exportBookingsToCsv(filePath);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/export/guest-services")
    public ResponseEntity<String> exportGuestServices(@RequestParam String filePath) {
        String result = hotelAdmin.exportGuestServicesToCsv(filePath);
        return ResponseEntity.ok(result);
    }
}