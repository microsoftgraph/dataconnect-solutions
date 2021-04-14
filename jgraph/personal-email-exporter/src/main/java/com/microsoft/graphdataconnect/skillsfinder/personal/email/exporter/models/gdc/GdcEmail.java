package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gdc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GdcEmail {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("CreatedDateTime")
    private String createdDateTime;
    @JsonProperty("LastModifiedDateTime")
    private String lastModifiedDateTime;
    @JsonProperty("ChangeKey")
    private String changeKey;
    @JsonProperty("Categories")
    private List<String> categories;
    @JsonProperty("ReceivedDateTime")
    private String receivedDateTime;
    @JsonProperty("SentDateTime")
    private String sentDateTime;
    @JsonProperty("HasAttachments")
    private Boolean hasAttachments;
    @JsonProperty("InternetMessageId")
    private String internetMessageId;
    @JsonProperty("Subject")
    private String subject;
    @JsonProperty("Importance")
    private String importance;
    @JsonProperty("ParentFolderId")
    private String parentFolderId;
    @JsonProperty("ConversationId")
    private String conversationId;
    @JsonProperty("IsDeliveryReceiptRequested")
    private Boolean deliveryReceiptRequested;
    @JsonProperty("IsReadReceiptRequested")
    private Boolean readReceiptRequested;
    @JsonProperty("IsRead")
    private Boolean read;
    @JsonProperty("IsDraft")
    private Boolean draft;
    @JsonProperty("WebLink")
    private String webLink;
    @JsonProperty("Sender")
    private GdcSender sender;
    @JsonProperty("From")
    private GdcFrom from;
    @JsonProperty("ToRecipients")
    private List<EmailAddress> toRecipients;
    @JsonProperty("CcRecipients")
    private List<EmailAddress> ccRecipients;
    @JsonProperty("BccRecipients")
    private List<EmailAddress> bccRecipients;
    @JsonProperty("ReplyTo")
    private List<EmailAddress> replyTo;
    @JsonProperty("UniqueBody")
    private GdcBody uniqueBody;
    @JsonProperty("ODataType")
    private String oDataType = "#Microsoft.OutlookServices.Message";
    @JsonProperty("puser")
    private String puser;
    @JsonProperty("ptenant")
    private String ptenant;
    @JsonProperty("pAdditionalInfo")
    private String pAdditionalInfo;
    @JsonProperty("datarow")
    private Integer datarow;
    @JsonProperty("userrow")
    private Integer userrow;
    @JsonProperty("pagerow")
    private Integer pagerow;
    @JsonProperty("rowinformation")
    private RowInformation rowinformation;

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

    public GdcBody getUniqueBody() {
        return uniqueBody;
    }

    public void setUniqueBody(GdcBody uniqueBody) {
        this.uniqueBody = uniqueBody;
    }

    public GdcSender getSender() {
        return sender;
    }

    public void setSender(GdcSender sender) {
        this.sender = sender;
    }

    public GdcFrom getFrom() {
        return from;
    }

    public void setFrom(GdcFrom from) {
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

    public String getoDataType() {
        return oDataType;
    }

    public void setoDataType(String oDataType) {
        this.oDataType = oDataType;
    }

    public String getPuser() {
        return puser;
    }

    public void setPuser(String puser) {
        this.puser = puser;
    }

    public String getPtenant() {
        return ptenant;
    }

    public void setPtenant(String ptenant) {
        this.ptenant = ptenant;
    }

    public String getpAdditionalInfo() {
        return pAdditionalInfo;
    }

    public void setpAdditionalInfo(String pAdditionalInfo) {
        this.pAdditionalInfo = pAdditionalInfo;
    }

    public Integer getDatarow() {
        return datarow;
    }

    public void setDatarow(Integer datarow) {
        this.datarow = datarow;
    }

    public Integer getUserrow() {
        return userrow;
    }

    public void setUserrow(Integer userrow) {
        this.userrow = userrow;
    }

    public Integer getPagerow() {
        return pagerow;
    }

    public void setPagerow(Integer pagerow) {
        this.pagerow = pagerow;
    }

    public RowInformation getRowinformation() {
        return rowinformation;
    }

    public void setRowinformation(RowInformation rowinformation) {
        this.rowinformation = rowinformation;
    }
}
