//This sorting algarithm is derived on the "bouncybubbles" example from Processing.org
//http://processing.org/learning/topics/bouncybubbles.html
class Bubble {
  float x, y, z;
  float diameter;
  float dmoney;
  float monease;
  float dease;
  float zease;
  float vx = 0;
  float vy = 0;
  int index;
  int id;
  int lineCount;
  String bclient;
  String bmoney;
  boolean dragging = false;
  boolean bufferSwitch;
  boolean trackToggle = false;
  color barColor;
  color c = color(50, 50, 50, 25);
  color cm = color(0);
  float spring;
  float gravity;
  float friction;
  float xMiddle;
  float yMiddle;
  float minDist;
  float buffer;
  float originalSize;
  float displayRectWIDTH;
  float diamStandard;
  ArrayList<Bubble> theseBubbles = new ArrayList();
  float total;
  float maxSpentSq;
  float minSpentSq;
  ArrayList spec;

  int colorSwitchCount = 0;



  Bubble(float xin, float yin, int din, String iss, String mny, int indexin, float totalin, ArrayList specin) {
    x = xin;
    y = yin;
    diameter = map(sqrt(1), 0, sqrt(clientArrayList.size()*0.7), 0, 200); //map(sqrt(1), 0, sqrt(clientArrayList.size()*0.4), 0, 200);
    diamStandard = map(sqrt(1), 0, sqrt(clientArrayList.size()*0.7), 0, 200);
    bclient= iss;
    bmoney= mny;
    id = din;
    spring = 0.4;
    gravity = 0;
    friction = 0;
    index = indexin;
    xMiddle = 0;
    yMiddle = 0;
    z = 0;//din*(-10);
    bufferSwitch = false;
    buffer = 1.2;
    total = totalin;
    spec = specin;
    maxSpentSq = sqrt(max(allSpentArray)/205000);
    minSpentSq = sqrt(min(allSpentArray)/205000);
    originalSize = map(sqrt(1), 0, sqrt(clientArrayList.size()*0.7), 0, 200);
  } 

  void collide() {
    if (selected != this) {
      for (int i = 0; i < bubbles.length; i++) {
        if (selected != bubbles[i]) {
          float dx = bubbles[i].x - x;
          float dy = bubbles[i].y - y;
          float distance = sqrt(dx*dx + dy*dy);
          //minDist = ((bubbles[i].diameter*0.9))+ (diameter/2);//use this for middle gravity circle layout
          minDist = ((bubbles[i].dease*0.5))+(dease*this.buffer); 
          if (distance < minDist) { 
            float angle = atan2(dy, dx);
            float targetX = x + cos(angle) * minDist;
            float targetY = y + sin(angle) * minDist;
            float ax = (targetX - bubbles[i].x) * spring;
            float ay = (targetY - bubbles[i].y) * spring;
            vx -= ax;
            vy -= ay;
            bubbles[i].vx += ax;
            bubbles[i].vy += ay;
          }
        }
      }
    }
  }

  void motion() {
    //vy += gravity;
    if (centerGravity) {
      xMiddle = 0;
      yMiddle = 0;
      x += (xMiddle-x)*0.016;
      y += (yMiddle-y)*0.016;
    }
    x += vx/2;
    y += vy/2;

    if (mouseX < x+(dease/2)+(width/2) && mouseX > x-(dease/2)+(width/2) && mouseY < y+(dease/2)+(height/2) && mouseY > y-(dease/2)+(height/2)) {
      if (mousePressed) {
        x = mouseX-(width/2); 
        y = mouseY-(height/2);
        if (selected == null) {selected = this;} //println(this.bclient);}
      } 
      else {
        selected = null;
      }
    }
    if (x + dease/2 > (width/2)) {
      //        x = (width/2) - diameter;
      //        x -= 12;
      vx *= friction*(-1);
    }
    else if (x - dease/2 < (-width/2)) {
      //      x = (-width/2)+diameter;
      //      x += 12;
      vx *= friction*(-1);
    } //else {vx *= 0.5;}

    if (y + dease/2 > (height/2)) {
      //      y = (height/2) - diameter;
      //      y -= 12;
      vy *= friction*(-1);
    } 
    else if (y - dease/2 < (-height/2)) {
      //      y = (-height/2)+diameter;
      //      y += 12;
      vy *= friction*(-1);
    } //else {vy *= 0.5;}
    vx *= 0.9;
    vy *= 0.9;
    zease += (z-zease)*0.2;
    dease += (diameter-dease)*0.08;
    monease += (dmoney-monease)*0.08;
  }
  void moneyMotion() {
    dease += (diameter-dease)*0.08;
    monease += (dmoney-monease)*0.08;
    zease += (z-zease)*0.2;
    x += (xMiddle-x)*0.04;
    y += (yMiddle-y)*0.04;
  }

