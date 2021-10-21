package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements row = doc.select(".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            Element parent = td.parent();
            System.out.println(href.attr("href"));
            System.out.println(href.text());
            if (parent != null) {
                System.out.println(parent
                        .children()
                        .get(5)
                        .text()
                        .replaceAll("сегодня", LocalDate.now()
                                .format(DateTimeFormatter.ofPattern("dd MMM yy")))
                        .replaceAll("вчера", (LocalDate.now().minusDays(1))
                                .format(DateTimeFormatter.ofPattern("dd MMM yy"))));
            }
        }
    }
}