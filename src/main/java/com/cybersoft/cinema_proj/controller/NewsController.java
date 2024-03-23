package com.cybersoft.cinema_proj.controller;

import com.cybersoft.cinema_proj.dto.NewsDTO;
import com.cybersoft.cinema_proj.request.NewsRequest;
import com.cybersoft.cinema_proj.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/all")
    public ResponseEntity<?> getNews(){
        List<NewsDTO> listNew = newsService.getAllNew();
        return new ResponseEntity<>(listNew, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMovie(@ModelAttribute NewsRequest newsRequest) {
        if (newsRequest.getImage() == null) {
            return ResponseEntity.badRequest().body("Image is required");
        }
        NewsDTO newsDTO = newsService.convertToNewsRequestToDTO(newsRequest);
        NewsDTO saved = newsService.addNews(newsDTO);

        if (saved == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("News already exists");
        }
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateMember(@PathVariable int id, @ModelAttribute NewsRequest newsRequest) {
        NewsDTO existingNews = newsService.getNewsById(id);
        if (existingNews == null) {
            return new ResponseEntity<>("News not found", HttpStatus.NOT_FOUND);
        }
        NewsDTO newsDTO = newsService.convertToNewsRequestToDTO(newsRequest);
        newsDTO.setId(id);
        NewsDTO updateNews = newsService.updateNews(newsDTO);
        if (updateNews != null) {
            return new ResponseEntity<>("News updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to update news", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getNewsById(@PathVariable("id") int id) {
        NewsDTO newsDTO = newsService.getNewsById(id);
        if (newsDTO != null) {
            return new ResponseEntity<>(newsDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("News not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable int id) {
        newsService.deleteNews(id);
        return new  ResponseEntity<>("Delete news", HttpStatus.OK);
    }

    @GetMapping("/images")
    public List<String> getAllImages() {
        return newsService.getAllImages();
    }
}