  void display() {
    noStroke();
    fill(c);
    if (toggle3D == true) {
      //      pushMatrix(); AWESOME perspective with wire mesh
      //       // rotateY(radians(map(mouseY, 0, height, 0, 360)));
      //        rotateY(radians(map(mouseX, 0, screen.width, 0, 360)));
      //        rotateX(radians(map(mouseX, 0, screen.width, -100, 100)));
      //        translate(x,y);
      //        box(dease/2, dease/2, 5);
      //      popMatrix(); 

      pushMatrix(); 
      translate(this.x, this.y, this.zease);
      box(dease/2, dease/2, 5);
      popMatrix();
    } 
    else {
      
      if(moneySort && mouseX < x+(originalSize/2)+(width/2) && mouseX > x-(originalSize/2)+(width/2) && mouseY < y+(originalSize/2)+(height/2) && mouseY > y-(originalSize/2)+(height/2)){
        fill(255, 255, 255, 180);
        companyTab();
      } else if (!moneySort && mouseX < x+(dease/2)+(width/2) && mouseX > x-(dease/2)+(width/2) && mouseY < y+(dease/2)+(height/2) && mouseY > y-(dease/2)+(height/2)) {
        fill(255, 255, 255, 180); 
        companyTab(); 
      } 
      if (this.trackToggle){fill(75, 80, 95, 200); stroke(60, 50, 60, 200); strokeWeight(1);} //fill(95, 95, 75, 200);
      else {strokeWeight(1); noStroke();}
      ellipse(x, y, dease, dease);
      
      fill(50);
      //textAlign(CENTER);
      issueScroll = false;
    } //END pop 3D Matrix if statement
    //noStroke();
  }

  void widthFlux() {
    if (goWidth) {      

      for (int j = 0; j < clientArrayList.get(this.index).issueArrayList.size(); j++) {
        if (!clientArrayList.get(this.index).issueArrayList.get(j).issuetype.equals("")) {
          if (clientArrayList.get(this.index).issueArrayList.get(j).issuetype.equals(thisone)) {
            if (!moneySort && mouseX < x+(dease/2)+(width/2) && mouseX > x-(dease/2)+(width/2) && mouseY < y+(dease/2)+(height/2) && mouseY > y-(dease/2)+(height/2)) {
                fill(50,50,50,80);
                pushMatrix();
                translate(0, 0, 2);
                textSize(13);
                textAlign(CENTER);
                text("$"+clientArrayList.get(this.index).lobHASH_Issue.get(thisone).spentOnIssue, this.x, this.y+4);
                popMatrix();
              }
            if(firstTimeAround){
              bufferResize.add(this);
            }
            float issuesFlux = clientArrayList.get(this.index).issueArrayList.get(j).countissues;
            float colorSet = map(this.total, theseSpentMin, theseSpentMax, 100, 255);
            //println(this.total +" : "+theseSpentMax);
            cm = color(210-issuesFlux*10, 125-issuesFlux*issuesFlux/6, 120+issuesFlux*issuesFlux/10, 120-issuesFlux*2);
            this.c = cm;
            this.colorSwitchCount = frameCount;
            this.diameter = map(sqrt(clientArrayList.get(this.index).lobHASH_Issue.get(thisone).spentOnIssue / (theseSpentMin*0.55)), minSpentSq, maxSpentSq, 10, 150);
            this.dmoney = map(sqrt(clientArrayList.get(this.index).lobHASH_Issue.get(thisone).spentOnIssue / (theseSpentMin*0.55)), minSpentSq, maxSpentSq, 10, 150);
            //this.diameter = map(sqrt(clientArrayList.get(this.index).lobHASH_Issue.get(thisone).spentOnIssue / 205000), 0, sqrt(200), 0, 200);
            
            if (this.dease > diamStandard*10) {
              this.buffer = 0.6;
            } else if (this.dease > diamStandard) {
              this.buffer = 1;
            }
            
            this.z = -clientArrayList.get(this.index).issueArrayList.get(j).countissues*10;
            
            if (lineTime && firstTimeAround) {
              Connector tempc = new Connector(this, this.x, this.y, this.bclient, thisone, lineCount, this.diameter, this.z);
              connects.add(tempc);
              lineCount++;
            }
          } 
          else {
            if (this.colorSwitchCount != frameCount) { 
              this.c = color(50, 50, 50, 25); 
              this.bufferSwitch = false;
              this.buffer = 1.2;
              this.z = 0;
              this.diameter = diamStandard;
              this.dmoney = 5;
              colorMode(RGB);
            }
          }
        } // close the int j; clientArrayList.get(i).issueArrayList.size() for loop
      }
      //println(connects.size());
    }
  }

