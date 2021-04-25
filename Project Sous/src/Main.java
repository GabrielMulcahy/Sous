
/**
 * @author Gabriel Mulcahy
 * @since 05-10-2019
 */
public class Main {

    public static void main(String[] args) {



        SoundPlayer sp = new SoundPlayer();
        sp.playSound("res/startup.wav");

        VoiceAssistant sous = new VoiceAssistant();
        Thread thread1 = new Thread(sous);
        thread1.start();

    }

}





