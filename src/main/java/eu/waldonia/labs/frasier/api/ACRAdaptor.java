package eu.waldonia.labs.frasier.api;

import com.acrcloud.utils.ACRCloudRecognizer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author sih
 */
@Service
public class ACRAdaptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ACRAdaptor.class);

    @Autowired
    private ACRCloudRecognizer recognizer;

    SimpleResult recognize(byte[] inputSnippet) {

        SimpleResult result = new SimpleResult();

        String acrResult = recognizer.recognizeByFileBuffer(inputSnippet, inputSnippet.length, 0);

        ObjectMapper mapper = new ObjectMapper();
        try {

            LOGGER.info(acrResult);

            JsonNode root = mapper.readTree(acrResult);
            JsonNode status = root.get("status").get("msg");
            JsonNode album = root.get("metadata").get("music").get(0).get("album").get("name");
            JsonNode title = root.get("metadata").get("music").get(0).get("title");

            result.setAlbum(album.textValue());
            result.setTrack(title.textValue());
            result.setStatus(status.textValue());

        }
        catch (IOException ioe) {
            LOGGER.error(ioe.getMessage());
        }




        return result;
    }

/*

    public static void main(String[] args) {
    	Map<String, Object> config = new HashMap<String, Object>();
        config.put("access_key", "XXXXXX");
        config.put("access_secret", "XXXXXX");
        config.put("debug", false);
        config.put("timeout", 5);

        ACRCloudRecognizer re = new ACRCloudRecognizer(config);
        //String result = re.recognizeByFile(args[0], 80);
        //System.out.println(result);

        //System.out.println(ACRCloudExtrTool.getDoc());
        //ACRCloudExtrTool.setDebug();
        //byte[] fp = ACRCloudExtrTool.createFingerprintByFile(args[0], 30, 12, false);
        //System.out.println(fp.length);
        //byte[] fp = ACRCloudExtrTool.decodeAudioByFile(args[0], 200, 0);
        //System.out.println(fp.length);

        File file = new File(args[0]);
        byte[] buffer = new byte[3 * 1024 * 1024];
        if (!file.exists()) {
            return;
        }
        FileInputStream fin = null;
        int bufferLen = 0;
        try {
            fin = new FileInputStream(file);
            bufferLen = fin.read(buffer, 0, buffer.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                	fin.close();
                }
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        System.out.println("bufferLen=" + bufferLen);

        if (bufferLen <= 0)
            return;

        String result = re.recognizeByFileBuffer(buffer, bufferLen, 80);
        System.out.println(result);

        //byte[] fp = ACRCloudExtrTool.decodeAudioByBuffer(buffer, bufferLen, 200, 0);
        //System.out.println(fp.length);

        //byte[] fp = ACRCloudExtrTool.createFingerprintByBuffer(buffer, bufferLen, 180, 20, false);
        //System.out.println(fp.length);
    }
 */




}
