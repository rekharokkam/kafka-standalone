package com.learning.spring.kafka.avro.schemaevolution;

import com.learning.spring.kafka.avro.CustomerV1;
import com.learning.spring.kafka.avro.CustomerV2;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;

@Slf4j
public class ForwardCompatibleExample {

    public static void main(String[] args) {
        //Create the CustomerV1 Record
        CustomerV2 customerV2 = CustomerV2.newBuilder()
                .setFirstName("John")
                .setLastName("Doe")
                .setAge(55)
                .setHeight(174.3f)
                .setWeight(70.34f)
                .setPhoneNumber("123-456-7890")
                .build();

        log.info("Customer V2 record created : {}", customerV2);

        //Write to a file
        final DatumWriter<CustomerV2> datumWriter = new SpecificDatumWriter<>(CustomerV2.class);
        try (DataFileWriter<CustomerV2> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(customerV2.getSchema(), new File("customerV2.avro"));
            dataFileWriter.append(customerV2);
            log.info("avro schema successfully created for Customer V2 Record");
        } catch (IOException ioException) {
            System.err.println("Exception occurred while writing Customer V2 Record to a file");
            ioException.printStackTrace(System.err);
        }

        //Read the CustomerV2 data with Customer V1 Schema providing forward compatibility
        final File customerV2AvroFile = new File("customerV2.avro");
        final DatumReader<CustomerV1> datumReader = new SpecificDatumReader<>(CustomerV1.class);
        try (DataFileReader<CustomerV1> dataFileReader = new DataFileReader<>(customerV2AvroFile, datumReader)) {
            while (dataFileReader.hasNext()) {
                CustomerV1 customer = dataFileReader.next();
                log.info("Read Forward Compatible : CustomerV2 file and build CustomerV1 : {}", customer);
            }
        } catch (IOException ioException) {
            System.err.println("Exception occurred while Reading Forward Compatible Record from a file");
            ioException.printStackTrace(System.err);
        }

    }

}
