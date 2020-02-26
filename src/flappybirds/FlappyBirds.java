package flappybirds;

import pkg2dgamesframework.AFrameOnImage;
import pkg2dgamesframework.Animation;
import pkg2dgamesframework.GameScreen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class FlappyBirds extends GameScreen {

    private BufferedImage birds;
    private Animation bird_anim;


    private Bird bird;

    private Ground ground;
    private ChimneyGroup chimneyGroup;

    private int Point = 0;

    private int BEGIN_SCREEN = 0;
    private int GAMEPLAY_SCREEN = 1;
    private int GAMEOVER_SCREEN = 2;

    private int CurrentScreen = BEGIN_SCREEN;



    // trong luc
    public static float g = 0.15f;

    public FlappyBirds(){
        super(800,600);

        try {
            birds = ImageIO.read(new File("Assets/bird_sprite.png"));

        } catch (IOException e) { }

        bird_anim = new Animation(70);
        AFrameOnImage f;
        f = new AFrameOnImage(0,0,60,60);
        bird_anim.AddFrame(f);
        f = new AFrameOnImage(60,0,60,60);
        bird_anim.AddFrame(f);
        f = new AFrameOnImage(120,0,60,60);
        bird_anim.AddFrame(f);
        f = new AFrameOnImage(60,0,60,60);
        bird_anim.AddFrame(f);

        bird = new Bird(350,250,50,50);
        ground = new Ground();
        chimneyGroup = new ChimneyGroup();

        BeginGame();
    }

    public static void main(String[] args) {

        new FlappyBirds();

    }

    private void resetGame(){
        bird.setPos(350,250);
        bird.setVt(0);
        bird.setLive(true);
        Point = 0;
        chimneyGroup.resetChimneys();
    }

    @Override
    public void GAME_UPDATE(long deltaTime) {

        if(CurrentScreen == BEGIN_SCREEN){
            resetGame();
        }else if(CurrentScreen == GAMEPLAY_SCREEN){

            if (bird.getLive()) bird_anim.Update_Me(deltaTime);
            bird.update(deltaTime);
            ground.Update();

            chimneyGroup.Update();

            for (int i = 0; i < ChimneyGroup.SIZE; i++){

                if(bird.getRect().intersects(chimneyGroup.getChimney(i).getRect())){
                    if (bird.getLive()) bird.bupSound.play();
                    bird.setLive(false);
                    System.out.println("Chim con song " + bird.getLive());
                }

            }

            // Tang diem khi qua mot ong khoi neu ong khoi do co toa do sau con chim va ong khoi do chua duoc setIsBeHind bang true
            for (int i = 0; i < ChimneyGroup.SIZE; i++){
                if( bird.getPosX() > chimneyGroup.getChimney(i).getPosX() && !chimneyGroup.getChimney(i).getIsBeHindBird()
                && i % 2 == 0){
                    Point ++;
                    bird.getMoneySound.play();
                    chimneyGroup.getChimney(i).setIsBeHindBird(true);
                }
//                if( bird.getPosX() > chimneyGroup.getChimney(i).getPosX() && !chimneyGroup.getChimney(i).getIsBeHindBird()
//                        ){
//                    System.out.println("Tinh trang cot: " + chimneyGroup.getChimney(i).getIsBeHindBird());
//                }
            }

            // Chim va cham Ground => Game Over
            if(bird.getPosY() + bird.getH() > ground.getYGround()){
                bird.bupSound.play();
                CurrentScreen = GAMEOVER_SCREEN;
            }
            // Chim va cham chimeny => Game Over
            if(!bird.getLive()) CurrentScreen = GAMEOVER_SCREEN;

        }else {



        }

    }

    @Override
    public void GAME_PAINT(Graphics2D g2) {

        g2.setColor(Color.decode("#b8daef"));
        g2.fillRect(0,0,MASTER_WIDTH,MASTER_HEIGHT);

        chimneyGroup.Paint(g2);

        ground.Paint(g2);

//        cnObject.Paint(g2);

        if (bird.getIsFlying())
            bird_anim.PaintAnims((int) bird.getPosX(),(int) bird.getPosY(),birds,g2,0,-1);
        else
            bird_anim.PaintAnims((int) bird.getPosX(),(int) bird.getPosY(),birds,g2,0,0);



        if(CurrentScreen == BEGIN_SCREEN){
            g2.setColor(Color.red);
            g2.drawString("Press space to play game",200,300);
        }

        if(CurrentScreen == GAMEOVER_SCREEN){
            g2.setColor(Color.red);
            g2.drawString("Press space to turn back begin screen",200,300);
        }

        g2.setColor(Color.red);
        g2.drawString("Point: " + Point, 20, 50);
    }

    @Override
    public void KEY_ACTION(KeyEvent e, int Event) {

        if(Event == KEY_PRESSED ){

            //Nhan nut bat dau tro choi
            if(CurrentScreen == BEGIN_SCREEN){

                CurrentScreen = GAMEPLAY_SCREEN;

            }else if(CurrentScreen == GAMEPLAY_SCREEN){
                // Khi chim chet sau va cham thi co nhan space chim cung khong bay nua
                if (bird.getLive()) bird.fly();
            }else if(CurrentScreen == GAMEOVER_SCREEN){
                CurrentScreen = BEGIN_SCREEN;
            }


        }

    }
}
