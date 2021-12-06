package com.jackkim.moviecatalogservice.resources;

import com.jackkim.moviecatalogservice.models.CatalogItem;
import com.jackkim.moviecatalogservice.models.Movie;
import com.jackkim.moviecatalogservice.models.Rating;
import com.jackkim.moviecatalogservice.models.UserRating;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        //RestTemplate restTemplate = new RestTemplate();

        // get all rated movie IDs;
        UserRating ratings = restTemplate.getForObject("http://localhost:8083/ratingsdata/users/" + userId, UserRating.class);

       return ratings.getUserRating().stream().map(rating -> {

           // for each movie ID, call movie info service and get details
           Movie myMovie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);

           // put them all together
           return new CatalogItem(myMovie.getName(), "Description", rating.getRating());

       }).collect(Collectors.toList());

    }
}
