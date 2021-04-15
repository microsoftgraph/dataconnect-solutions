package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.services;

import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.MailFolder;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.graph.requests.extensions.IMessageCollectionPage;
import com.microsoft.graph.requests.extensions.IUserCollectionPage;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class GraphService {

    public static final int HARDCODED_EMAILS_PER_PAGE = 20;
    public static final int HARDCODED_USERS_PER_PAGE = 20;

    private final String accessToken;

    private IGraphServiceClient graphClient = null;
    private SimpleAuthProvider authProvider = null;

    public GraphService(String accessToken) {
        this.accessToken = accessToken;
    }

    private void ensureGraphClient(String accessToken) {
        if (graphClient == null) {
            // Create the auth provider
            authProvider = new SimpleAuthProvider(accessToken);

            // Create default logger to only log errors
            DefaultLogger logger = new DefaultLogger();
            logger.setLoggingLevel(LoggerLevel.ERROR);

            // Build a Graph client
            graphClient = GraphServiceClient.builder()
                    .authenticationProvider(authProvider)
                    .logger(logger)
                    .buildClient();
        }
    }

    public User getUser() {
        ensureGraphClient(accessToken);

        // GET /me to get authenticated user
        User me = graphClient
                .me()
                .buildRequest()
                .get();

        return me;
    }

    public List<MailFolder> getMailDirectories() {
        ensureGraphClient(accessToken);
        final List<Option> options = new LinkedList<>();
        options.add(new QueryOption("top", "300"));
        return graphClient.me().mailFolders().buildRequest(options).get().getCurrentPage();
    }

    public IMessageCollectionPage getMailsFromFolder(String mailFolderId, String columnToFilter) {
        ensureGraphClient(accessToken);
        // receivedDateTime
        final List<Option> options = new LinkedList<>();
        options.add(new QueryOption("orderby", columnToFilter + " DESC"));
        options.add(new QueryOption("top", String.valueOf(HARDCODED_EMAILS_PER_PAGE)));
        return graphClient.me().mailFolders(mailFolderId).messages().buildRequest(options).get();
    }

    public IUserCollectionPage readUsers() {
        ensureGraphClient(accessToken);
        return graphClient.users().buildRequest().get();
    }

    public byte[] readUserProfilePicture(String mail) {
        ensureGraphClient(accessToken);
        try {
            BufferedInputStream stream = graphClient.customRequest("/users/" + mail + "/photo/$value", BufferedInputStream.class)
                    .buildRequest()
                    .get();

            return readStream(stream);
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] readStream(BufferedInputStream stream) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        while (true) {
            try {
                if ((nRead = stream.read(data, 0, data.length)) == -1) break;
                buffer.write(data, 0, nRead);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return buffer.toByteArray();

    }

}
