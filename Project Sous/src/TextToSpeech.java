// Imports the Google Cloud client library
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * edited example program provided by Google to produce TTS
 * @author Gabriel Mulcahy, Google
 * @since 13-02-2019
 */
public class TextToSpeech {

    private SoundPlayer sp = new SoundPlayer();

    /**
     * takes a String, converts the text to synthesised audio and plays it to the system audio
     * @param t text to be converted
     */
    public void say(String t) {

        String text = t.replace("_"," ");
        // Instantiates a client
        TextToSpeechClient textToSpeechClient = null;
        try {
            textToSpeechClient = TextToSpeechClient.create();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Set the text input to be synthesized
    SynthesisInput input = SynthesisInput.newBuilder()
            .setText(text)
            .build();

    // Build the voice request, select the language code ("en-GB") and the ssml voice gender
    // ("neutral")
    VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
            .setLanguageCode("en-GB")
            .setSsmlGender(SsmlVoiceGender.FEMALE)
            .build();

    // Select the type of audio file you want returned
    AudioConfig audioConfig = AudioConfig.newBuilder()
            .setAudioEncoding(AudioEncoding.LINEAR16) //wav format
            .build();

    // Perform the text-to-speech request on the text input with the selected voice parameters and
    // audio file type
    SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
            audioConfig);

    // Get the audio contents from the response
    ByteString audioContents = response.getAudioContent();

    // Write the response to the output file.
        OutputStream out = null;
        try {
            out = new FileOutputStream("output.wav");
        } catch (FileNotFoundException e) {
            System.err.println("File Not Found: " + e.getMessage());
        }
        try {
            out.write(audioContents.toByteArray());
        } catch (IOException e) {
            System.err.println("IO " + e.getMessage());
        }
        System.out.println("Audio content written to file \"output.wav\"");
        sp.playSound();

    }
}
