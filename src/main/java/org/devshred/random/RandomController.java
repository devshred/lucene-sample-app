package org.devshred.random;

import static java.util.stream.Collectors.toList;
import com.github.javafaker.Faker;
import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.devshred.search.SearchDocument;
import org.devshred.search.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/random")
@Slf4j
public class RandomController {
  private static final Faker FAKER = new Faker(Locale.GERMAN);

  private final SearchService searchService;

  public RandomController(SearchService searchService) {
    this.searchService = searchService;
  }

  @GetMapping("/single")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void singleUpdates(@RequestParam("n") Integer numberOfEntries) {
    final List<SearchDocument> documents = createListOfRandomStrings(numberOfEntries);

    final Stopwatch stopwatch = Stopwatch.createStarted();
    documents.forEach(searchService::addToIndex);
    log.info("singleUpdates. entries: {}, time: {}", numberOfEntries, stopwatch);
  }

  @GetMapping("/batch")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void batchUpdate(@RequestParam("n") Integer numberOfEntries) {
    final List<SearchDocument> documents = createListOfRandomStrings(numberOfEntries);

    final Stopwatch stopwatch = Stopwatch.createStarted();
    searchService.addToIndex(documents);
    log.info("batchUpdate. entries: {}, time: {}", numberOfEntries, stopwatch);
  }

  @GetMapping("/buffer")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void bufferedUpdate(@RequestParam("n") Integer numberOfEntries) {
    final List<SearchDocument> documents = createListOfRandomStrings(numberOfEntries);

    final Stopwatch stopwatch = Stopwatch.createStarted();
    documents.forEach(searchService::bufferedAddToIndex);
    log.info("bufferedUpdate. entries: {}, time: {}", numberOfEntries, stopwatch);
  }

  private List<SearchDocument> createListOfRandomStrings(int numberOfEntries) {
    return IntStream.range(0, numberOfEntries)
        .mapToObj(
            $ ->
                new SearchDocument(
                    FAKER.internet().url(),
                    FAKER.backToTheFuture().quote(),
                    FAKER.chuckNorris().fact()))
        .collect(toList());
  }
}
