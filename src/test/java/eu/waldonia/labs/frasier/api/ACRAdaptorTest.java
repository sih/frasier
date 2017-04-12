package eu.waldonia.labs.frasier.api;

import eu.waldonia.labs.frasier.FrasierApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author sih
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(FrasierApplication.class)
public class ACRAdaptorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ACRAdaptorTest.class);

    @Value("${test.db.mp3}")
    private String dbmp3;

    @Value("${test.via-phone.m4a}")
    private String viaPhoneM4a;


    @Autowired
    private ACRAdaptor adaptor;

    private byte[] dbSnippet;
    private byte[] viaPhoneSnippet;

    @Before
    public void setUp() throws IOException {

        LOGGER.info(System.getProperty("java.library.path"));

        String workingDir = System.getProperty("user.dir")+"/src/test/resources";
        LOGGER.info(workingDir);

        dbSnippet = Files.readAllBytes(Paths.get(workingDir, dbmp3));

    }

    @Test
    public void recognize() throws Exception {
        adaptor.recognize(dbSnippet);
    }

}