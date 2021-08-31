package com.learning.spring.kafka.avro;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class SpecificRecordCustomerComplexExample {

    public static void main(String[] args) {

        //Create SpecificRecord for CustomerComplex
        CustomerComplex.Builder customerComplexBuilder = CustomerComplex.newBuilder();
        customerComplexBuilder.setFirstName("Vidyut");
        customerComplexBuilder.setLastName("Jammwal");
        customerComplexBuilder.setAge(35);
        customerComplexBuilder.setHeight(160.75f);
        customerComplexBuilder.setWeight(110.00f);
        customerComplexBuilder.setAutomatedEmail(false);
        customerComplexBuilder.setCustomerSignupTs(Instant.now());
        customerComplexBuilder.setSalary("2000.89");
//        customerComplexBuilder.setCustomerEmails(Arrays.asList("example@example.com"));


        CustomerAddress.Builder customerAddressBuilder = CustomerAddress.newBuilder();
        customerAddressBuilder.setAddress("someStreet");
        customerAddressBuilder.setAddressType(AddressType.RESIDENTIAL);
        customerAddressBuilder.setCity("someCity");
        customerAddressBuilder.setPostcode("someCode");
        CustomerAddress customerAddress = customerAddressBuilder.build();

//        customerComplexBuilder.setCustomerAddress(customerAddress);

        CustomerComplex customerComplex = customerComplexBuilder.build();
        log.info("Specific CustomerComplex built from Specific Record : {}", customerComplex);

        //Write to a file
        final DatumWriter<CustomerComplex> datumWriter = new SpecificDatumWriter<>(CustomerComplex.class);
        try (DataFileWriter<CustomerComplex> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(customerComplex.getSchema(), new File("customer-complex-specific-generated.avro"));
            dataFileWriter.append(customerComplex);
            log.info("avro schema successfully created from Specific Record for Customer Complex");
        } catch (IOException ioException) {
            System.err.println("Exception occurred while writing Specific Record to a file");
            ioException.printStackTrace(System.err);
        }

        //Read from a file
        final File complexCustomerFile = new File("customer-complex-specific-generated.avro");
        final DatumReader<CustomerComplex> datumReader = new SpecificDatumReader<>();
        try (DataFileReader<CustomerComplex> dataFileReader = new DataFileReader<>(complexCustomerFile, datumReader)) {
            while (dataFileReader.hasNext()) {
                CustomerComplex customer = dataFileReader.next();
                log.info("Read Specific avro schema from file : {}", customer);
                log.info("Customer First Name : {}" , customer.getFirstName());
                Float height = customer.getHeight();
                log.info("Customers Height : {}", height);
            }
        } catch (IOException ioException) {
            System.err.println("Exception occurred while Reading Specific Record from a file");
            ioException.printStackTrace(System.err);
        }
    }
}
