package com.cybersoft.cinema_proj.controller;

import com.cybersoft.cinema_proj.dto.MovieDTO;
import com.cybersoft.cinema_proj.request.AddMovieRequest;
import com.cybersoft.cinema_proj.request.MovieRequest;
import com.cybersoft.cinema_proj.request.UpdateMovieRequest;
import com.cybersoft.cinema_proj.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/all")
    public ResponseEntity<?> getMovie(){
        List<MovieDTO> listMovie = movieService.getAllMovie();
        return new ResponseEntity<>(listMovie, HttpStatus.OK);
    }

    @GetMapping("/now-showing")
    public ResponseEntity<?> getNowShowingMovies() {
        List<MovieDTO> nowShowingMovies = movieService.getNowShowingMovies();
        return new ResponseEntity<>(nowShowingMovies, HttpStatus.OK);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingMovies() {
        List<MovieDTO> upcomingMovies = movieService.getUpcomingMovies();
        return new ResponseEntity<>(upcomingMovies, HttpStatus.OK);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<?> getMovieByName(@PathVariable String name) {
        MovieDTO movieDTO = movieService.getMoviesByName(name);
        if (movieDTO != null) {
            return new ResponseEntity<>(movieDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Movie not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/genre/{genre}")
    public ResponseEntity<?> getMoviesByGenre(@PathVariable("genre") String genre) {
        List<MovieDTO> listMovies = movieService.getMoviesByGenre(genre);
        return new ResponseEntity<>(listMovies, HttpStatus.OK);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable("id") int id) {
        MovieDTO movieDTO = movieService.getMovieById(id);
        if (movieDTO != null) {
            return new ResponseEntity<>(movieDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Movie not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getDetailById(@PathVariable("id") int id) {
        MovieDTO movieDTO = movieService.getDetailById(id);
        if (movieDTO != null) {
            return new ResponseEntity<>(movieDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Movie and director not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/details/trailer/{id}")
    public ResponseEntity<?> getTrailerById(@PathVariable("id") int id) {
        MovieDTO movieDTO = movieService.getTrailerById(id);
        if (movieDTO != null) {
            return new ResponseEntity<>(movieDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Movie and director not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/search/key")
    public ResponseEntity<?> searchMoviesByKeyword(@RequestBody MovieRequest request) {
        String keyword = request.name;
        List<MovieDTO> movies = movieService.searchMoviesByKeyword(keyword);
        if (movies != null) {
            return new ResponseEntity<>(movies, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Movie and director not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMovie(@ModelAttribute AddMovieRequest addMovieRequest) {
        if (addMovieRequest.getImage() == null) {
            return ResponseEntity.badRequest().body("Image is required");
        }
        if (addMovieRequest.getTrailer() == null) {
            return ResponseEntity.badRequest().body("Trailer is required");
        }

        MovieDTO movieDTO = movieService.convertToAddMovieRequestToDTO(addMovieRequest);
        MovieDTO savedMovieDTO = movieService.addMovie(movieDTO);

        if (savedMovieDTO == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Movie already exists");
        }

        return ResponseEntity.ok(savedMovieDTO);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable int id, @ModelAttribute UpdateMovieRequest updateMovieRequest) {
        MovieDTO existingMovieDTO = movieService.getMovieById(id);
        if (existingMovieDTO == null) {
            return new ResponseEntity<>("Movie not found", HttpStatus.NOT_FOUND);
        }
        if (updateMovieRequest.getImage() == null || updateMovieRequest.getImage().isEmpty()) {
            return ResponseEntity.badRequest().body("Image is required");
        }
        if (updateMovieRequest.getTrailer() == null || updateMovieRequest.getTrailer().isEmpty()) {
            return ResponseEntity.badRequest().body("Trailer is required");
        }

        MovieDTO movieDTO = movieService.convertToUpdateMovieRequestToDTO(updateMovieRequest);
        movieDTO.setId(id);

        MovieDTO updatedMovieResult = movieService.updateMovie(movieDTO);
        if (updatedMovieResult != null) {
            return new ResponseEntity<>(updatedMovieResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to update movie", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable int id) {
        boolean isDeleted = movieService.deleteMovie(id);
        if (isDeleted) {
            return new ResponseEntity<>(isDeleted, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Movie not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countAllMovie() {
        long count = movieService.countAllMovie();
        return ResponseEntity.ok(count);
    }
}
