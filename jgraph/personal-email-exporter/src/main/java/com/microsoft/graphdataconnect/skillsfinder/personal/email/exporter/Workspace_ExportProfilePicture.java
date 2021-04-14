package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter;

import com.microsoft.aad.msal4j.*;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.services.GraphService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;


public class Workspace_ExportProfilePicture {

    private static final List<String> BEST_EMPLOYEES_EVER = new ArrayList<String>() {{
        //TODO add email addresses of employees
    }};

    private static final String HARDCODED_CLIENT_ID = "f47bb045-f513-4dd5-9f04-1eea501bdcf5";
    private static final String HARDCODED_TENANT_ID = "12e2dd65-5024-44c2-83b5-3ca21c04ef0e";
    private static final String HARDCODED_CLIENT_SECRET = "HARDCODED_CLIENT_SECRET";
    private final static String AUTHORITY = "https://login.microsoftonline.com/" + HARDCODED_TENANT_ID + "/";

    private final static Set<String> SCOPE = Collections.singleton("https://graph.microsoft.com/.default");

    public static void main(String... args) throws Exception {

        ApplicationTokenCredentials creds = new ApplicationTokenCredentials(
                HARDCODED_CLIENT_ID, HARDCODED_TENANT_ID, HARDCODED_CLIENT_SECRET, AzureEnvironment.AZURE);
        Azure.Authenticated authenticated = Azure.configure().authenticate(creds);

        authenticated.activeDirectoryGroups();

        IAuthenticationResult token = acquireToken();

        GraphService graphService = new GraphService(token.accessToken());

        List<String> lines = new ArrayList<>();
        for (String email : BEST_EMPLOYEES_EVER) {
            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE gdc_database.dbo.employee_profile SET profile_picture = '");
            byte[] photo = graphService.readUserProfilePicture(email);
            if (null != photo) {
                String encodedPhoto = Base64.getEncoder().encodeToString(photo);
                sb.append(encodedPhoto);
                sb.append("' where mail = '");
                sb.append(email);
                sb.append("';\n\n");
                lines.add(sb.toString());
            }
        }

        File file = new File("/Users/michael/IdeaProjects/gdc/jgraph/core/src/main/resources/db/test-data/employee_profile_picture.sql");
        FileOutputStream fos = new FileOutputStream(file);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for (String line : lines) {
            bw.write(line);
            bw.newLine();
        }

        bw.close();

//
//        IUserCollectionPage page = graphService.readUsers();
//        List<User> users = page.getCurrentPage();
//
//        while (true) {
//            for (User user : users) {
//                String photo = graphService.readUserProfilePicture(user.mail);
//                if (null != photo) {
//                    String encodedPhoto = java.util.Base64.getEncoder().encodeToString(photo.getBytes());
//                    System.out.println(encodedPhoto);
//                }
//            }
//
//            if (null == page.getNextPage()) {
//                break;
//            }
//
//            page = page.getNextPage().buildRequest().get();
//            users = page.getCurrentPage();
//        }


//        gs.graphClient.users().buildRequest().get()

//        ((Azure.AuthenticatedImpl) authenticated).graphRbacManager.graphRbacManagementClient.users.get("f47bb045-f513-4dd5-9f04-1eea501bdcf5")
//        authenticated.graphRbacManager.graphRbacManagementClient.users.get("f47bb045-f513-4dd5-9f04-1eea501bdcf5")
        System.out.println("done");
    }

    private static IAuthenticationResult acquireToken() throws Exception {
        IClientCredential credential = ClientCredentialFactory.createFromSecret(HARDCODED_CLIENT_SECRET);
        ConfidentialClientApplication cca =
                ConfidentialClientApplication
                        .builder(HARDCODED_CLIENT_ID, credential)
                        .authority(AUTHORITY)
                        .build();

        IAuthenticationResult result;
        try {
            SilentParameters silentParameters =
                    SilentParameters
                            .builder(SCOPE)
                            .build();

            result = cca.acquireTokenSilently(silentParameters).join();
        } catch (Exception ex) {
            if (ex.getCause() instanceof MsalException) {
                ClientCredentialParameters parameters =
                        ClientCredentialParameters
                                .builder(SCOPE)
                                .build();

                result = cca.acquireToken(parameters).join();
            } else {
                throw ex;
            }
        }
        return result;
    }

}
