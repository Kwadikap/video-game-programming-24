public class SoundManager {
    Sound sound;

    public SoundManager() {
        sound = new Sound();
    }

    public void playBackgroundMusic() {
        sound.setFile(0);
        sound.play();
        sound.loop();
    }

    public void stopBackgroundMusic() {
        sound.stop();
    }

    public void setSound(int i) {
        sound.setFile(i);
        sound.play();
    }

    public void playGameOverSE() {
        setSound(1);
    }

    public void playHitSE() {
        setSound(2);
    }

}
