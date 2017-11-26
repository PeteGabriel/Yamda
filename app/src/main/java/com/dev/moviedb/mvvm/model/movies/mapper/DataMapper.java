package com.dev.moviedb.mvvm.model.movies.mapper;

import android.support.annotation.NonNull;

import com.dev.moviedb.mvvm.model.movies.AdvancedFact;
import com.dev.moviedb.mvvm.model.movies.Genres;
import com.dev.moviedb.mvvm.model.movies.Movie;
import com.dev.moviedb.mvvm.model.movies.MovieAggregator;
import com.dev.moviedb.mvvm.model.movies.MovieImages;
import com.dev.moviedb.mvvm.model.movies.MovieTrailer;
import com.dev.moviedb.mvvm.model.movies.Popularity;
import com.dev.moviedb.mvvm.model.movies.PrimaryFact;
import com.dev.moviedb.mvvm.model.movies.dto.MovieCollectionDto;
import com.dev.moviedb.mvvm.model.movies.dto.MovieDTO;

/**
 *
 *
 * Yamda 1.0.0
 */

public class DataMapper {

    /**
     * Converts the given {@link MovieCollectionDto} instance to the corresponding {@link MovieAggregator}
     * instance.
     * @param dto The {@link MovieCollectionDto} instance to be converted.
     * @return The resulting {@link MovieAggregator} instance.
     */
    @NonNull
    public MovieAggregator convertFrom(@NonNull MovieCollectionDto dto) {
        return new MovieAggregator.AggregatorBuilder().setPage(dto.getPage())
                .setResults(dto.getResults())
                .setTotalPages(dto.getTotalPages())
                .setTotalResults(dto.getTotalResults())
                .build();
    }

    /**
     * Converts the given {@link MovieDTO} instance to the corresponding {@link Movie}
     * instance.
     * @param dto The {@link MovieDTO} instance to be converted.
     * @return The resulting {@link Movie} instance.
     */
    @NonNull
    public Movie convertFrom(@NonNull MovieDTO dto) {

        PrimaryFact facts = new PrimaryFact(dto.getOverview(),
                dto.getTitle(), dto.getTitle(), (long) dto.getId(), dto.getReleaseDate());

        Genres genres = new Genres(dto.getGenres());
        AdvancedFact aFacts = null;
        aFacts = new AdvancedFact(dto.getImdb(), dto.getRuntime(), dto.getTagline(),
                    facts.getReleaseDate(), dto.getHomepage(), genres);

        //this should have been different however we did enough just to provide one trailer.
        MovieTrailer mt = null;
        if(dto.getTrailers() != null && dto.getTrailers().getYoutube().size() > 0) {
            mt = new MovieTrailer();
            mt.addTrailer(dto.getTrailers().getYoutube().get(0).getSource());
        }

        Movie tmp = new Movie(facts);
        tmp.setPopularity(new Popularity(dto.getVoteAverage(), dto.getPopularity(), dto.getVoteCount()));
        tmp.setMovieImages(new MovieImages(dto.getImageBackdropPath(), dto.getPosterPath()));
        tmp.setAdvancedFacts(aFacts);
        tmp.setMovieTrailers(mt);
        return tmp;
    }


}