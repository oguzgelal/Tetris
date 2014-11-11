import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.signals.*; 
import ddf.minim.analysis.*; 
import ddf.minim.effects.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class tetris extends PApplet {






Grid grid;
GamePlay gp;
ArrayList<int[]> actives = new ArrayList<int[]>();
PImage bg;
PImage enterscreen;
PImage lshape;
PImage llshape;
PImage zshape;
PImage zzshape;
PImage sshape;
PImage ishape;
PImage tshape;
PFont font;
int gamescreen = 0; // 0 : men\u00fc, 1 : oyun, 2 : gameover

Minim minim;
AudioPlayer bgaudio;

public void setup() {
  size(350, 420, P2D);
  grid = new Grid();
  gp = new GamePlay();
  bg = loadImage("img/bg.jpg");
  enterscreen = loadImage("img/enter.jpg");
  font = loadFont("data/font.vlw");
  lshape = loadImage("img/lshape.png");
  llshape = loadImage("img/llshape.png");
  zshape = loadImage("img/zshape.png");
  zzshape = loadImage("img/zzshape.png");
  sshape = loadImage("img/sshape.png");
  ishape = loadImage("img/ishape.png");
  tshape = loadImage("img/tshape.png");
  
  minim = new Minim(this);
  bgaudio = minim.loadFile("tetris.mp3");
  bgaudio.loop();
}

public void draw() {
  if (gamescreen == 0) {
  image(enterscreen, 0, 0);
  fill(0);
  }
  else if (gamescreen == 1) {
    image(bg, 0, 0);
    fill(0);
    
    grid.init();   
    gp.holdBlocks();
    gp.play();
  }
  else if (gamescreen == 2) {
    fill(255, 0, 0);
    textFont(font, 30);
    text("game over", width/2, height/2);
    fill(0);
    textFont(font, 12);
    text("press enter to restart", width/2-8, height/2+15);
    noFill();
  }
}

class Rectangle { // class : rectangle --------------------------------------------------------------------------------------------------------------------------
  int rectstatus; // 1 active , 0 deactive, 2 empty
  int clr;

  public void active() {
    rectstatus = 1;
  }
  public void deactive() {
    rectstatus = 0;
  }
  public void empty() {
    rectstatus = 2;
  }

  public void run(int x, int y, int rectwidth, int rectheight) {
    switch (rectstatus) {
    case 0:
      clr = color(255, 255, 255);
      break;
    case 1:
      clr = color(255, 0, 0);
      break;
    case 2:
      clr = color(200, 200, 200);
      break;
    default:
      clr = color(255, 255, 255);
      break;
    }
    fill(clr);
    smooth();
    rect(x, y, rectwidth, rectheight);
  }
}

class Grid { // class : grid --------------------------------------------------------------------------------------------------------------------------
  int gwidth = 15;
  int swidth = 210;
  int sheight = 420;
  int vertical_gridcount = sheight/gwidth;
  int horizontal_gridcount = swidth/gwidth;
  Rectangle[][] rectangle = new Rectangle[horizontal_gridcount][vertical_gridcount];


  public void init() {
    stroke(200, 200, 200);
    for (int i = 0; i < horizontal_gridcount; i++) {
      for (int j = 0; j < vertical_gridcount; j++) {
        //noStroke();
        rectangle[i][j] = new Rectangle();
        rectangle[i][j].run(i*gwidth, j*gwidth, gwidth, gwidth);
      }
    }
  }
  public void active(int x, int y) {
    if ((x < horizontal_gridcount) && (y < vertical_gridcount)) {
      rectangle[x][y].active();
      rectangle[x][y].run(x*gwidth, y*gwidth, gwidth, gwidth);
    }
  }
  public void deactive(int x, int y) {
    if ((x < horizontal_gridcount) && (y < vertical_gridcount)) {
      rectangle[x][y].deactive();
      rectangle[x][y].run(x*gwidth, y*gwidth, gwidth, gwidth);
    }
  }
  public void empty(int x, int y) {
    if ((x < horizontal_gridcount) && (y < vertical_gridcount)) {
      rectangle[x][y].empty();
      rectangle[x][y].run(x*gwidth, y*gwidth, gwidth, gwidth);
    }
  }
  public boolean isActive(int x, int y) {
    return rectangle[x][y].rectstatus == 1;
  }
  public boolean isDeactive(int x, int y) {
    return rectangle[x][y].rectstatus == 0;
  }
  public boolean isEmpty(int x, int y) {
    return rectangle[x][y].rectstatus == 2;
  }
}

class GamePlay {
  boolean onGoing = true;
  boolean completedrow = true;
  int count_delay = 0;
  boolean goleft = true;
  boolean goright = true;
  boolean atleft = false;
  boolean atright = false;
  int delaytime_mode = 50;
  int shapenum = rnd.length; // \u015fekil say\u0131s\u0131
  //int currentshape = 6;
  int currentshape = (int)random(0, shapenum);
  //int nextshape = 6;
  int nextshape = (int)random(0, shapenum);
  int stagenum = rnd[currentshape].length; // \u015fekillerin a\u015fama say\u0131s\u0131
  int currentstage = (int)random(0, stagenum);
  int countongoing = 0;
  int y_initial = 0;
  boolean turnable = false;

  int score = 0;
  int level = 1;
  int x = (int)random(0, grid.horizontal_gridcount-5);
  int y = y_initial;

  public void restart() {
    actives.clear();
    score = 0;
    level = 1;
    x = (int)random(0, grid.horizontal_gridcount-5);
    y = y_initial;
  }

  public void runBlock(int[][] block, int[][] block_base, int[][] block_left, int[][] block_right, int block_height, int blockno, int[][] places) {
    
    fill(0);
    textAlign(RIGHT);
    textFont(font, 30);
    text(score, 327, 112);
    text (level, 288, 179);
    noFill();

    delaytime_mode = 50 - (3*level);
    if (delaytime_mode <= 0) {
      delaytime_mode = 1;
    }

    if (((score % 25) == 0) && (score != 0)) {
      level = (score/25)+1;
    }

    switch (nextshape) {
    case 0:
      image(lshape, 281, 332);
      break;
    case 1:
      image(llshape, 281, 332);
      break;
    case 2:
      image(sshape, 281, 332);
      break;
    case 3:
      image(zshape, 281, 332);
      break;
    case 4:
      image(zzshape, 281, 332);
      break;
    case 5:
      image(tshape, 281, 332);
      break;
    case 6:
      image(ishape, 281, 332);
      break;
    }

    for (int i = 0; i < block_base.length; i++) {
      if (y+block_base[i][1]+1 == grid.vertical_gridcount) {
        onGoing = false;
        break;
      }
      else {
        if (grid.isActive(x+block_base[i][0], y+block_base[i][1]+1)) {
          onGoing = false;
          break;
        }
      }
    }

    if ((count_delay % 3) == 0) {
      if (keyPressed) {
        if (keyCode == LEFT) {
          for (int i = 0; i < block_left.length; i++) {
            if (x+block_left[i][0] == 0) {
              atleft = true;
              break;
            }
            else {
              atleft = false;
            }
          }
          if (!atleft) {
            for (int i = 0; i < block_left.length; i++) {

              if (grid.isActive(x+block_left[i][0]-1, y+block_left[i][1])) {
                goleft = false;
                break;
              }
              else {
                goleft = true;
              }
            }
            if (goleft) {
              x -= 1;
            }
          }
        }
        else if (keyCode == RIGHT) {
          for (int i = 0; i < block_right.length; i++) {
            if (x + block_right[i][0]+1 == grid.horizontal_gridcount) {
              atright = true;
              break;
            }
            else {
              atright = false;
            }
          }
          if (!atright) {
            for (int i = 0; i < block_right.length; i++) {
              if (grid.isActive(x + block_right[i][0] + 1, y + block_right[i][1])) {
                goright = false;
                break;
              }
              else {
                goright = true;
              }
            }
            if (goright) {
              x += 1;
            }
          }
        }
        else if (keyCode == DOWN) {
          delaytime_mode = 3;
        }
        else if (key == ' ') {
          boolean isnextactive = false;
          int xinstant = x;
          for (int i = 0; i < rnd[currentshape][(currentstage+1)%stagenum][6].length; i++) {
            if ((xinstant+rnd[currentshape][(currentstage+1)%stagenum][6][i][0] >= grid.horizontal_gridcount)) {
              xinstant--;
            }
          }
          for (int i = 0; i < rnd[currentshape][(currentstage+1)%stagenum][6].length; i++) {
            if (y+rnd[currentshape][(currentstage+1)%stagenum][6][i][1] < grid.vertical_gridcount) {
              if (grid.isActive(xinstant+rnd[currentshape][(currentstage+1)%stagenum][6][i][0], y+rnd[currentshape][(currentstage+1)%stagenum][6][i][1])) {
                isnextactive=true;
                break;
              }
              else {
                isnextactive=false;
              }
            }
            else {
              isnextactive = true;
              break;
            }
          }

          if (!isnextactive) {
            x = xinstant;
            delay(100);
            currentstage = (currentstage+1)%stagenum;
          }
        }
      }
    }

    for (int i = 0; i < block.length; i++) {
      for (int j = 0; j < block[i].length; j++) {
        if (block[i][j] == 1) {
          grid.active(x+j, y+i);
        }
      }
    }

    if (onGoing) {
      if ((count_delay % delaytime_mode) == 0) {
        y += 1;
      }
      count_delay++;
    }
    else {
      if (countongoing > 10) {
        count_delay = 0;
        int[][] block_coor = new int[blockno][2];
        int count = 0;
        for (int i = 0; i < block.length; i++) {
          for (int j = 0; j < block[i].length; j++) {
            if (block[i][j] == 1) {
              block_coor[count][0] = x+j;
              block_coor[count][1] = y+i;
              count += 1;
            }
          }
        }
        controlBlocks(block_coor, true);
        pointWatcher();
        x = (int)random(0, grid.horizontal_gridcount-5);
        y = y_initial;
        currentshape = nextshape;
        nextshape = (int)random(0, shapenum);
        stagenum = rnd[currentshape].length;
        currentstage = (int)random(0, stagenum);
        for (int f = 0; f < block_base.length; f++) {
          if (grid.isActive(x+block_base[f][0], 0)) {
            gamescreen = 2;
          }
        }
        onGoing = true;
        countongoing = 0;
      }
      else {
        count_delay+=2;
        onGoing = true;
        countongoing++;
      }
    }
  
  }

  public void holdBlocks() {
    for (int i = 0; i < actives.size(); i++) {
      int[] block = actives.get(i);
      grid.active(block[0], block[1]);
    }
  }

  public void controlBlocks(int[][] block, boolean dowhat) {
    if (dowhat) { // record blocks
      for (int i = 0; i < block.length; i++) {
        int[] activeadd = {
          block[i][0], block[i][1]
        };
        actives.add(activeadd);
      }
    }
    else { // remove blocks
      for (int i = 0; i < block.length; i++) {
        int[] removeblock = {
          block[i][0], block[i][1]
        };
        if (getindex(actives, removeblock) != -1) {
          actives.remove(getindex(actives, removeblock));
        }
      }
    }
  }

  public int getindex(ArrayList arrlist, int[] arr) {
    for (int i = 0; i < arrlist.size(); i++) {
      //if (Arrays.equals((int[])arrlist.get(i), arr)) {
        if (compareArrays((int[])arrlist.get(i), arr)){
        return i;
      }
    }
    return -1;
  }
  
  public boolean compareArrays(int[] arr1, int[] arr2){
   int minLen = (arr1.length < arr2.length) ? arr1.length : arr2.length;
      for(int j = 0; j < minLen; j++){
        if (arr1[j] != arr2[j]) {
          return false;
        }
      }
      return true;
  }


  public void destroyline(int i) {
    int[][] completed = new int[grid.horizontal_gridcount][2];
    for (int j = 0; j < grid.horizontal_gridcount; j++) {
      completed[j][0] = j;
      completed[j][1] = i;
    }
    controlBlocks(completed, false);
    completedrow = false;
    for (int j = i-1; j >= 0; j--) {
      for (int jj = 0; jj < grid.horizontal_gridcount; jj++) {
        if (grid.isActive(jj, j)) {
          int[] todeactivate = {
            jj, j
          };  
          int[] toactivate = {
            jj, j+1
          };
          if (getindex(actives, todeactivate) != -1) {
            actives.remove(getindex(actives, todeactivate));
            actives.add(toactivate);
          }
        }
      }
    }
    score += 5;
  }

  public void pointWatcher() {
    completedrow = false;
    for (int i = 0; i < grid.vertical_gridcount; i++) {
      if ((grid.isActive(0, i)) && (grid.isActive(grid.horizontal_gridcount-1, i))) {
        for (int j = 0; j < grid.horizontal_gridcount; j++) {
          if (grid.isDeactive(j, i)) {
            completedrow = false;
            break;
          }
          else {
            completedrow = true;
          }
        }
        if (completedrow) {
          destroyline(i);
        }
      }
    }
  }

  public void play() {


    if (onGoing) {
      int[][] block_properties_play = rnd[currentshape][currentstage][0];
      int[][] block_numbering_play = rnd[currentshape][currentstage][1];
      int[][] block_play = rnd[currentshape][currentstage][2];
      int[][] block_base_play = rnd[currentshape][currentstage][3];
      int[][] block_left_play = rnd[currentshape][currentstage][4];
      int[][] block_right_play = rnd[currentshape][currentstage][5];
      int[][] block_places_play = rnd[currentshape][currentstage][6];

      runBlock(block_play, block_base_play, block_left_play, block_right_play, block_properties_play[0][0], block_properties_play[0][1], block_places_play);
    }

  }
}
public void keyPressed() {

  if ((gamescreen == 2) && keyCode == ENTER) {
    gp.restart();
    gamescreen = 1;
  }
  if (gamescreen == 0){
  gamescreen = 1;
  }
  if ((keyCode == 'Q') || (keyCode == 'q')){
    exit();
  
  }
}
  
public void keyReleased() {
  if (keyCode == DOWN) {
    gp.delaytime_mode = 35;
  }
}

// L BLOCK -------------------------------------------------------------------------------------------

int[][] lblock_properties = {{3,4}}; // block_height (karelere g\u00f6re 3x3), block_no

int[][] lblock1_numbering = {{0,0}}; // shape_number, stage_number
int[][] lblock1 = {{1, 0, 0},{1, 0, 0},{1, 1, 0}};
int[][] lblock1_base = {{0, 2},{1, 2}};
int[][] lblock1_left = {{0, 0},{0, 1},{0, 2}};
int[][] lblock1_right = {{0, 0},{0,1},{1,2}};
int[][] lblock1_places = {{0,0},{0,1},{0,2},{1,2}};

int[][] lblock2_numbering = {{0,1}}; // shape_number, stage_number
int[][] lblock2 = {{0, 0, 0},{0, 0, 1},{1, 1, 1}};
int[][] lblock2_base = {{0, 2},{1, 2},{2,2}};
int[][] lblock2_left = {{0, 2},{2, 1}};
int[][] lblock2_right = {{2, 1},{2,2}};
int[][] lblock2_places = {{0,2},{1,2},{2,2},{2,1}};

int[][] lblock3_numbering = {{0,2}}; // shape_number, stage_number
int[][] lblock3 = {{1,1,0},{0,1,0},{0,1,0}};
int[][] lblock3_base = {{0,0},{1,2}};
int[][] lblock3_left = {{0, 0},{1, 1},{1,2}};
int[][] lblock3_right = {{1, 0},{1, 1},{1, 2}};
int[][] lblock3_places = {{0, 0},{1, 0},{1, 1},{1, 2}};

int[][] lblock4_numbering = {{0,3}}; // shape_number, stage_number
int[][] lblock4 = {{1,1,1},{1,0,0},{0,0,0}};
int[][] lblock4_base = {{0,1},{1,0},{2,0}};
int[][] lblock4_left = {{0, 0},{0, 1}};
int[][] lblock4_right = {{0, 1},{2,0}};
int[][] lblock4_places = {{0,0},{1,0},{2,0},{0,1}};

int[][] llblock1_numbering = {{1,0}}; // shape_number, stage_number
int[][] llblock1 = {{0, 1, 0},{0, 1, 0},{1, 1, 0}};
int[][] llblock1_base = {{0, 2},{1, 2}};
int[][] llblock1_left = {{1, 0},{1, 1},{0, 2}};
int[][] llblock1_right = {{1, 0},{1, 1},{1, 2}};
int[][] llblock1_places = {{1, 0},{1, 1},{0, 2},{1, 2}};

int[][] llblock2_numbering = {{1,1}}; // shape_number, stage_number
int[][] llblock2 = {{0, 0, 0},{1, 1, 1},{0, 0, 1}};
int[][] llblock2_base = {{0, 1},{1, 1},{2, 2}};
int[][] llblock2_left = {{0, 1},{2, 2}};
int[][] llblock2_right = {{2, 1},{2, 2}};
int[][] llblock2_places = {{0, 1},{1, 1},{2, 1},{2, 2}};

int[][] llblock3_numbering = {{1,2}}; // shape_number, stage_number
int[][] llblock3 = {{1, 1, 0},{1, 0, 0},{1, 0, 0}};
int[][] llblock3_base = {{0, 2},{1, 0}};
int[][] llblock3_left = {{0, 0},{0, 1},{0, 2}};
int[][] llblock3_right = {{1, 0},{0, 1},{0, 2}};
int[][] llblock3_places = {{0, 0},{1, 0},{0, 1},{0, 2}};

int[][] llblock4_numbering = {{1,3}}; // shape_number, stage_number
int[][] llblock4 = {{1, 0, 0},{1, 1, 1},{0, 0, 0}};
int[][] llblock4_base = {{0, 1},{1, 1},{2, 1}};
int[][] llblock4_left = {{0, 0},{0, 1}};
int[][] llblock4_right = {{0, 0},{2, 1}};
int[][] llblock4_places = {{0, 0},{0, 1},{1, 1},{2, 1}};

// S BLOCK -------------------------------------------------------------------------------------------

int[][] sblock_properties = {{2,4}}; // block_height (karelere g\u00f6re 3x3), block_no

int[][] sblock1_numbering = {{2,0}}; // shape_number, stage_number
int[][] sblock1 = {{1, 1},{1, 1}};
int[][] sblock1_base = {{0, 1},{1, 1}};
int[][] sblock1_left = {{0, 0},{0, 1}};
int[][] sblock1_right = {{1, 0},{1, 1}};
int[][] sblock1_places = {{0, 0},{0, 1},{1, 0},{1, 1}};

// Z BLOCK -------------------------------------------------------------------------------------------

int[][] zblock_properties = {{3,4}}; // block_height (karelere g\u00f6re 3x3), block_no

int[][] zblock1_numbering = {{3,0}}; // shape_number, stage_number
int[][] zblock1 = {{1, 1, 0},{0, 1, 1},{0, 0, 0}};
int[][] zblock1_base = {{0, 0},{1, 1}, {2, 1}};
int[][] zblock1_left = {{0, 0},{1, 1}};
int[][] zblock1_right = {{1, 0},{2, 1}};
int[][] zblock1_places = {{0, 0},{1, 0},{1, 1},{2, 1}};

int[][] zblock2_numbering = {{3,1}}; // shape_number, stage_number
int[][] zblock2 = {{0, 1, 0},{1, 1, 0},{1, 0, 0}};
int[][] zblock2_base = {{0, 2},{1, 1}};
int[][] zblock2_left = {{1, 0},{0, 1},{0, 2}};
int[][] zblock2_right = {{1, 0},{1, 1},{0, 2}};
int[][] zblock2_places = {{1, 0},{1, 1},{0, 1},{0, 2}};

int[][] zzblock1_numbering = {{4,0}}; // shape_number, stage_number
int[][] zzblock1 = {{0, 1, 1},{1, 1, 0},{0, 0, 0}};
int[][] zzblock1_base = {{0, 1},{1, 1}, {2, 0}};
int[][] zzblock1_left = {{1, 0},{0, 1}};
int[][] zzblock1_right = {{2, 0},{1, 1}};
int[][] zzblock1_places = {{1, 0},{2, 0},{0, 1},{1, 1}};

int[][] zzblock2_numbering = {{4,1}}; // shape_number, stage_number
int[][] zzblock2 = {{1, 0, 0},{1, 1, 0},{0, 1, 0}};
int[][] zzblock2_base = {{0, 1},{1, 2}};
int[][] zzblock2_left = {{0, 0},{0, 1},{1, 2}};
int[][] zzblock2_right = {{0, 0},{1, 1},{1, 2}};
int[][] zzblock2_places = {{0, 0},{0, 1},{1, 1},{2, 2}};

// T BLOCK -------------------------------------------------------------------------------------------

int[][] tblock_properties = {{3,4}}; // block_height (karelere g\u00f6re 3x3), block_no

int[][] tblock1_numbering = {{5,0}}; // shape_number, stage_number
int[][] tblock1 = {{0, 0, 0},{0, 1, 0},{1, 1, 1}};
int[][] tblock1_base = {{0, 2},{1, 2},{2, 2}};
int[][] tblock1_left = {{0, 2},{1, 1}};
int[][] tblock1_right = {{2, 2},{1, 1}};
int[][] tblock1_places = {{0, 2},{1, 1},{1, 2},{2, 2}};

int[][] tblock2_numbering = {{5,1}}; // shape_number, stage_number
int[][] tblock2 = {{0, 1, 0},{1, 1, 0},{0, 1, 0}};
int[][] tblock2_base = {{0, 1},{1, 2}};
int[][] tblock2_left = {{1, 0},{0, 1},{1, 2}};
int[][] tblock2_right = {{1, 0},{1, 1},{1, 2}};
int[][] tblock2_places = {{1, 0},{1, 1},{1, 2},{0, 1}};

int[][] tblock3_numbering = {{5,2}}; // shape_number, stage_number
int[][] tblock3 = {{1, 1, 1},{0, 1, 0},{0, 0, 0}};
int[][] tblock3_base = {{0, 0},{1, 1},{2, 0}};
int[][] tblock3_left = {{0, 0},{1, 1}};
int[][] tblock3_right = {{2, 0},{1, 1}};
int[][] tblock3_places = {{0, 0},{1, 0},{1, 1},{2, 0}};

int[][] tblock4_numbering = {{5,2}}; // shape_number, stage_number
int[][] tblock4 = {{1, 0, 0},{1, 1, 0},{1, 0, 0}};
int[][] tblock4_base = {{0, 2},{1, 1}};
int[][] tblock4_left = {{0, 0},{0, 1},{0, 2}};
int[][] tblock4_right = {{0, 0},{1, 1},{0, 2}};
int[][] tblock4_places = {{0, 0},{0, 1},{0, 2},{1, 1}};

// I BLOCK -------------------------------------------------------------------------------------------

int[][] iblock_properties = {{4,4}}; // block_height (karelere g\u00f6re 3x3), block_no

int[][] iblock1_numbering = {{6,0}}; // shape_number, stage_number
int[][] iblock1 = {{1, 0, 0, 0},{1, 0, 0, 0},{1, 0, 0, 0},{1, 0, 0, 0}};
int[][] iblock1_base = {{0, 3}};
int[][] iblock1_left = {{0, 0},{0, 1},{0, 2},{0, 3}};
int[][] iblock1_right = {{0, 0},{0, 1},{0, 2},{0, 3}};
int[][] iblock1_places = {{0, 0},{0, 1},{0, 2},{0, 3}};

int[][] iblock2_numbering = {{6,1}}; // shape_number, stage_number
int[][] iblock2 = {{0, 0, 0, 0},{1, 1, 1, 1},{0, 0, 0, 0},{0, 0, 0, 0}};
int[][] iblock2_base = {{0, 1},{1, 1},{2, 1},{3, 1}};
int[][] iblock2_left = {{0, 1}};
int[][] iblock2_right = {{3, 1}};
int[][] iblock2_places = {{0, 1},{1, 1},{2, 1},{3, 1}};








//SHAPE (0:LBLOCK, 1:SBLOCK)
//  STAGE
//    PROPERTIES {HEIGHT,NO} 0
//    BLOCK_NUMBERING 1
//    BLOCK 2
//    BLOCK-BASE 3
//    BLOCK-LEFT 4
//    BLOCK-RIGHT 5
//    BLOCK-PLACES 6

int[][][][][] rnd = {
  {
{lblock_properties,lblock1_numbering,lblock1,lblock1_base,lblock1_left,lblock1_right,lblock1_places},
{lblock_properties,lblock2_numbering,lblock2,lblock2_base,lblock2_left,lblock2_right,lblock2_places},
{lblock_properties,lblock3_numbering,lblock3,lblock3_base,lblock3_left,lblock3_right,lblock3_places},
{lblock_properties,lblock4_numbering,lblock4,lblock4_base,lblock4_left,lblock4_right,lblock4_places}
  },
  {
{lblock_properties,llblock1_numbering,llblock1,llblock1_base,llblock1_left,llblock1_right,llblock1_places},
{lblock_properties,llblock2_numbering,llblock2,llblock2_base,llblock2_left,llblock2_right,llblock2_places},
{lblock_properties,llblock3_numbering,llblock3,llblock3_base,llblock3_left,llblock3_right,llblock3_places},
{lblock_properties,llblock4_numbering,llblock4,llblock4_base,llblock4_left,llblock4_right,llblock4_places}
  },
  {
{sblock_properties,sblock1_numbering,sblock1,sblock1_base,sblock1_left,sblock1_right,sblock1_places}
  },
  {
{zblock_properties,zblock1_numbering,zblock1,zblock1_base,zblock1_left,zblock1_right,zblock1_places},
{zblock_properties,zblock2_numbering,zblock2,zblock2_base,zblock2_left,zblock2_right,zblock2_places}
  },
  {
{zblock_properties,zzblock1_numbering,zzblock1,zzblock1_base,zzblock1_left,zzblock1_right,zzblock1_places},
{zblock_properties,zzblock2_numbering,zzblock2,zzblock2_base,zzblock2_left,zzblock2_right,zzblock2_places}
  },
  {
{tblock_properties,tblock1_numbering,tblock1,tblock1_base,tblock1_left,tblock1_right,tblock1_places},
{tblock_properties,tblock2_numbering,tblock2,tblock2_base,tblock2_left,tblock2_right,tblock2_places},
{tblock_properties,tblock3_numbering,tblock3,tblock3_base,tblock3_left,tblock3_right,tblock3_places},
{tblock_properties,tblock4_numbering,tblock4,tblock4_base,tblock4_left,tblock4_right,tblock4_places}
  },
    {
{iblock_properties,iblock1_numbering,iblock1,iblock1_base,iblock1_left,iblock1_right,iblock1_places},
{iblock_properties,iblock2_numbering,iblock2,iblock2_base,iblock2_left,iblock2_right,iblock2_places}
  }
};
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--hide-stop", "tetris" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
