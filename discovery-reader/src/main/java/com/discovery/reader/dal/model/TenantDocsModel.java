package com.discovery.reader.dal.model;

import com.google.gson.annotations.SerializedName;

public class TenantDocsModel {

    @SerializedName("Documents")
    private TenantDocModel[] documents;

    public TenantDocModel[] getDocuments() {
        return documents;
    }

    public void setDocuments(TenantDocModel[] documents) {
        this.documents = documents;
    }
}