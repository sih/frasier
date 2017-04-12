package eu.waldonia.labs.frasier.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;

/**
 * @author sih
 */
@RestController
@RequestMapping(value="/frasier")
@MultipartConfig(fileSizeThreshold = 1*1024*1024) // 1MB threshold
public class RecognizerController {

    @Autowired
    private ACRAdaptor adaptor;

    private static final Logger LOGGER = LoggerFactory.getLogger(RecognizerController.class);

    @RequestMapping(value="/identify", method = RequestMethod.POST)
    public void identify(@RequestParam("snippet") MultipartFile snippet) {

    }

}
