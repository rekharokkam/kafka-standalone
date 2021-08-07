package com.learning.spring.kafka.avro.genericrecord;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;

import java.io.File;
import java.io.IOException;

@Slf4j
public class GenericRecordExample {

    public static void main(String[] args) {

        System.out.println(System.getProperty("java.library.path"));

        //Define a Schema
        Schema schema = new Schema.Parser().parse("{\n" +
                "  \"type\": \"record\",\n" +
                "  \"namespace\": \"com.learning.spring.kafka.avro\",\n" +
                "  \"name\": \"CustomerBasic\",\n" +
                "  \"doc\": \"Avro Schema for Basic Customer\",\n" +
                "  \"fields\": [\n" +
                "    {\n" +
                "      \"name\": \"first_name\",\n" +
                "      \"type\": \"string\",\n" +
                "      \"doc\": \"First Name of the Customer\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"last_name\",\n" +
                "      \"type\": \"string\",\n" +
                "      \"doc\": \"Last Name of the Customer\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"age\",\n" +
                "      \"type\": \"int\",\n" +
                "      \"doc\": \"Age of the Customer\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"height\",\n" +
                "      \"type\": \"float\",\n" +
                "      \"doc\": \"Height of the customer in inches\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"weight\",\n" +
                "      \"type\": \"float\",\n" +
                "      \"doc\": \"weight of the customer in pounds\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"automated_email\",\n" +
                "      \"type\": \"boolean\",\n" +
                "      \"default\": true,\n" +
                "      \"doc\": \"Indicates if the customer opted for automated marketing emails, by default its true\"\n" +
                "    }\n" +
                "  ]\n" +
                "}");

        //Builds GenericRecord
        GenericRecordBuilder customerBasicBuilder = new GenericRecordBuilder(schema);
        customerBasicBuilder.set("first_name", "Vidyut  ");
        customerBasicBuilder.set("last_name", "Jammwal");
        customerBasicBuilder.set("age", 35);
        customerBasicBuilder.set("height", 170f);
        customerBasicBuilder.set("weight", 80.5f);
        customerBasicBuilder.set("automated_email", false);
        GenericRecord customerBasic = customerBasicBuilder.build();

        log.info ("Generic Record : {}", customerBasic.toString());

        //Build a GenericRecord with defaults
        GenericRecordBuilder customerBasicBuilderWithDefaults = new GenericRecordBuilder(schema);
        customerBasicBuilderWithDefaults.set("first_name", "Vidyut  ");
        customerBasicBuilderWithDefaults.set("last_name", "Jammwal");
        customerBasicBuilderWithDefaults.set("age", 35);
        customerBasicBuilderWithDefaults.set("height", 170f);
        customerBasicBuilderWithDefaults.set("weight", 80.5f);
        GenericRecord customerBasicWithDefaults = customerBasicBuilderWithDefaults.build();

        log.info ("Generic Record with defaults : {}", customerBasicWithDefaults.toString());

        //Write the generic record to a file
        final DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        try (DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(customerBasic.getSchema(), new File("customer-generated.avro"));
            dataFileWriter.append(customerBasic);
            log.info("avro schema successfully created from Generic Record");
        } catch (IOException ioException) {
            System.err.println("Exception occurred while writing Generic Record to a file");
            ioException.printStackTrace(System.err);
        }

        //Read a generic record from a file
        final File basicCustomerFile = new File("customer-generated.avro");
        final DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
        GenericRecord customerBasicRecord;
        try (DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(basicCustomerFile, datumReader)) {
            customerBasicRecord = dataFileReader.next();
            log.info("Successfully read customer record");
            log.info("Read avro schema from file : {}", customerBasicRecord.toString());
            log.info("Customer First Name : {}" , customerBasicRecord.get("first_name"));
            log.info("Reading a non-existin field : {}", customerBasicRecord.get("non-existing"));
        } catch (IOException ioException) {
            System.err.println("Exception occurred while Reading Generic Record from a file");
            ioException.printStackTrace(System.err);
        }
    }
}
