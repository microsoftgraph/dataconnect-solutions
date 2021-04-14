package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GraphApiEmail {
    @JsonProperty("id")
    private String id;
    @JsonProperty("createdDateTime")
    private String createdDateTime;
    @JsonProperty("lastModifiedDateTime")
    private String lastModifiedDateTime;
    @JsonProperty("changeKey")
    private String changeKey;
    @JsonProperty("categories")
    private List<String> categories;
    @JsonProperty("receivedDateTime")
    private String receivedDateTime;
    @JsonProperty("sentDateTime")
    private String sentDateTime;
    @JsonProperty("hasAttachments")
    private Boolean hasAttachments;
    @JsonProperty("internetMessageId")
    private String internetMessageId;
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("bodyPreview")
    private String bodyPreview;
    @JsonProperty("importance")
    private String importance;
    @JsonProperty("parentFolderId")
    private String parentFolderId;
    @JsonProperty("conversationId")
    private String conversationId;
    @JsonProperty("conversationIndex")
    private String conversationIndex;
    @JsonProperty("isDeliveryReceiptRequested")
    private Boolean deliveryReceiptRequested;
    @JsonProperty("isReadReceiptRequested")
    private Boolean readReceiptRequested;
    @JsonProperty("isRead")
    private Boolean read;
    @JsonProperty("isDraft")
    private Boolean draft;
    @JsonProperty("webLink")
    private String webLink;
    @JsonProperty("inferenceClassification")
    private String inferenceClassification;
    @JsonProperty("body")
    private Body body;
    @JsonProperty("sender")
    private Sender sender;
    @JsonProperty("from")
    private From from;
    @JsonProperty("toRecipients")
    private List<EmailAddress> toRecipients;
    @JsonProperty("ccRecipients")
    private List<EmailAddress> ccRecipients;
    @JsonProperty("bccRecipients")
    private List<EmailAddress> bccRecipients;
    @JsonProperty("replyTo")
    private List<EmailAddress> replyTo;
    @JsonProperty("flag")
    private Flag flag;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(String lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    public String getChangeKey() {
        return changeKey;
    }

    public void setChangeKey(String changeKey) {
        this.changeKey = changeKey;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getReceivedDateTime() {
        return receivedDateTime;
    }

    public void setReceivedDateTime(String receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }

    public String getSentDateTime() {
        return sentDateTime;
    }

    public void setSentDateTime(String sentDateTime) {
        this.sentDateTime = sentDateTime;
    }

    public Boolean getHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(Boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public String getInternetMessageId() {
        return internetMessageId;
    }

    public void setInternetMessageId(String internetMessageId) {
        this.internetMessageId = internetMessageId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyPreview() {
        return bodyPreview;
    }

    public void setBodyPreview(String bodyPreview) {
        this.bodyPreview = bodyPreview;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(String parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationIndex() {
        return conversationIndex;
    }

    public void setConversationIndex(String conversationIndex) {
        this.conversationIndex = conversationIndex;
    }

    public Boolean getDeliveryReceiptRequested() {
        return deliveryReceiptRequested;
    }

    public void setDeliveryReceiptRequested(Boolean deliveryReceiptRequested) {
        this.deliveryReceiptRequested = deliveryReceiptRequested;
    }

    public Boolean getReadReceiptRequested() {
        return readReceiptRequested;
    }

    public void setReadReceiptRequested(Boolean readReceiptRequested) {
        this.readReceiptRequested = readReceiptRequested;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getDraft() {
        return draft;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getInferenceClassification() {
        return inferenceClassification;
    }

    public void setInferenceClassification(String inferenceClassification) {
        this.inferenceClassification = inferenceClassification;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public From getFrom() {
        return from;
    }

    public void setFrom(From from) {
        this.from = from;
    }

    public List<EmailAddress> getToRecipients() {
        return toRecipients;
    }

    public void setToRecipients(List<EmailAddress> toRecipients) {
        this.toRecipients = toRecipients;
    }

    public List<EmailAddress> getCcRecipients() {
        return ccRecipients;
    }

    public void setCcRecipients(List<EmailAddress> ccRecipients) {
        this.ccRecipients = ccRecipients;
    }

    public List<EmailAddress> getBccRecipients() {
        return bccRecipients;
    }

    public void setBccRecipients(List<EmailAddress> bccRecipients) {
        this.bccRecipients = bccRecipients;
    }

    public List<EmailAddress> getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(List<EmailAddress> replyTo) {
        this.replyTo = replyTo;
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }
}
