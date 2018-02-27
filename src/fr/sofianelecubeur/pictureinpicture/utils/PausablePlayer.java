package fr.sofianelecubeur.pictureinpicture.utils;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public class PausablePlayer {

    public final static int NOTSTARTED = 0;
    public final static int PLAYING = 1;
    public final static int PAUSED = 2;
    public final static int FINISHED = 3;

    private final Player player;
    private final AudioDevice device;

    private final Object playerLock = new Object();

    private int playerStatus = NOTSTARTED, skippedFrames = 0;
    private Runnable onFinish;

    public PausablePlayer(final InputStream inputStream) throws JavaLayerException {
        this(inputStream, FactoryRegistry.systemRegistry().createAudioDevice());
    }

    public PausablePlayer(final InputStream inputStream, final AudioDevice audioDevice) throws JavaLayerException {
        this.player = new Player(inputStream, audioDevice);
        this.device = audioDevice;
    }

    /**
     * Starts playback (resumes if paused)
     */
    public void play() {
        synchronized (playerLock) {
            switch (playerStatus) {
                case NOTSTARTED:
                    final Runnable r = () -> playInternal();
                    final Thread t = new Thread(r);
                    t.setDaemon(true);
                    t.setPriority(Thread.MAX_PRIORITY);
                    playerStatus = PLAYING;
                    t.start();
                    break;
                case PAUSED:
                    resume();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Pauses playback. Returns true if new state is PAUSED.
     */
    public boolean pause() {
        synchronized (playerLock) {
            if (playerStatus == PLAYING) {
                playerStatus = PAUSED;
            }
            return playerStatus == PAUSED;
        }
    }

    /**
     * Resumes playback. Returns true if the new state is PLAYING.
     */
    public boolean resume() {
        synchronized (playerLock) {
            if (playerStatus == PAUSED) {
                playerStatus = PLAYING;
                playerLock.notifyAll();
            }
            return playerStatus == PLAYING;
        }
    }

    /**
     * Stops playback. If not playing, does nothing
     */
    public void stop() {
        synchronized (playerLock) {
            playerStatus = FINISHED;
            playerLock.notifyAll();
        }
    }

    private void playInternal() {
        while (playerStatus != FINISHED) {
            try {
                if (!player.play(1)) {
                    break;
                }
            } catch (final JavaLayerException e) {
                break;
            }
            // check if paused or terminated
            synchronized (playerLock) {
                while (playerStatus == PAUSED) {
                    try {
                        playerLock.wait();
                    } catch (final InterruptedException e) {
                        // terminate player
                        break;
                    }
                }
            }
        }
        close();
        if(onFinish != null){
            onFinish.run();
        }
    }

    /**
     * Closes the player, regardless of current state.
     */
    public void close() {
        synchronized (playerLock) {
            playerStatus = FINISHED;
        }
        try {
            player.close();
        } catch (final Exception e) {
            // ignore, we are terminating anyway
        }
    }

    public float getPosition(){
        return device.getPosition() + skippedFrames;
    }

    public int getState(){
        return playerStatus;
    }

    public void setOnFinishListener(Runnable onFinish) {
        this.onFinish = onFinish;
    }

    public void skip(long frames){
        try {
            int old = this.playerStatus;
            pause();
            Bitstream bitstream = getBitstream();
            if(checkClosed(bitstream)){
                if(old == PLAYING) {
                    resume();
                }
                return;
            }
            float mspf = 0f;
            if(frames < 0){
                /*for (int i = 0; i < Math.abs(frames); i++) {
                    bitstream.unreadFrame();
                }
                Header header = bitstream.readFrame();
                while(header == null){
                    header = bitstream.readFrame();
                }
                mspf = header.ms_per_frame();
                skippedFrames -= Math.abs(frames) * mspf;*/
            } else {
                for (int i = 0; i < frames; i++) {
                    Header header = bitstream.readFrame();
                    if (header != null) {
                        bitstream.closeFrame();
                        if (mspf == 0) {
                            mspf = header.ms_per_frame();
                        }
                    }
                }
                skippedFrames += frames * mspf;
            }
            if(old == PLAYING) {
                resume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long millisToFrames(long millis){
        long frames = 0;
        try {
            Bitstream bitstream = getBitstream();
            Header header = bitstream.readFrame();
            while(header == null){
                header = bitstream.readFrame();
            }
            float mspf = header.ms_per_frame();
            frames = millis / (long)mspf;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return frames;
    }

    protected boolean checkClosed(Bitstream bitstream){
        try {
            bitstream.readFrame();
            return false;
        } catch (Exception e) {}
        return true;
    }

    public static long getSoundDuration(FileInputStream in){
        Header h = null;
        Bitstream bitstream = new Bitstream(in);
        try {
            h = bitstream.readFrame();
        } catch (BitstreamException e) {
            e.printStackTrace();
        }
        long tn = 0;
        try {
            tn = in.getChannel().size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (h != null ? (long)h.total_ms((int)tn) : 0);
    }

    protected Bitstream getBitstream() throws Exception {
        Field field = player.getClass().getDeclaredField("bitstream");
        field.setAccessible(true);
        return (Bitstream)field.get(player);
    }
}