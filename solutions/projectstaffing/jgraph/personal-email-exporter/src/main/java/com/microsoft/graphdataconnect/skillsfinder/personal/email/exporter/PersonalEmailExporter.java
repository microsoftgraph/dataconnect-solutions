package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.Lists;
import com.microsoft.graph.models.extensions.MailFolder;
import com.microsoft.graph.models.extensions.Message;
import com.microsoft.graph.requests.extensions.IMessageCollectionPage;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.arguments.AppArguments;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.services.AuthenticationService;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.services.EmailsMapper;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.services.GraphService;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.services.OutputWriter;
import org.apache.log4j.BasicConfigurator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PersonalEmailExporter {

    private EmailsMapper emailsMapper;
    Instant startTime;
    Instant endTime;
    String subjectFilter;

    public static void main(String... args) {
        // logging auto config
        BasicConfigurator.configure();

        PersonalEmailExporter app = new PersonalEmailExporter();
        app.start(args);
    }

    public void start(String... args) {
        List<String> emails = new ArrayList<>();
        emailsMapper = new EmailsMapper();

        AppArguments arguments = parseArguments(args);
        startTime = arguments.startDate();
        endTime = arguments.endDate();
        subjectFilter = arguments.getSubjectFilter();

        System.out.println("Starting with parameters: " +
                "startTime:" + arguments.getStartDateStr() +
                " endTime:" + arguments.getEndDateStr() +
                " subject: " + arguments.getSubjectFilter() +
                " timeFilterField " + arguments.getDateField().name());

        AuthenticationService authentication = new AuthenticationService();
        final String accessToken = authentication.getUserAccessToken();

        GraphService graphService = new GraphService(accessToken);

        /*
         *Start reading emails
         */
        List<MailFolder> dirs = Lists.reverse(graphService.getMailDirectories());
        for (int i = 0; i < dirs.size(); i++) {
            MailFolder dir = dirs.get(i);
            int percentage = calculatePercentage(i + 1, dirs.size());
            System.out.println(percentage + "% complete");

            IMessageCollectionPage page = graphService.getMailsFromFolder(dir.id, arguments.getDateField().getValue());
            List<Message> messages = page.getCurrentPage();

            Instant timeOfThisEmail = Instant.now();
            while (true) {
                // process messages
                for (Message message : messages) {
                    timeOfThisEmail = message.receivedDateTime.toInstant();
                    if (shouldReadThisEmail(timeOfThisEmail, message.subject)) {
                        String payload = processEmail(message);
                        emails.add(payload);
                        ;
                    }
                }

                if (null == page.getNextPage()) {
                    break;
                }
                if (!shouldKeepScrolling(timeOfThisEmail)) {
                    break;
                }

                page = page.getNextPage().buildRequest().get();
                messages = page.getCurrentPage();
            }
        }

        String outputFileName = buildOutputFileName(arguments.getStartDateStr(), arguments.getEndDateStr());
        System.out.println("Writing output into " + outputFileName);
        OutputWriter writer = new OutputWriter();
        writer.writeEmails(outputFileName, emails);

        System.out.println("Done");
    }

    private String processEmail(Message message) {
        return emailsMapper.emailToGDCFormat(message.getRawObject().toString());
    }

    private AppArguments parseArguments(String... args) {
        AppArguments arguments = new AppArguments();
        JCommander jct = null;
        try {
            jct = JCommander.newBuilder().addObject(arguments).build();
            jct.parse(args);
        } catch (ParameterException e) {
            System.err.println("Error: " + e.getMessage());
            if (null != jct) {
                jct.usage();
            }
            System.exit(1);
        }
        if (arguments.isHelp()) {
            jct.usage();
            System.exit(0);
        }
        return arguments;
    }

    private int calculatePercentage(int currentSize, int totalSize) {
        return currentSize * 100 / totalSize;
    }

    private boolean shouldReadThisEmail(Instant emailTime, String emailSubject) {
        boolean isInTimeInterval = emailTime.isAfter(startTime) && emailTime.isBefore(endTime);
        boolean subjectMatch = true;
        if (subjectFilter != null && null != emailSubject && !subjectFilter.isEmpty()) {
            subjectMatch = emailSubject.toLowerCase().contains(subjectFilter.toLowerCase());
        }
        return isInTimeInterval && subjectMatch;
    }

    private boolean shouldKeepScrolling(Instant lastEmailTime) {
        return lastEmailTime.isAfter(startTime);
    }

    private String buildOutputFileName(String startDate, String endDate) {
        String username = System.getProperty("user.name");
        if (null != username) {
            return String.format("%s_emails_from_%s_to_%s.jsonl", username, startDate, endDate);
        }
        return String.format("emails_from_%s_to_%s.jsonl", startDate, endDate);
    }
}
