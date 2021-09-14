package com.learning.spring.kafka.avro.schemaevolution;

import com.learning.spring.kafka.avro.Customer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.File;
import java.io.IOException;

@Slf4j
public class ForwardCompatibleExample {

    public static void main(String[] args) {

        //Read the CustomerV2 data with Customer V1 Schema providing forward compatibility
        final File customerV2AvroFile = new File("customerV2.avro");
        final DatumReader<Customer> datumReader = new SpecificDatumReader<>(Customer.class);
        try (DataFileReader<Customer> dataFileReader = new DataFileReader<>(customerV2AvroFile, datumReader)) {
            while (dataFileReader.hasNext()) {
                Customer customerV1 = dataFileReader.next();
                log.info("Read Forward Compatible : CustomerV1 file and build CustomerV1 : {}", customerV1);
            }
        } catch (IOException ioException) {
            System.err.println("Exception occurred while Reading Forward Compatible Record from a file");
            ioException.printStackTrace(System.err);
        }

    }

}
