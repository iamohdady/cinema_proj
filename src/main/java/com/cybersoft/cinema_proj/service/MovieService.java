package com.cybersoft.cinema_proj.service;


import com.cybersoft.cinema_proj.dto.MovieDTO;
import com.cybersoft.cinema_proj.entity.*;

import com.cybersoft.cinema_proj.repository.MovieRepository;
import com.cybersoft.cinema_proj.request.AddMovieRequest;
import com.cybersoft.cinema_proj.request.UpdateMovieRequest;
import com.cybersoft.cinema_proj.specification.MovieSpecifications;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public List<MovieDTO> getAllMovie(){
        List<MovieEntity> listMovie = movieRepository.findAll();
        List<MovieDTO> listMovieDTO = new ArrayList<>();
        for (MovieEntity data : listMovie){
            MovieDTO movieDTO = new MovieDTO();
            BeanUtils.copyProperties(data, movieDTO);
            listMovieDTO.add(movieDTO);
        }
        return listMovieDTO;
    }

    public List<MovieDTO> getNowShowingMovies() {
        Date today = new Date();
        List<MovieEntity> listMovie = movieRepository.findMoviesByStartDateBefore(today);
        List<MovieDTO> listMovieDTO = new ArrayList<>();
        for (MovieEntity data : listMovie){
            MovieDTO movieDTO = new MovieDTO();
            BeanUtils.copyProperties(data, movieDTO);
            listMovieDTO.add(movieDTO);
        }
        return listMovieDTO;
    }

    public List<MovieDTO> getUpcomingMovies() {
        Date today = new Date();
        List<MovieEntity> listMovie = movieRepository.findMoviesByStartDateAfter(today);
        List<MovieDTO> listMovieDTO = new ArrayList<>();
        for (MovieEntity data : listMovie){
            MovieDTO movieDTO = new MovieDTO();
            BeanUtils.copyProperties(data, movieDTO);
            listMovieDTO.add(movieDTO);
        }
        return listMovieDTO;
    }

    public MovieDTO getMoviesByName(String name) {
        MovieEntity movie = movieRepository.findByName(name);
        if (movie != null) {
            MovieDTO movieDTO = new MovieDTO();
            BeanUtils.copyProperties(movie, movieDTO);
            return movieDTO;
        }
        return null;
    }

    public List<MovieDTO> getMoviesByGenre(String genre) {
        List<MovieEntity> movies = movieRepository.findAll(MovieSpecifications.hasGenre(genre));
        List<MovieDTO> movieDTOs = new ArrayList<>();
        for (MovieEntity movie : movies) {
            MovieDTO movieDTO = new MovieDTO();
            BeanUtils.copyProperties(movie, movieDTO);
            movieDTOs.add(movieDTO);
        }
        return movieDTOs;
    }

    public MovieDTO getMovieById(int id) {
        MovieEntity movieEntity = movieRepository.findById(id);
        if (movieEntity != null) {
            MovieDTO movieDTO = new MovieDTO();
            BeanUtils.copyProperties(movieEntity, movieDTO);
            return movieDTO;
        } else {
            return null;
        }
    }

    public MovieDTO getDetailById(int id) {
        MovieEntity movie = movieRepository.findById(id);
        if (movie != null) {
            MovieDTO movieDTO = new MovieDTO();
            BeanUtils.copyProperties(movie, movieDTO);
            return movieDTO;
        }
        return null;
    }

    public MovieDTO getTrailerById(int id) {
        MovieEntity movieEntity = movieRepository.findById(id);
        if (movieEntity != null) {
            MovieDTO movieDTO = new MovieDTO();
            movieDTO.setTrailer(movieEntity.getTrailer());
            return movieDTO;
        } else {
            return null;
        }
    }

    public List<MovieDTO> searchMoviesByKeyword(String keyword) {
        List<MovieEntity> movies = movieRepository.findByTenContainingKeyword(keyword);
        List<MovieDTO> movieDTOs = new ArrayList<>();
        for (MovieEntity movie : movies) {
            MovieDTO movieDTO = new MovieDTO();
            BeanUtils.copyProperties(movie, movieDTO);
            movieDTOs.add(movieDTO);
        }
        return movieDTOs;
    }

    public MovieDTO addMovie(MovieDTO movieDTO) {
        if (movieDTO == null) {
            return null;
        }
        MovieEntity existingMovie = movieRepository.findByName(movieDTO.getName());
        if (existingMovie != null) {
            return null;
        }
        MovieEntity movieEntity = new MovieEntity();
        BeanUtils.copyProperties(movieDTO, movieEntity);

        MovieEntity savedMovieEntity = movieRepository.save(movieEntity);
        MovieDTO savedMovieDTO = new MovieDTO();
        BeanUtils.copyProperties(savedMovieEntity, savedMovieDTO);
        return savedMovieDTO;
    }

    public MovieDTO convertToAddMovieRequestToDTO(AddMovieRequest addMovieRequest) {
        MovieDTO movieDTO = new MovieDTO();

        movieDTO.setName(addMovieRequest.getName());

        MultipartFile imageFile = addMovieRequest.getImage();
        MultipartFile trailerFile = addMovieRequest.getTrailer();

        if (imageFile != null) {
            movieDTO.setImage(imageFile.getOriginalFilename());
        } else {
            movieDTO.setImage(null);
        }

        if (trailerFile != null) {
            movieDTO.setTrailer(trailerFile.getOriginalFilename());
        } else {
            movieDTO.setTrailer(null);
        }

        movieDTO.setStart_date(addMovieRequest.getStart_date());
        movieDTO.setDuration(addMovieRequest.getDuration());
        movieDTO.setRated(addMovieRequest.getRated());
        movieDTO.setCategory(addMovieRequest.getCategory());
        movieDTO.setDirector(addMovieRequest.getDirector());
        movieDTO.setActor(addMovieRequest.getActor());
        movieDTO.setPrice(addMovieRequest.getPrice());

        return movieDTO;
    }

    public MovieDTO updateMovie(MovieDTO movieDTO) {
        Optional<MovieEntity> optionalExistingMovie = Optional.ofNullable(movieRepository.findById(movieDTO.getId()));
        if (optionalExistingMovie.isPresent()) {
            MovieEntity existingMovie = optionalExistingMovie.get();
            BeanUtils.copyProperties(movieDTO, existingMovie, "id");
            MovieEntity updatedMovieEntity = movieRepository.save(existingMovie);
            MovieDTO updatedMovieDTO = new MovieDTO();
            BeanUtils.copyProperties(updatedMovieEntity, updatedMovieDTO);
            return updatedMovieDTO;
        } else {
            return null;
        }
    }

    public MovieDTO convertToUpdateMovieRequestToDTO(UpdateMovieRequest updateMovieRequest) {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setName(updateMovieRequest.getName());
        MultipartFile imageFile = updateMovieRequest.getImage();
        MultipartFile trailerFile = updateMovieRequest.getTrailer();

        if (imageFile != null) {
            movieDTO.setImage(imageFile.getOriginalFilename());
        } else {
            movieDTO.setImage(null);
        }
        if (trailerFile != null) {
            movieDTO.setTrailer(trailerFile.getOriginalFilename());
        } else {
            movieDTO.setTrailer(null);
        }
        movieDTO.setName(updateMovieRequest.getName());
        movieDTO.setStart_date(updateMovieRequest.getStart_date());
        movieDTO.setDuration(updateMovieRequest.getDuration());
        movieDTO.setCategory(updateMovieRequest.getCategory());
        movieDTO.setDirector(updateMovieRequest.getDirector());
        movieDTO.setActor(updateMovieRequest.getActor());
        movieDTO.setPrice(updateMovieRequest.getPrice());

        return movieDTO;
    }


    public boolean deleteMovie(int id) {
        Optional<MovieEntity> optionalMovieEntity = Optional.ofNullable(movieRepository.findById(id));
        if (optionalMovieEntity.isPresent()) {
            movieRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public long countAllMovie() {
        return movieRepository.countAllMovie();
    }


}