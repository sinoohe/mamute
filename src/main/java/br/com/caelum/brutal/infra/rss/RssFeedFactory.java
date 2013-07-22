package br.com.caelum.brutal.infra.rss;

import static br.com.caelum.brutal.infra.rss.RssEntryBuilder.RSS_DATE_FORMATTER;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.joda.time.DateTime;

import br.com.caelum.brutal.model.interfaces.RssContent;
import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.ioc.Component;

@Component
public class RssFeedFactory {
	private QuestionRssEntryFactory entryFactory;
	private PrintStream stream;
	private String home;
	private String description;
	private String title;
	
	public RssFeedFactory(Environment env, QuestionRssEntryFactory questionRssEntryFactory) {
		this.home = env.get("host") + env.get("home.url");
		this.entryFactory = questionRssEntryFactory;
		this.title = env.get("rss.title", "GUJ respostas");
		this.description = env.get("rss.description", "Últimas perguntas do GUJ respostas");
	}

	public void build(List<RssContent> rssContents, OutputStream output) {
		stream = new PrintStream(output);
		open(output);
		for (RssContent rssContent : rssContents) {
			entryFactory.writeEntry(rssContent, output);
			stream.print('\n');
		}
		close(output);
	}


	private void open(OutputStream output) {
		DateTime dateTime = new DateTime();
		stream.print("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
				+ "<rss version=\"2.0\">\n"
				+ "<channel>\n"
				+ "<title>" + title + "</title>\n"
				+ "<description>" + description + "</description>\n"
				+ "<link>" + home + "</link>\n"
				+ "<lastBuildDate>" + RSS_DATE_FORMATTER.print(dateTime) + "</lastBuildDate>\n"
				+ "<pubDate>" + RSS_DATE_FORMATTER.print(dateTime) + "</pubDate>\n" 
				+ "<ttl>1800</ttl>\n\n");
	}

	private void close(OutputStream output) {
		stream.print("\n</channel>\n</rss>");
	}
}
