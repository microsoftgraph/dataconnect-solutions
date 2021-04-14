package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gapi.EmailAddress;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gapi.GraphApiEmail;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gdc.*;

import java.util.ArrayList;
import java.util.List;

public class EmailsMapper {

    private static final ObjectMapper objectMapp = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public String emailToGDCFormat(String emailGAPIFormat) {
        try {
            GraphApiEmail graphApiEmail = objectMapp.readValue(emailGAPIFormat, GraphApiEmail.class);
            GdcEmail gdcEmail = GAPIToGDCEmail(graphApiEmail);

            return objectMapp.writeValueAsString(gdcEmail);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private GdcEmail GAPIToGDCEmail(GraphApiEmail graphApiEmail) {
        GdcEmail gdcEmail = new GdcEmail();
        gdcEmail.setId(graphApiEmail.getId());
        gdcEmail.setCreatedDateTime(graphApiEmail.getCreatedDateTime());
        gdcEmail.setLastModifiedDateTime(graphApiEmail.getLastModifiedDateTime());
        gdcEmail.setChangeKey(graphApiEmail.getChangeKey());
        gdcEmail.setCategories(graphApiEmail.getCategories());
        gdcEmail.setReceivedDateTime(graphApiEmail.getReceivedDateTime());
        gdcEmail.setSentDateTime(graphApiEmail.getSentDateTime());
        gdcEmail.setHasAttachments(graphApiEmail.getHasAttachments());
        gdcEmail.setInternetMessageId(graphApiEmail.getInternetMessageId());
        gdcEmail.setSubject(graphApiEmail.getSubject());
        gdcEmail.setImportance(graphApiEmail.getImportance());
        gdcEmail.setParentFolderId(graphApiEmail.getParentFolderId());
        gdcEmail.setConversationId(graphApiEmail.getConversationId());
        gdcEmail.setDeliveryReceiptRequested(graphApiEmail.getDeliveryReceiptRequested());
        gdcEmail.setReadReceiptRequested(graphApiEmail.getReadReceiptRequested());
        gdcEmail.setRead(graphApiEmail.getRead());
        gdcEmail.setDraft(graphApiEmail.getDraft());
        gdcEmail.setWebLink(graphApiEmail.getWebLink());
        gdcEmail.setSender(new GdcSender(new GdcEmailAddressFields(graphApiEmail.getSender().getEmailAddress().getAddress(), graphApiEmail.getSender().getEmailAddress().getAddress())));
        gdcEmail.setFrom(new GdcFrom(new GdcEmailAddressFields(graphApiEmail.getFrom().getEmailAddress().getAddress(), graphApiEmail.getFrom().getEmailAddress().getAddress())));
        gdcEmail.setToRecipients(emailAddressesGraphApiToEmailAddressesGDC(graphApiEmail.getToRecipients()));
        gdcEmail.setCcRecipients(emailAddressesGraphApiToEmailAddressesGDC(graphApiEmail.getCcRecipients()));
        gdcEmail.setBccRecipients(emailAddressesGraphApiToEmailAddressesGDC(graphApiEmail.getBccRecipients()));
        gdcEmail.setReplyTo(emailAddressesGraphApiToEmailAddressesGDC(graphApiEmail.getReplyTo()));

        String GAPIBodyType = graphApiEmail.getBody().getContentType();
        if ("html".equals(GAPIBodyType)) {
            gdcEmail.setUniqueBody(new GdcBody(GdcBody.HTML_TYPE, graphApiEmail.getBody().getContent()));
        } else {
            gdcEmail.setUniqueBody(new GdcBody(graphApiEmail.getBody().getContentType(), graphApiEmail.getBody().getContent()));
        }

        return gdcEmail;
    }

    private List<com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gdc.EmailAddress> emailAddressesGraphApiToEmailAddressesGDC(List<EmailAddress> gapiEmails) {
        List<com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gdc.EmailAddress> result = new ArrayList<>();
        for (EmailAddress gapiEmail : gapiEmails) {
            com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gdc.EmailAddress gdcEmail = new com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gdc.EmailAddress(new GdcEmailAddressFields(gapiEmail.getEmailAddress().getName(), gapiEmail.getEmailAddress().getAddress()));
            result.add(gdcEmail);
        }
        return result;
    }

}
