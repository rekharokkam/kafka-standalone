package com.learning.spring.kafka.avro.schemaevolution;

import com.learning.spring.kafka.avro.Customer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;

@Slf4j
public class BackwardCompatibleExample {

//    public static void main(String[] args) {
////        Create the CustomerV1 Record
//        Customer customerv1 = Customer.newBuilder()
//                .setFirstName("Vidyut")
//                .setLastName("Jammwal")
//                .setAge(35)
//                .setHeight(154.3f)
//                .setWeight(65.34f)
//                .setAutomatedEmail(false)
//                .build();
//
//        log.info("Customer V1 record created : {}", customerv1);
//
//        //Write to a file
//        final DatumWriter<Customer> datumWriter = new SpecificDatumWriter<>(Customer.class);
//        try (DataFileWriter<Customer> dataFileWriter = new DataFileWriter<>(datumWriter)) {
//            dataFileWriter.create(customerv1.getSchema(), new File("customerV1.avro"));
//            dataFileWriter.append(customerv1);
//            log.info("avro schema successfully created for Customer Version1 Record");
//        } catch (IOException ioException) {
//            System.err.println("Exception occurred while writing Customer V1 Record to a file");
//            ioException.printStackTrace(System.err);
//        }
//
//    }

}
