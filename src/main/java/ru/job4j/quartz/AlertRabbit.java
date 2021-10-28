package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    private final String filename = "rabbit.properties";
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static void main(String[] args) {
        AlertRabbit rabbit = new AlertRabbit();
        Properties properties = rabbit.getPropertiesFromFile(rabbit.filename);
        try (Connection connection = rabbit.initConnection(properties)) {
            int interval = Integer.parseInt(properties.getProperty("rabbit.interval"));
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                JobDataMap data = new JobDataMap();
                data.put("connection", connection);
                JobDetail job = newJob(Rabbit.class)
                        .usingJobData(data)
                        .build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(interval)
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(10000);
                scheduler.shutdown();
                System.out.println(
                        "Thread stopped at "
                                + LocalDateTime.now().format(rabbit.formatter)
                );
        } catch (SQLException | SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println("Rabbit says hashCode = " + hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            Connection connection = (Connection) dataMap.get("connection");
            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 "insert into rabbit(created_date) values (?)"
                         )) {
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Connection initConnection(Properties config) {
        Connection connection = null;
        try {
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private Properties getPropertiesFromFile(String propsFile) {
        InputStream in = AlertRabbit.class
                .getClassLoader()
                .getResourceAsStream(propsFile);
        Properties config = new Properties();
        try {
            config.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }
}