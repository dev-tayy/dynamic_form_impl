package com.example.myapplication

import com.google.gson.annotations.SerializedName

data class FormConfig(
    @SerializedName("forms") val forms: Map<String, Form>
)

data class Form(
    @SerializedName("pages") val pages: Map<String, Page>
)

data class Page(
    @SerializedName("fields") val fields: Map<String, Field>
)

data class Field(
    @SerializedName("ui_type") val uiType: String,
    @SerializedName("column_type") val columnType: String,
    @SerializedName("column_name") val columnName: String,
    @SerializedName("required") val required: Boolean,
    @SerializedName("min_length") val minLength: Int? = null,
    @SerializedName("max_length") val maxLength: Int? = null,
    @SerializedName("showOnList") val showOnList: Boolean? = null,
    @SerializedName("values") val values: List<String>? = null
)
