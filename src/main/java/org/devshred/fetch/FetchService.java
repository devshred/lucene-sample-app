package org.devshred.fetch;

import static java.util.Collections.emptyList;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import io.vavr.control.Try;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class FetchService {
  public List<FeedEntry> fetch(String feedUrl) {
    return fetchEntriesFromUrl(feedUrl);
  }

  private List<FeedEntry> fetchEntriesFromUrl(String feedUrl) {
    final RestTemplate restTemplate = new RestTemplate();
    final SyndFeed syndFeed =
        restTemplate.execute(
            feedUrl,
            HttpMethod.GET,
            null,
            response -> {
              final SyndFeedInput input = new SyndFeedInput();
              try {
                return input.build(new XmlReader(response.getBody()));
              } catch (FeedException e) {
                throw new IOException("Could not parse response", e);
              }
            });

    return Try.of(() -> StreamEx.of(syndFeed.getEntries()).map(FeedEntry::of).toList())
        .getOrElse(emptyList());
  }
}
