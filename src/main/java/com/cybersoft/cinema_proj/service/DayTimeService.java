package com.cybersoft.cinema_proj.service;

import com.cybersoft.cinema_proj.dto.DayTimeDTO;
import com.cybersoft.cinema_proj.entity.DayTimeEntity;
import com.cybersoft.cinema_proj.repository.DayTimeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DayTimeService {

    private final DayTimeRepository dayTimeRepository;

    @Autowired
    public DayTimeService(DayTimeRepository dayTimeRepository) {
        this.dayTimeRepository = dayTimeRepository;
    }

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
}
