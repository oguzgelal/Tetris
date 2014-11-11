import ddf.minim.*;
import ddf.minim.signals.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;

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
int gamescreen = 0; // 0 : menü, 1 : oyun, 2 : gameover

Minim minim;
AudioPlayer bgaudio;

void setup() {
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

void draw() {
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
  color clr;

  void active() {
    rectstatus = 1;
  }
  void deactive() {
    rectstatus = 0;
  }
  void empty() {
    rectstatus = 2;
  }

  void run(int x, int y, int rectwidth, int rectheight) {
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


  void init() {
    stroke(200, 200, 200);
    for (int i = 0; i < horizontal_gridcount; i++) {
      for (int j = 0; j < vertical_gridcount; j++) {
        //noStroke();
        rectangle[i][j] = new Rectangle();
        rectangle[i][j].run(i*gwidth, j*gwidth, gwidth, gwidth);
      }
    }
  }
  void active(int x, int y) {
    if ((x < horizontal_gridcount) && (y < vertical_gridcount)) {
      rectangle[x][y].active();
      rectangle[x][y].run(x*gwidth, y*gwidth, gwidth, gwidth);
    }
  }
  void deactive(int x, int y) {
    if ((x < horizontal_gridcount) && (y < vertical_gridcount)) {
      rectangle[x][y].deactive();
      rectangle[x][y].run(x*gwidth, y*gwidth, gwidth, gwidth);
    }
  }
  void empty(int x, int y) {
    if ((x < horizontal_gridcount) && (y < vertical_gridcount)) {
      rectangle[x][y].empty();
      rectangle[x][y].run(x*gwidth, y*gwidth, gwidth, gwidth);
    }
  }
  boolean isActive(int x, int y) {
    return rectangle[x][y].rectstatus == 1;
  }
  boolean isDeactive(int x, int y) {
    return rectangle[x][y].rectstatus == 0;
  }
  boolean isEmpty(int x, int y) {
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
  int shapenum = rnd.length; // şekil sayısı
  //int currentshape = 6;
  int currentshape = (int)random(0, shapenum);
  //int nextshape = 6;
  int nextshape = (int)random(0, shapenum);
  int stagenum = rnd[currentshape].length; // şekillerin aşama sayısı
  int currentstage = (int)random(0, stagenum);
  int countongoing = 0;
  int y_initial = 0;
  boolean turnable = false;

  int score = 0;
  int level = 1;
  int x = (int)random(0, grid.horizontal_gridcount-5);
  int y = y_initial;

  void restart() {
    actives.clear();
    score = 0;
    level = 1;
    x = (int)random(0, grid.horizontal_gridcount-5);
    y = y_initial;
  }

  void runBlock(int[][] block, int[][] block_base, int[][] block_left, int[][] block_right, int block_height, int blockno, int[][] places) {
    
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

  void holdBlocks() {
    for (int i = 0; i < actives.size(); i++) {
      int[] block = actives.get(i);
      grid.active(block[0], block[1]);
    }
  }

  void controlBlocks(int[][] block, boolean dowhat) {
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

  int getindex(ArrayList arrlist, int[] arr) {
    for (int i = 0; i < arrlist.size(); i++) {
      //if (Arrays.equals((int[])arrlist.get(i), arr)) {
        if (compareArrays((int[])arrlist.get(i), arr)){
        return i;
      }
    }
    return -1;
  }
  
  boolean compareArrays(int[] arr1, int[] arr2){
   int minLen = (arr1.length < arr2.length) ? arr1.length : arr2.length;
      for(int j = 0; j < minLen; j++){
        if (arr1[j] != arr2[j]) {
          return false;
        }
      }
      return true;
  }


  void destroyline(int i) {
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

  void pointWatcher() {
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

  void play() {


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
void keyPressed() {

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
  
void keyReleased() {
  if (keyCode == DOWN) {
    gp.delaytime_mode = 35;
  }
}

