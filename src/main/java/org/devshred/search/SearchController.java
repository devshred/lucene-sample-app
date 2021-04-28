package org.devshred.search;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.apache.lucene.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {
  private final SearchService searchService;

  public SearchController(SearchService searchService) {
    this.searchService = searchService;
  }

  @PostMapping("/")
  public List<SearchDocument> search(@RequestBody SearchQuery searchQuery) {
    final List<Document> documents = searchService.search(searchQuery);
    return StreamEx.of(documents).map(SearchDocument::of).toList();
  }

  @GetMapping("/status")
  public String status() {
    return String.format("index contains %d documents", searchService.status());
  }
}
