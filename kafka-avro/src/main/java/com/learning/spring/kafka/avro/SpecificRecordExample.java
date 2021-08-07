package com.learning.spring.kafka.avro;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;

@Slf4j
public class SpecificRecordExample {

    public static void main(String[] args) {
        //Create SpecificRecord
        CustomerBasic.Builder customerBasicBuilder = CustomerBasic.newBuilder();
        customerBasicBuilder.setFirstName("Vidyut");
        customerBasicBuilder.setLastName("Jammwal");
        customerBasicBuilder.setAge(35);
        customerBasicBuilder.setHeight(160.75f);
        customerBasicBuilder.setWeight(110.00f);
        customerBasicBuilder.setAutomatedEmail(false);

        CustomerBasic customerBasic = customerBasicBuilder.build();
        log.info("Specific Customer built from Specific Record : {}", customerBasic);

        //Write to a file
        final DatumWriter<CustomerBasic> datumWriter = new SpecificDatumWriter<>(CustomerBasic.class);
        try (DataFileWriter<CustomerBasic> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(customerBasic.getSchema(), new File("customer-specific-generated.avro"));
            dataFileWriter.append(customerBasic);
            log.info("avro schema successfully created from Specific Record");
        } catch (IOException ioException) {
            System.err.println("Exception occurred while writing Generic Record to a file");
            ioException.printStackTrace(System.err);
        }

        //Read from a file
        final File basicCustomerFile = new File("customer-specific-generated.avro");
        final DatumReader<CustomerBasic> datumReader = new SpecificDatumReader<>();
        try (DataFileReader<CustomerBasic> dataFileReader = new DataFileReader<>(basicCustomerFile, datumReader)) {
            while (dataFileReader.hasNext()) {
                CustomerBasic customer = dataFileReader.next();
                log.info("Read Specific avro schema from file : {}", customer);
                log.info("Customer First Name : {}" , customer.getFirstName());
                Float height = customer.getHeight();
                log.info("Customers Height : {}", height);
            }
        } catch (IOException ioException) {
            System.err.println("Exception occurred while Reading Generic Record from a file");
            ioException.printStackTrace(System.err);
        }
    }
}
