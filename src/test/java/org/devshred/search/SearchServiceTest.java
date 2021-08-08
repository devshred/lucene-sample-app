package org.devshred.search;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.apache.lucene.document.Document;
import org.junit.jupiter.api.Test;

class SearchServiceTest {
  private static final String URL = "http://exampleorg";
  private static final String TITLE = "some title";
  private static final String CONTENT = "some content";
  private SearchService searchService;

  public SearchServiceTest() {
    try {
      searchService = new SearchService(Files.createTempDirectory("lucene.index.").toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void addToIndex(){
    searchService.addToIndex(new SearchDocument(URL, TITLE, CONTENT));

    List<Document> results = searchService.search(new SearchQuery("title:" + TITLE));
    assertThat(results).hasSize(1);
  }
}