  void chartTool() {
    if (oddNumber) {
      barColor = color(70, 70, 70, 180);
      fill(barColor);
      rect((chartPlace)-this.index, hHeight-100, 1, (-this.monease));
      rect((chartPlace)-this.index, hHeight-100, 1, (-this.zease/4));
      oddNumber = !oddNumber;
    } 
    else {
      barColor = color(130, 105, 80, 180);
      fill(barColor);
      rect((chartPlace)-this.index, hHeight-100, 1, (-this.monease));
      rect((chartPlace)-this.index, hHeight-100, 1, (-this.zease/4));
      oddNumber = !oddNumber;
    }
  }
  
  void companyTab(){
    pushMatrix();
        textAlign(CENTER);
        translate(0, 0, 2);
        textFont(f2);
        textSize(20);
        String displayText1 = bclient;
        float displayWIDTH1 = textWidth(displayText1);
        
        textFont(f);
        textSize(14);
        String displayText2 = "$"+this.total+" spent in 2010";
        float displayWIDTH2 = textWidth(displayText2);
        
        if(displayWIDTH1 > displayWIDTH2){
          displayRectWIDTH = displayWIDTH1;
        } else { displayRectWIDTH = displayWIDTH2; }
        fill(75, 70, 65, 200);
        rect(((chartPlace-20)-this.index)-(displayRectWIDTH/2)-17, (hHeight-118)+(-this.dease-49), displayRectWIDTH+35, 47);
        fill(250);
        textFont(f2);
        textSize(20);
        textAlign(CENTER);
        text(displayText1, (chartPlace-20)-this.index, (hHeight-145)+(-this.dease));
        textFont(f);
        textSize(14);
        text(displayText2, (chartPlace-20)-this.index, (hHeight-128)+(-this.dease));
        stroke(100, 100, 100, 100);
        line((chartPlace-20)-this.index, (hHeight-118)+(-this.dease), (chartPlace)-this.index, (hHeight-100)+(-this.dease));
        if(showSpecific) specIssueDisplay();
      popMatrix();
  }

  void specIssueDisplay() {
    issueScroll = true;
    fill(220);
    textFont(f2, 14);
    textAlign(LEFT);
    textLeading(60);
    textSize(14);
    
    if( this.c == cm){
      String[] issueSpecStringArray = new String[0];
      String issueSpecString = ""; //String to put each specific issue into
        for (int b = 0; b<this.spec.size(); b++) {
          issueSpecString += this.spec.get(b)+"$$$";
        }
        issueSpecStringArray = split(issueSpecString, "$$$");
         if(scrollCount < issueSpecStringArray.length-1){
          
         } else {scrollCount = issueSpecStringArray.length-1;}
          //println(issueSpecStringArray.length-1 +" : "+issueSpecStringArray.length);
          //println("scrollCount = "+scrollCount);
          for (int issa = 0; issa<issueSpecStringArray.length; issa++) {
              fill(255, 255, 255, 200);
              rect(-hWidth+125, (-hHeight+185)+(issa*30), width-245, height-245);
              fill(60);
              text(issueSpecStringArray[issa], -hWidth+140, (-hHeight+200)+(issa*30), width-280, height-280);
              fill(255, 255, 255, 180);
         }
    }
           
//    for (int j = 0; j < clientArrayList.get(this.index).issueArrayList.size(); j++) {
//      if (!clientArrayList.get(this.index).issueArrayList.get(j).issuetype.equals("")) {
//        if (clientArrayList.get(this.index).issueArrayList.get(j).issuetype.equals(thisone)) {
//
//          String[] issueSpecStringArray = new String[0];
//          //for (int b = 0; b<clientArrayList.get(this.index).issueArrayList.get(j).specIssueType.length; b++) {
//           if(scrollCount < clientArrayList.get(this.index).issueArrayList.get(j).specIssueType.length-1){
//            issueSpecStringArray = split(clientArrayList.get(this.index).issueArrayList.get(j).specIssueType[scrollCount], "$$$");
//           } else {scrollCount = clientArrayList.get(this.index).issueArrayList.get(j).specIssueType.length-1;}
//            println(clientArrayList.get(this.index).issueArrayList.get(j).specIssueType.length +" : "+issueSpecStringArray.length);
//            println("scrollCount = "+scrollCount);
//            for (int issa = 0; issa<issueSpecStringArray.length; issa++) {
//                text(issueSpecStringArray[issa], -hWidth+20, (-hHeight+20)+(issa*30));
//            }
//        }
//      }
//    }
  } //END specIssueDisplay()
  
//  void mouseReleased() {
//    println("click");
//       if(moneySort && mouseX < x+(originalSize/2)+(width/2) && mouseX > x-(originalSize/2)+(width/2) && mouseY < y+(originalSize/2)+(height/2) && mouseY > y-(originalSize/2)+(height/2)){
//        println("this click");
//      } else if (!moneySort && mouseX < x+(dease/2)+(width/2) && mouseX > x-(dease/2)+(width/2) && mouseY < y+(dease/2)+(height/2) && mouseY > y-(dease/2)+(height/2)) {
//        println("that click"); 
//      } 
//  }
  
  
}//END BUBBLE CLASS



