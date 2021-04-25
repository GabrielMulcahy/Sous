
import java.io.File;
import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 * plays local audio files
 * modified example code provided for ECM2415
 * @author David Wakeling, Gabriel Mulcahy
 * @since 13-11-2018
 */
public class SoundPlayer
{
    private final static String FILENAME = "output.wav";

    /*
     * Set up stream.
     */
    private static AudioInputStream setupStream(String name)
    {
        try
        {
            File file = new File(name);
            System.out.println(file.getCanonicalPath());
            AudioInputStream stm = AudioSystem.getAudioInputStream(file);
            return stm;
        }
        catch (Exception e)
        {
            System.err.println("Error establishing stream: " + e.getMessage());
            return null;
        }
    }

    /*
     * Read stream.
     */
    private static ByteArrayOutputStream readStream(AudioInputStream stm)
    {
        try
        {
            AudioFormat af  = stm.getFormat();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int bufferSize = (int) af.getSampleRate() * af.getFrameSize();
            byte buffer[] = new byte[bufferSize];

            for ( ; ; )
            {
                int n = stm.read( buffer, 0, buffer.length );
                if ( n > 0 )
                {
                    bos.write( buffer, 0, n );
                }
                else
                {
                    break;
                }
            }
            return bos;
        }
        catch (Exception e)
        {
            System.err.println("Error reading stream: " + e.getMessage());
            return null;
        }
    }

    /*
     * Play stream.
     */
    private static void playStream( AudioInputStream stm, ByteArrayOutputStream bos )
    {
        try
        {
            AudioFormat    af   = stm.getFormat();
            byte[]         ba   = bos.toByteArray();
            DataLine.Info  info = new DataLine.Info(SourceDataLine.class, af);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

            line.open(af);
            line.start();
            line.write(ba, 0, ba.length);
        }
        catch (Exception e)
        {
            System.err.println("Error playing audio: " + e.getMessage());
        }
    }


    /*
     * Uses the above methods to set up and play an audio file in one go.
     */
    public static void playSound()
    {
        AudioInputStream stm = setupStream(FILENAME);
        playStream(stm, readStream(stm));
    }

    public static void playSound(String filename) {
        AudioInputStream stm = setupStream(filename);
        playStream(stm, readStream(stm));
    }

    /*
     * Method designed to play files that diagnose errors.
     */
    public static void playError(String file) //Extra parameter as it can't use the final "FILE" variable.
    {
        AudioInputStream stm = setupStream(file);
        playStream(stm, readStream(stm));
    }
}
