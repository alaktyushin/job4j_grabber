package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SqlRuParse implements Parse {

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
            rsl.setCreated(dateTimeParser
                    .parse(date));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    public List<Post> list(String url) {
        List<Post> rsl = new ArrayList<>();
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args) {
        DateTimeParser dtp = new SqlRuDateTimeParser();
        SqlRuParse parseForum = new SqlRuParse(dtp);
        String url = "https://www.sql.ru/forum/job-offers";
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String urlN = url.concat("/").concat(String.valueOf(i));
            postList.addAll(parseForum.list(urlN));
        }
        System.out.println(postList.size() + " vacancies loaded.");
    }
}
