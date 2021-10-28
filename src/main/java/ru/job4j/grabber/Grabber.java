package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.html.Post;
import ru.job4j.html.SqlRuParse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Properties cfg = new Properties();

    public Store store() {
        return new PsqlStore(cfg);
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfg() throws IOException {

        try (InputStream in =
                     new FileInputStream("./src/main/resources/app.properties")) {
            cfg.load(in);
            System.out.println(cfg);
        }
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            System.out.println("Let's get the party started!");
            String url = "https://www.sql.ru/forum/job-offers";
            List<Post> postList = new ArrayList<>();
            for (int i = 1; i <= 30000; i++) {
                String urlN = url.concat("/").concat(String.valueOf(i));
                List<Post> list = parse.list(urlN);
                if (list.size() <= 3) {
                    break;
                }
                postList.addAll(list);
            }
            System.out.println(postList.size() + " vacancies parsed.");
            for (var el : postList) {
                if (el.getTitle().toLowerCase().contains("java")
                        && !el.getTitle().toLowerCase().contains("javascript")) {
                    store.save(el);
                }
            }
            System.out.println(
                    "There's "
                            + store.getAll().size()
                            + " vacancies in the database regarding Java.");
        }
    }

    public static void main(String[] args) throws Exception {
        DateTimeParser dtp = new SqlRuDateTimeParser();
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new SqlRuParse(dtp), store, scheduler);
    }
}