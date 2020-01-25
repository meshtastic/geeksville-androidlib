package com.geeksville.util

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.beust.klaxon.JsonObject
import java.io.Closeable

class DynamoDBClient(val tableName: String) : Closeable {

    val primaryKey = "id"

    // For local testing
    // private val endpoint = AwsClientBuilder.EndpointConfiguration("http://localhost:6000", "us-west-2")
    private val builder = AmazonDynamoDBClientBuilder.standard().apply {
        // setEndpointConfiguration(endpoint)
    }

    val client = builder.build()!!
    private val db = DynamoDB(client)

    val table = db.getTable(tableName)

    fun get(key: String): Map<String, AttributeValue>? {
        val keymap = mapOf(primaryKey to AttributeValue(key))
        val req = GetItemRequest().withKey(keymap).withTableName(tableName)
        return client.getItem(req).item?.toMap()
    }

    fun put(key: String, values: Map<String, Any?>) {
        val i = Item().withPrimaryKey(primaryKey, key)

        values.forEach { (k, v) ->
            when (v) {
                is JsonObject -> i.withJSON(k, v.toJsonString(false))
                else -> i.with(k, v)
            }
        }
        table.putItem(i)
    }

    fun delete(key: String) {
        val d = DeleteItemSpec().withPrimaryKey(primaryKey, key)
        table.deleteItem(d)
    }

    override fun close() {
        db.shutdown()
    }
}
