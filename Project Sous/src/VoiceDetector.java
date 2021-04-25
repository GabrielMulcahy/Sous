
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * responsible for detecting changes in audio level
 * representing the user starting to speak
 * @author Gabriel Mulcahy
 * @since 08-04-2019
 */
public class VoiceDetector {

    private AudioFormat af;
    private final double THRESHOLD = 0.09;


    VoiceDetector(){
        File file = new File("input.wav");
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            af = stream.getFormat();
        } catch (UnsupportedAudioFileException | IOException e) {
            System.err.println("Failed to create VoiceDetector: " + e.getMessage());
            System.exit(-1);
        }
    }


    /**
     * establishes a data line to the default audio input device
     * repeatedly calls findLevel until the level of audio crosses the threshold
     */
    public void detectVoice() {
        try {
            // the data line mus be established here, and not in the constructor
            // it needs to go out of scope upon completion of the method
            // this is to allow the sound recorder to establish its own data line
            DataLine.Info dli = new DataLine.Info(TargetDataLine.class, af);
            final TargetDataLine tdl = (TargetDataLine) AudioSystem.getLine(dli);
            tdl.open(af);
            tdl.start();

            int buffSize = (int) af.getSampleRate() * af.getFrameSize();
            byte[] buffer = new byte[buffSize];

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while (true) {
                int size = tdl.read(buffer, 0, buffer.length);
                // determine if the volume of the audio is above the given threshold
                if (findLevel(buffer) >= THRESHOLD) {
                    break;
                }
                if (size > 0) baos.write(buffer, 0, size);
            }
            tdl.close();
        } catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e.getMessage());
            System.exit(-1);
        }
    }


    /**
     * determines volume of audio
     * @param buffer byte array containing audio data
     * @return float representing volume of audio
     */
    private float findLevel (byte[] buffer) {
        int peak = 0;
        // increasing the incrementing of i speeds up the process but reduces accuracy
        for (int i=0; i<buffer.length; i+=2) {
            // determine current value of volume
            int high  = buffer[i+1];
            int low   = buffer [i];
            int value = ((high << 8) | (byte) low);
            // find the highest value so far
            peak      = Math.max(peak, value);
        }
        // normalise values between 0.0 and 1.0
        return (float) peak / Short.MAX_VALUE;
    }
}