package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        SqlRuParse parseForum = new SqlRuParse();
        String url = "https://www.sql.ru/forum/job-offers";
        String cssQuery = ".postslisttopic";
        for (int i = 1; i <= 5; i++) {
            String urlN = url. concat("/").concat(String.valueOf(i));
            parseForum.parsePage(urlN, cssQuery);
            Thread.sleep(1000);
        }
    }

    private void parsePage(String url, String cssQuery) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements row = doc.select(cssQuery);
        SqlRuDateTimeParser pageParser = new SqlRuDateTimeParser();
        System.out.println(
                System.lineSeparator()
                + "Parsing URL " + url
                        + System.lineSeparator()
        );
        for (Element td : row) {
            Element href = td.child(0);
            Element parent = td.parent();
            System.out.println(href.attr("href"));
            System.out.println(href.text());
            if (parent != null) {
                String strDate = parent.children().get(5).text();
                System.out.println(pageParser.parse(strDate));
            }
        }
    }
}