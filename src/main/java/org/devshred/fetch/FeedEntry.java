package org.devshred.fetch;

import com.rometools.rome.feed.synd.SyndEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.devshred.search.SearchDocument;

@AllArgsConstructor
@Getter
public class FeedEntry {
  private final String url;
  private final String title;
  private final String content;

  public static FeedEntry of(SyndEntry syndEntry) {
    return new FeedEntry(
        syndEntry.getLink(),
        syndEntry.getTitle(),
        syndEntry.getContents().isEmpty()
            ? syndEntry.getTitle()
            : syndEntry.getContents().get(0).toString());
  }

  public SearchDocument toSearchDocument() {
    return new SearchDocument(url, title, content);
  }
}
