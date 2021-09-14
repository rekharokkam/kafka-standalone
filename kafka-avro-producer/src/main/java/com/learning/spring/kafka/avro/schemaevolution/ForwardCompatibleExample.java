package com.learning.spring.kafka.avro.schemaevolution;

import com.learning.spring.kafka.avro.Customer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;

@Slf4j
public class ForwardCompatibleExample {

    public static void main(String[] args) {
        //Create the CustomerV1 Record
        Customer customerV2 = Customer.newBuilder()
                .setFirstName("John")
                .setLastName("Doe")
                .setAge(55)
                .setHeight(174.3f)
                .setWeight(70.34f)
//                .setPhoneNumber("123-456-7890")
                .build();

        log.info("Customer V2 record created : {}", customerV2);

        //Write to a file
        final DatumWriter<Customer> datumWriter = new SpecificDatumWriter<>(Customer.class);
        try (DataFileWriter<Customer> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(customerV2.getSchema(), new File("customerV2.avro"));
            dataFileWriter.append(customerV2);
            log.info("avro schema successfully created for Customer V2 Record");
        } catch (IOException ioException) {
            System.err.println("Exception occurred while writing Customer V2 Record to a file");
            ioException.printStackTrace(System.err);
        }
    }

}
