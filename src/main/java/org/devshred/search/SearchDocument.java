package org.devshred.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

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

  public Document toDocument() {
    final Document document = new Document();
    document.add(new TextField("url", this.getUrl(), Field.Store.YES));
    document.add(new TextField("title", this.getTitle(), Field.Store.YES));
    document.add(new TextField("content", this.getContent(), Field.Store.YES));
    return document;
  }
}
