package com.cybersoft.cinema_proj.service;

import com.cybersoft.cinema_proj.dto.DayTimeDTO;
import com.cybersoft.cinema_proj.dto.MovieDTO;
import com.cybersoft.cinema_proj.dto.ScheduleDTO;
import com.cybersoft.cinema_proj.dto.ShowTimeDTO;
import com.cybersoft.cinema_proj.entity.DayTimeEntity;
import com.cybersoft.cinema_proj.entity.MovieEntity;
import com.cybersoft.cinema_proj.entity.ScheduleEntity;
import com.cybersoft.cinema_proj.entity.ShowTimeEntity;
import com.cybersoft.cinema_proj.repository.MovieRepository;
import com.cybersoft.cinema_proj.repository.ScheduleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MovieRepository movieRepository;

    public List<ScheduleDTO> getAll(){
        List<ScheduleEntity> listSchedule = scheduleRepository.findAll();
        List<ScheduleDTO> listScheduleDTO = new ArrayList<>();
        for (ScheduleEntity data : listSchedule){
            ScheduleDTO scheduleDTO = new ScheduleDTO();
            BeanUtils.copyProperties(data, scheduleDTO);
            listScheduleDTO.add(scheduleDTO);
        }
        return listScheduleDTO;
    }

    public List<ScheduleDTO> getScheduleDaytimeId(int daytimeId) {
        List<ScheduleEntity> scheduleEntities = scheduleRepository.findByDayTimeId(daytimeId);
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for (ScheduleEntity scheduleEntity : scheduleEntities) {
            ScheduleDTO scheduleDTO = new ScheduleDTO();
            BeanUtils.copyProperties(scheduleEntity, scheduleDTO);
            scheduleDTOS.add(scheduleDTO);
        }
        return scheduleDTOS;
    }

    public List<ScheduleDTO> getAllSchedule(){
        List<ScheduleEntity> listSchedule = scheduleRepository.findAll();
        List<ScheduleDTO> listScheduleDTO = new ArrayList<>();
        for (ScheduleEntity data : listSchedule){
            ScheduleDTO scheduleDTO = new ScheduleDTO();
            BeanUtils.copyProperties(data, scheduleDTO);
            listScheduleDTO.add(scheduleDTO);
        }
        return listScheduleDTO;
    }

    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        ScheduleEntity scheduleEntity = new ScheduleEntity();
        BeanUtils.copyProperties(scheduleDTO, scheduleEntity);

        scheduleEntity = scheduleRepository.save(scheduleEntity);
        ScheduleDTO createdScheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(scheduleEntity, createdScheduleDTO);
        return createdScheduleDTO;
    }

}