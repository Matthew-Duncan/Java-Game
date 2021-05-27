package game2D;

//By 2717790

import java.io.*;
import javax.sound.sampled.*;

public class SoundFilter extends Thread {

	String filename; // The name of the file to play
	boolean finished; // A flag showing that the thread has finished

	public SoundFilter(String fname) {
		filename = fname;
		finished = false;
	}

	/**
	 * run will play the actual sound but you should not call it directly. You need
	 * to call the 'start' method of your sound object (inherited from Thread, you
	 * do not need to declare your own). 'run' will eventually be called by 'start'
	 * when it has been scheduled by the process scheduler.
	 */
	public void run() {
		// Copy of the sound class due to a bug, the filter would be applied no matter
		// what, to avoid this I have created a copy that applies the filter.
		try {
			File file = new File(filename);
			AudioInputStream stream = AudioSystem.getAudioInputStream(file);
			AudioFormat format = stream.getFormat();
			Filter filtered = new Filter(stream);
			stream = new AudioInputStream(filtered, format, stream.getFrameLength());
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();
			Thread.sleep(100);
			while (clip.isRunning()) {
				Thread.sleep(100);
			}
			clip.close();
		} catch (Exception e) {
		}
		finished = true;

	}
}
