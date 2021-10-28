package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.util.Objects;

public class ParseVacancy {

    public Post getPost(String url) {
        Post rsl = new Post();
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
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
            rsl.setCreated(parser
                    .parse(date));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    public static void main(String[] args) {
        ParseVacancy parseVacancy = new ParseVacancy();
        String url =
                "https://www.sql.ru/forum/1325330"
                        + "/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t";
        Post post = parseVacancy.getPost(url);
        Post postShort = parseVacancy.getPost("https://www.sql.ru/forum/1325330");
        Post postWrong = parseVacancy.getPost("https://www.sql.ru/forum/13");
        System.out.println(post);
        System.out.println(postShort);
        System.out.println(postWrong);
    }
}
