package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SqlRuParse {

    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public Post detail(String url) {
        Post rsl = new Post();
        try {
            Document doc = Jsoup.connect(url).get();
            rsl.setLink(url);
            rsl.setTitle(Objects.requireNonNull(doc
                            .select(".messageHeader")
                            .first())
                    .text()
                    .split(" \\[")[0]);
            rsl.setDescription(doc
                    .select(".msgBody")
                    .get(1)
                    .text());
            String date = Objects.requireNonNull(doc
                            .select(".msgFooter")
                            .first())
                    .text()
                    .split(" \\[")[0];
            rsl.setLocalDateTime(dateTimeParser
                    .parse(date));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    private void parsePage(String url, String cssQuery) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements row = doc.select(cssQuery);
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
                System.out.println(dateTimeParser.parse(strDate));
            }
        }
    }

    private List<Post> list(String url) throws IOException {
        List<Post> rsl = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        String cssQuery = ".postslisttopic";
        Elements row = doc.select(cssQuery);
        for (Element td : row) {
            Element href = td.child(0);
            String link = href.attr("href");
            rsl.add(detail(link));
        }
        System.out.println("Added " + rsl.size() + " vacancies from " + url);
        return rsl;
    }

    public static void main(String[] args) throws Exception {
        DateTimeParser dtp = new SqlRuDateTimeParser();
        SqlRuParse parseForum = new SqlRuParse(dtp);
        String url = "https://www.sql.ru/forum/job-offers";
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String urlN = url. concat("/").concat(String.valueOf(i));
            /*parseForum.parsePage(urlN, cssQuery);*/
            postList.addAll(parseForum.list(urlN));
        }
        System.out.println(postList.size() + " vacancies loaded.");
    }
}