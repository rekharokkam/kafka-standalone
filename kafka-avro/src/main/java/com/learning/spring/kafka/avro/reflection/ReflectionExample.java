package com.learning.spring.kafka.avro.reflection;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;

import java.io.File;
import java.io.IOException;

@Slf4j
public class ReflectionExample {

    public static void main(String[] args) {

        //Use reflection to determine the schema
        Schema schema = ReflectData.get().getSchema(ReflectionCustomer.class);
        log.info("Reflection Customer Schema : {}", schema.toString(true));

        //publish the schema to a file
        final DatumWriter<ReflectionCustomer> datumWriter = new ReflectDatumWriter<>(ReflectionCustomer.class);
        try (DataFileWriter<ReflectionCustomer> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.setCodec(CodecFactory.deflateCodec(9))
                    .create(schema, new File("customer-reflection-generated.avro"));
            dataFileWriter.append(new ReflectionCustomer("Vidyut", "Jammwal", null));
            log.info("avro schema successfully created from Reflection Record");
        } catch (IOException ioException) {
            System.err.println("Exception occurred while writing Reflection Record to a file");
            ioException.printStackTrace(System.err);
        }

        //Read from a file
        final File reflectionCustomerFile = new File("customer-reflection-generated.avro");
        final DatumReader<ReflectionCustomer> datumReader = new ReflectDatumReader<>(ReflectionCustomer.class);
        try (DataFileReader<ReflectionCustomer> dataFileReader = new DataFileReader<>(reflectionCustomerFile, datumReader)) {

            for (ReflectionCustomer customer : dataFileReader) {
                log.info("Reflection Customer : {}", customer);
            }

        } catch (IOException ioException) {
            System.err.println("Exception occurred while Reading Reflection Record from a file");
            ioException.printStackTrace(System.err);
        }

    }
}
