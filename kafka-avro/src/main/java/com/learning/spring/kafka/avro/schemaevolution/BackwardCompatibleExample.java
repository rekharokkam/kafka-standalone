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
public class BackwardCompatibleExample {

    public static void main(String[] args) {
        //Create the CustomerV1 Record
        CustomerV1 customerV1 = CustomerV1.newBuilder()
                .setFirstName("Vidyut")
                .setLastName("Jammwal")
                .setAge(35)
                .setHeight(154.3f)
                .setWeight(65.34f)
                .setAutomatedEmail(false)
                .build();

        log.info("Customer V1 record created : {}", customerV1);

        //Write to a file
        final DatumWriter<CustomerV1> datumWriter = new SpecificDatumWriter<>(CustomerV1.class);
        try (DataFileWriter<CustomerV1> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(customerV1.getSchema(), new File("customerV1.avro"));
            dataFileWriter.append(customerV1);
            log.info("avro schema successfully created for Customer V1 Record");
        } catch (IOException ioException) {
            System.err.println("Exception occurred while writing Customer V1 Record to a file");
            ioException.printStackTrace(System.err);
        }

        //Read the CustomerV1 data with Customer V2 Schema providing backward compatibility
        final File customerV1AvroFile = new File("customerV1.avro");
        final DatumReader<CustomerV2> datumReader = new SpecificDatumReader<>(CustomerV2.class);
        try (DataFileReader<CustomerV2> dataFileReader = new DataFileReader<>(customerV1AvroFile, datumReader)) {
            while (dataFileReader.hasNext()) {
                CustomerV2 customer = dataFileReader.next();
                log.info("Read Backward Compatible : CustomerV1 file and build CustomerV2 : {}", customer);
            }
        } catch (IOException ioException) {
            System.err.println("Exception occurred while Reading Backward Compatible Record from a file");
            ioException.printStackTrace(System.err);
        }

    }

}
