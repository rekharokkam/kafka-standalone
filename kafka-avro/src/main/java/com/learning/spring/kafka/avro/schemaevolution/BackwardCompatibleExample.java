package com.learning.spring.kafka.avro.schemaevolution;

import com.learning.spring.kafka.avro.Customer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.File;
import java.io.IOException;

@Slf4j
public class BackwardCompatibleExample {

    public static void main(String[] args) {

        //Read the CustomerV1 data with Customer V2 Schema providing backward compatibility
        final File customerV1AvroFile = new File("customerV1.avro");
        final DatumReader<Customer> datumReader = new SpecificDatumReader<>(Customer.class);
        try (DataFileReader<Customer> dataFileReader = new DataFileReader<>(customerV1AvroFile, datumReader)) {
            while (dataFileReader.hasNext()) {
                Customer customerV2 = dataFileReader.next();
                log.info("Read Backward Compatible : CustomerV1 file and build CustomerV2 : {}", customerV2);
            }
        } catch (IOException ioException) {
            System.err.println("Exception occurred while Reading Backward Compatible Record from a file");
            ioException.printStackTrace(System.err);
        }

    }

}
