package com.cybersoft.cinema_proj.service;

import com.cybersoft.cinema_proj.dto.DayTimeDTO;
import com.cybersoft.cinema_proj.dto.MemberDTO;
import com.cybersoft.cinema_proj.dto.MovieDTO;
import com.cybersoft.cinema_proj.dto.NewsDTO;
import com.cybersoft.cinema_proj.entity.DayTimeEntity;
import com.cybersoft.cinema_proj.entity.MovieEntity;
import com.cybersoft.cinema_proj.entity.NewsEntity;
import com.cybersoft.cinema_proj.repository.NewsRepository;
import com.cybersoft.cinema_proj.request.NewsRequest;
import com.cybersoft.cinema_proj.request.UpdateMemberRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    public List<NewsDTO> getAllNew(){
        List<NewsEntity> listNew = newsRepository.findAll();
        List<NewsDTO> listNewDTO = new ArrayList<>();
        for (NewsEntity data : listNew){
            NewsDTO newsDTO = new NewsDTO();
            BeanUtils.copyProperties(data, newsDTO);
            listNewDTO.add(newsDTO);
        }
        return listNewDTO;
    }

    public NewsDTO addNews(NewsDTO newsDTO) {
        NewsEntity newsEntity = new NewsEntity();
        BeanUtils.copyProperties(newsDTO, newsEntity);

        NewsEntity saveNews = newsRepository.save(newsEntity);
        NewsDTO saveNewsDTO = new NewsDTO();
        BeanUtils.copyProperties(saveNews, saveNewsDTO);
        return saveNewsDTO;
    }

    public NewsDTO updateNews(NewsDTO newsDTO) {
        Optional<NewsEntity> optionalExistingDNews = Optional.ofNullable(newsRepository.findById(newsDTO.getId()));
        if (optionalExistingDNews.isPresent()) {
            NewsEntity existingNews = optionalExistingDNews.get();
            BeanUtils.copyProperties(newsDTO, existingNews);

            NewsEntity updatedNews = newsRepository.save(existingNews);
            NewsDTO updatedNewsDTO = new NewsDTO();
            BeanUtils.copyProperties(updatedNews, updatedNewsDTO);
            return updatedNewsDTO;
        } else {
            return null;
        }
    }

    public NewsDTO convertToNewsRequestToDTO(NewsRequest newsRequest) {
        NewsDTO newsDTO = new NewsDTO();

        newsDTO.setName(newsRequest.getName());

        MultipartFile imageFile = newsRequest.getImage();

        if (imageFile != null) {
            newsDTO.setImage(imageFile.getOriginalFilename());
        } else {
            newsDTO.setImage(null);
        }
        newsDTO.setDescription(newsRequest.getDescription());
        newsDTO.setShort_desc(newsRequest.getShort_desc());
        return newsDTO;
    }

    public NewsDTO getNewsById(int id) {
        NewsEntity news = newsRepository.findById(id);
        if (news != null) {
            NewsDTO newsDTO = new NewsDTO();
            BeanUtils.copyProperties(news, newsDTO);
            return newsDTO;
        } else {
            return null;
        }
    }

    public void deleteNews(int id) {
        newsRepository.deleteById(id);
    }

    public List<String> getAllImages() {
        return newsRepository.findAllImages();
    }
}
