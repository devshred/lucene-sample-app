package org.devshred.search;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import io.vavr.control.Try;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SearchService {
  private Analyzer analyzer;
  private Directory directory;
  private IndexWriter writer;
  private QueryParser queryParser;

  public SearchService(@Value("${index.location:lucene.idx}") String indexLocation) {
    try {
      analyzer = new StandardAnalyzer();

      directory = new MMapDirectory(Paths.get(indexLocation));
      writer = new IndexWriter(directory, new IndexWriterConfig(analyzer));
      writer.commit();

      queryParser = new QueryParser("title", analyzer);

      final IndexReader reader = DirectoryReader.open(directory);
      log.info("index opened with {} documents", reader.numDocs());
    } catch (IndexNotFoundException e) {
      log.warn("empty index");
    } catch (IOException e) {
      log.error("failed to initialize SearchService", e);
    }
  }

  public void addToIndex(SearchDocument searchDocument) {
    addToIndex(singletonList(searchDocument));
  }

  public void addToIndex(List<SearchDocument> searchDocuments) {
    for (SearchDocument searchDocument : searchDocuments) {
      final Document document = new Document();
      document.add(new TextField("url", searchDocument.getUrl(), Field.Store.YES));
      document.add(new TextField("title", searchDocument.getTitle(), Field.Store.YES));
      document.add(new TextField("content", searchDocument.getContent(), Field.Store.YES));

      try {
        writer.addDocument(document);
        log.debug("added to index: {}", searchDocument.getTitle());
      } catch (IOException e) {
        log.error("failed to add to index: {}", searchDocument.getTitle(), e);
      }
      try {
        writer.commit();
      } catch (IOException e) {
        log.error("failed to commit to index", e);
      }
    }
  }

  public List<Document> search(SearchQuery queryString) {
    try {
      final Query query = queryParser.parse(queryString.getQuery());

      final IndexReader indexReader = DirectoryReader.open(directory);
      final IndexSearcher searcher = new IndexSearcher(indexReader);
      final TopDocs topDocs = searcher.search(query, 10);

      final List<Document> documents = new ArrayList<>();
      for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
        documents.add(searcher.doc(scoreDoc.doc));
      }
      log.info("found {} entries for {}", documents.size(), queryString);
      return documents;
    } catch (IOException | ParseException e) {
      log.error("something went wrong while searching for {}", queryString, e);
    }
    return emptyList();
  }

  @PreDestroy
  private void close() {
    try {
      directory.close();
      log.info("directory closed");
    } catch (IOException e) {
      log.error("Failed to close index: {}", e.getMessage());
    }
  }

  public Integer status() {
    return Try.of(() -> DirectoryReader.open(directory).numDocs()).getOrElse(0);
  }
}
