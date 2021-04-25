// Imports the Google Cloud client library
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * edited example program provided by Google to produce STT
 * @author Gabriel Mulcahy, Google
 * @since 01/02/2019
 */
public class SpeechToText {

    private VoiceDetector vd = new VoiceDetector();

    /**
     * takes audio input from microphone and returns text of words spoken
     * waits for audio to be detected/trigger
     * @return String words spoken
     */
    public String getText() {


        String text = "";
        // record until speech is detected
        while (text.replace(" ","").equals("")){
            // wait for voice to be detected
            vd.detectVoice();

            JavaSoundRecorder jsr = new JavaSoundRecorder();
            jsr.record();

            // Instantiates a client
            SpeechClient speechClient = null;
            try {
                speechClient = SpeechClient.create();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // The path to the audio file to transcribe
            String fileName = "input.wav";

            // Reads the audio file into memory
            Path path = Paths.get(fileName);
            byte[] data = new byte[0];
            try {
                data = Files.readAllBytes(path);
            } catch (IOException e) {
                System.err.println("IO: " + e.getMessage());
            }
            ByteString audioBytes = ByteString.copyFrom(data);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.LINEAR16)
                    .setLanguageCode("en-GB")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Performs speech recognition on the audio file
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            //if the user pauses, more than one result may be produced, so we now have to concatenate them
            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                text = text + alternative.getTranscript();
            }
        }
        // all recipe recipe information has not uppercase, this avoids a conflict
        System.out.println(text);
        return text.toLowerCase();
    }

    /**
     * takes audio input from microphone and returns text of words spoken
     * does not wait for user to start talking
     * to be used when Sous asks a question
     * @return String words spoken
     */
    public String getResponse() {


        String text = "";
        // record until speech is detected
        while (text.replace(" ","").equals("")){

            JavaSoundRecorder jsr = new JavaSoundRecorder();
            jsr.record();

            // Instantiates a client
            SpeechClient speechClient = null;
            try {
                speechClient = SpeechClient.create();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // The path to the audio file to transcribe
            String fileName = "input.wav";

            // Reads the audio file into memory
            Path path = Paths.get(fileName);
            byte[] data = new byte[0];
            try {
                data = Files.readAllBytes(path);
            } catch (IOException e) {
                System.err.println("IO: " + e.getMessage());
            }
            ByteString audioBytes = ByteString.copyFrom(data);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.LINEAR16)
                    .setLanguageCode("en-GB")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Performs speech recognition on the audio file
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            //if the user pauses, more than one result may be produced, so we now have to concatenate them
            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                text = text + alternative.getTranscript();
            }
        }
        // all recipe recipe information has not uppercase, this avoids a conflict
        System.out.println(text);
        return text.toLowerCase();
    }


}
