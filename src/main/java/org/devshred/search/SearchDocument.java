package org.devshred.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.lucene.document.Document;

@AllArgsConstructor
@Getter
public class SearchDocument {
  private final String url;
  private final String title;
  private final String content;

  public static SearchDocument of(Document document) {
    return new SearchDocument(
        document.getField("url").stringValue(),
        document.getField("title").stringValue(),
        document.getField("content").stringValue());
  }
}
