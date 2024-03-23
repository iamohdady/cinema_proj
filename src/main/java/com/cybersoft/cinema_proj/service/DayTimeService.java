package com.cybersoft.cinema_proj.service;

import com.cybersoft.cinema_proj.dto.*;
import com.cybersoft.cinema_proj.entity.*;
import com.cybersoft.cinema_proj.repository.DayTimeRepository;
import com.cybersoft.cinema_proj.repository.ShowTimeRepository;
import com.cybersoft.cinema_proj.response.DaytimeResponse;
import com.cybersoft.cinema_proj.response.ShowTimeReponse;
import jakarta.persistence.Tuple;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DayTimeService {

    @Autowired
    private DayTimeRepository dayTimeRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    public List<DayTimeDTO> getAllDayTimes() {
        List<DayTimeEntity> dayTimeEntities = dayTimeRepository.findAll();
        List<DayTimeDTO> dayTimeDTOs = new ArrayList<>();
        for (DayTimeEntity entity : dayTimeEntities) {
            DayTimeDTO dto = new DayTimeDTO();
            BeanUtils.copyProperties(entity, dto);
            dayTimeDTOs.add(dto);
        }
        return dayTimeDTOs;
    }

    public DayTimeDTO addDayTime(DayTimeDTO dayTimeDTO) {
        DayTimeEntity dayTimeEntity = new DayTimeEntity();
        BeanUtils.copyProperties(dayTimeDTO, dayTimeEntity);

        DayTimeEntity savedDayTimeEntity = dayTimeRepository.save(dayTimeEntity);
        DayTimeDTO savedDayTimeDTO = new DayTimeDTO();
        BeanUtils.copyProperties(savedDayTimeEntity, savedDayTimeDTO);
        return savedDayTimeDTO;
    }

    public DayTimeDTO updateDayTime(DayTimeDTO dayTimeDTO) {
        Optional<DayTimeEntity> optionalExistingDayTime = dayTimeRepository.findById(dayTimeDTO.getId());
        if (optionalExistingDayTime.isPresent()) {
            DayTimeEntity existingDayTime = optionalExistingDayTime.get();
            BeanUtils.copyProperties(dayTimeDTO, existingDayTime);

            DayTimeEntity updatedDayTimeEntity = dayTimeRepository.save(existingDayTime);
            DayTimeDTO updatedDayTimeDTO = new DayTimeDTO();
            BeanUtils.copyProperties(updatedDayTimeEntity, updatedDayTimeDTO);
            return updatedDayTimeDTO;
        } else {
            return null;
        }
    }

    public void deleteDayTime(int id) {
        dayTimeRepository.deleteById(id);
    }

    public DayTimeDTO getDayTimeById(int id) {
        Optional<DayTimeEntity> dayTimeEntity = dayTimeRepository.findById(id);
        if (dayTimeEntity != null) {
            DayTimeDTO dayTimeDTO = new DayTimeDTO();
            BeanUtils.copyProperties(dayTimeEntity, dayTimeDTO);
            return dayTimeDTO;
        } else {
            return null;
        }
    }

    public DayTimeDTO createDayTime(DayTimeDTO dayTimeDTO) {
        DayTimeEntity dayTimeEntity = new DayTimeEntity();
        BeanUtils.copyProperties(dayTimeDTO, dayTimeEntity);

        dayTimeEntity = dayTimeRepository.save(dayTimeEntity);
        DayTimeDTO createdDayTimeDTO = new DayTimeDTO();
        BeanUtils.copyProperties(dayTimeEntity, createdDayTimeDTO);
        return createdDayTimeDTO;
    }

    public List<DaytimeResponse> findRoomMovieScheduleShowTimeByDaytimeId(int daytimeId) {
        List<Object[]> results = dayTimeRepository.findRoomMovieScheduleShowTimeByDaytimeId(daytimeId);
        List<DaytimeResponse> responses = new ArrayList<>();
        for (Object[] result : results) {
            RoomEntity room = (RoomEntity) result[0];
            MovieEntity movie = (MovieEntity) result[1];
            ScheduleEntity schedule = (ScheduleEntity) result[2];
            ShowTimeEntity showTime = (ShowTimeEntity) result[3];

            DaytimeResponse response = new DaytimeResponse();
            if (room != null) {
                RoomDTO roomDTO = new RoomDTO();
                BeanUtils.copyProperties(room, roomDTO);
                response.setRoom(roomDTO);
            }
            if (movie != null) {
                MovieDTO movieDTO = new MovieDTO();
                BeanUtils.copyProperties(movie, movieDTO);
                response.setMovie(movieDTO);
            }
            if (schedule != null) {
                ScheduleDTO scheduleDTO = new ScheduleDTO();
                BeanUtils.copyProperties(schedule, scheduleDTO);
                response.setSchedule(scheduleDTO);
            }
            if (showTime != null) {
                ShowTimeDTO showTimeDTO = new ShowTimeDTO();
                BeanUtils.copyProperties(showTime, showTimeDTO);
                response.setShowTime(showTimeDTO);
            }
            responses.add(response);
        }
        return responses;
    }
}
