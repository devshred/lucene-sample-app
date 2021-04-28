package org.devshred.fetch;

import java.util.List;
import one.util.streamex.StreamEx;
import org.devshred.search.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FetchController {
  private final FetchService fetchService;
  private final SearchService searchService;

  public FetchController(FetchService fetchService, SearchService searchService) {
    this.fetchService = fetchService;
    this.searchService = searchService;
  }

  @GetMapping("/fetch")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void fetch(@RequestParam String feedUrl) {
    final List<FeedEntry> entries = fetchService.fetch(feedUrl);
    searchService.addToIndex(StreamEx.of(entries).map(FeedEntry::toSearchDocument).toList());
  }
}
