import processing.core.*; 
import processing.xml.*; 

import org.json.*; 
import processing.opengl.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class LobbyData_Issues2ee extends PApplet {

// PARSING SUNLIGHT LABS LOBBYIST DATA FROM TEXT FILES



PFont f;
PFont fb;
PFont f2; 

LobbyBlob lob;
ArrayList<LobbyBlob> lobList = new ArrayList();
ArrayList<LobbyBlob> lobIssueArray = new ArrayList();

ArrayList<LobbyClient> clientArrayList = new ArrayList();
HashMap<String, LobbyClient> lobHASH = new HashMap();

ArrayList<LobbyIssue> issueArrayList = new ArrayList();
HashMap<String, LobbyIssue> lobHASH_Issue = new HashMap();

Bubble[] bubbles = new Bubble[0];
Bubble selected = null;
ArrayList<Bubble> bufferResize = new ArrayList();
ArrayList<Connector> connects = new ArrayList();
ArrayList<String> alphaClients = new ArrayList();

LobbyClient ltype;

boolean showAlphaClients = false;
boolean trackThisBubble = false;
boolean setSort;
boolean bubblesAdded = false;
boolean addNewBases = true;
boolean goIssues = false;
boolean goWidth = false;
boolean lineTime = false;
boolean centerGravity = false;
boolean toggle3D = false;
boolean moneySort = false;
boolean oddNumber = true;
boolean issueScroll = false;
boolean firstTimeAround = true;
boolean showSpecific = false;
boolean matchFound = false;
boolean chooseYears = false;
boolean showYearsNow = false;
boolean pick2011 = false;
boolean pick2010 = false;
boolean pick2009 = false;
boolean pick2008 = false;
boolean pick2007 = false;

boolean forwardGo = false;
boolean backwardGo = false;
boolean pauseGo = false;
boolean gridGo = false;
boolean threeDGo = false;
boolean specificGo = false;
boolean alphaGo = true;

String intro = "Federal Lobbying";
String choosenYear = "year\u2265";
String thisone = "";
String[] lobbyData;
String[] lobbyistsArray;

int hWidth;
int hHeight;
int incrementIssue;
int clientsNew;
int lastMillis;
int scrollCount;

float chartPlace;
float Xmapped = 0;
float theseSpentMax;
float theseSpentMin;
float headWIDTH;
float listMap;
float[] theseSpentArray = new float[0];
float[] theseSpentFILTER = new float[0];
float[] allSpentArray = new float[0];


public void setup() {
  size(1300, 800, OPENGL);
  background(210, 215, 205);
  smooth();
  f = createFont("DIN-Medium", 60, true);
  fb = createFont("DINPro-Bold", 60, true);
  f2 = createFont("Copse-Regular.ttf", 60, true);
  textFont(f2, 30);
  textAlign(CENTER);
  lobbyData = new String[0];
  //loadAndSort();
  hWidth = width/2;
  hHeight = height/2;
  listMap = -hHeight+100;
}


public void draw() {
  colorMode(RGB);
  background(210, 215, 205);
  
  /////^^^^MAKE A PROCESSING INTERFACE^^^^\\\\\
  
   theInterface();
 
   /////^^^^END A PROCESSING INTERFACE^^^^\\\\\

  translate(width/2, height/2); //move everything to the center
  if(showAlphaClients){
    listClientsAlpha();
  }
  if (setSort == true) {
    sortByClient();
    sortTest();
  }
  if (bubblesAdded == true) {
    lineChartOverlay();
  }
  if (toggle3D == true) {
    pushMatrix();
    translate(0, 120);
    rotateX(map(mouseY, 0, screen.height, 1.5f, 1.5f));
    rotateY(22);
    rotate(map(mouseX, 0, screen.width, 0, 10));
    scale(1.15f);
  }
  if (bubblesAdded == true) {
    for (int i = 0; i < bubbles.length; i++) {
      if (!moneySort) {
        bubbles[i].collide();
        bubbles[i].motion();
        bubbles[i].display();
        bubbles[i].widthFlux();
      } 
      else {
        bubbles[i].moneyMotion();
        bubbles[i].display();
        bubbles[i].widthFlux();
      }
    }
    firstTimeAround = false;
    oddNumber = true;

    for (int i = 0; i < connects.size(); i++) {
      connects.get(i).display();
      connects.get(i).motion();
    }
    lineTime = false;
  }
  if (incrementIssue>issueArrayList.size()-1) {
    incrementIssue=0;
  }
  if (toggle3D == true) {
    popMatrix();
  }

  for (int i = 0; i < bubbles.length; i++) {
    bubbles[i].chartTool();
  }

  if (millis()>lastMillis+6000 && goIssues) { //been at least 6000 millis 
    lastMillis=millis();
    connects.clear();
    bufferResize.clear();
    if (incrementIssue<issueArrayList.size()-2) {
      incrementIssue++;
      firstTimeAround = true;
      if (!moneySort) {
        lineTime=true;
      }
    }
    else {
      incrementIssue=0;
    }
  }
}


public void loadAndSort(String chYear) {
  int koont= 0;
  println("loadAndSort() initializing. Loading from from text... please be patient...");
  String[] keys = new String[1];
  keys[0] = "issues";
  String[] genkey = new String[1];
  genkey[0] = "general_issue";
  ArrayList theseLobbyistIssues = new ArrayList(0);
  try {
    JSONObject lobbyData = new JSONObject(join(loadStrings("lobby_"+chYear+".txt"), ""));
    JSONArray lobbyists = lobbyData.getJSONArray("lobbyRequest");     

    for (int i = 0; i < lobbyists.length(); i++) {
      JSONObject lobbyist = lobbyists.getJSONObject(i);
      lob = new LobbyBlob();
      lob.lyear = lobbyist.getString("year");
      lob.clientName = lobbyist.getString("client_name");
      lob.amountSpent = lobbyist.getString("amount");

      JSONObject issuesData = new JSONObject(lobbyist, keys); //each element is complex issues
      JSONArray issuesArray = issuesData.getJSONArray("issues");

      for (int j = 0; j < issuesArray.length(); j++) {
        JSONObject theIssues = issuesArray.getJSONObject(j);
        lob.issuesPack.add(theIssues.get("general_issue"));
        lob.issuesSpecific.add(theIssues.get("specific_issue"));
      }

      if (lob.clientName != null) {
        lobList.add(lob);
        //Do we have this client in our HashMap already?
        if (lobHASH.containsKey(lob.clientName)) {
          //YES, we have that client
          ltype = lobHASH.get(lob.clientName);
          lob.c = ltype.c;
          //Add one to the LobbyClient counter
          ltype.count ++;

          String strcash = lob.amountSpent;
          int dotPos = strcash.lastIndexOf(".");
          strcash = strcash.substring(0, dotPos);
          float getcash = floor(Integer.parseInt(strcash));       
          ltype.totalMoney += getcash;

          //Add this LobbyClient to the list of LobbyClient's that share the client
          ltype.lobList.add(lob);
        } 
        else {
          //NO, we don't have that client
          clientsNew++;
          ltype = new LobbyClient();
          ltype.c = color(random(90, 190), random(90, 190), random(90, 190));
          lob.c = ltype.c;
          ltype.clname = lob.clientName;
          ltype.money = lob.amountSpent;
          ltype.originPlace = clientsNew; 

          String strcash = lob.amountSpent;
          int dotPos = strcash.lastIndexOf(".");
          strcash = strcash.substring(0, dotPos);
          float getcash = floor(Integer.parseInt(strcash));       
          ltype.totalMoney += getcash; 

          ltype.issuesPk = lob.issuesPack;
          ltype.issuesSpec = lob.issuesSpecific;
          ltype.count = 1;
          lobHASH.put(lob.clientName, ltype);
          //Add one to the LobbyClient to the list of unique LobbyClients
          clientArrayList.add(ltype);
          //Add this LobbyClient to the list of LobbyClient's that share the client
          ltype.lobList.add(lob);
        }


        if (lob.issuesPack != null) {
          lobIssueArray.add(lob);

          String issueString = ""; //String to put each issue into
          String issueSpecString = ""; //String to put each specific issue into
          for (int b = 0; b<lob.issuesPack.size(); b++) {
            issueString += lob.issuesPack.get(b)+"$$$";//put each issue into a string + ","
            issueSpecString += lob.issuesSpecific.get(b)+"$$$";
          }
          String[] issueStringArray = split(issueString, "$$$");//split the String into an array by ","
          issueStringArray = trim(issueStringArray);//get rid of whitespace



          for (int q = 0; q<issueStringArray.length; q++) {//for each issue in the array, check the Hash
            //GLOBAL ISSUE FILING
            //Do we have this issue in our HashMap already?
            if (lobHASH_Issue.containsKey(issueStringArray[q])) {
              //YES, we have that issue
              LobbyIssue litype = lobHASH_Issue.get(issueStringArray[q]);
              //Add one to the LobbyIssue countissues
              litype.countissues ++;
              //Add this LobbyIssue to the list of LobbyBlobs that share the issue (ie. "Taxes")
              litype.lobIssueArray.add(lob);
            } 
            else {
              //NO, we don't have that issue
              LobbyIssue litype = new LobbyIssue();
              litype.issuetype = issueStringArray[q];
              litype.countissues = 1;
              lobHASH_Issue.put(litype.issuetype, litype);
              //Add one LobbyIssue to the list of unique LobbyIssues
              issueArrayList.add(litype);
              //Add this LobbyIssue to the list of LobbyIssue's that share the issue (ie. "Taxes")
              litype.lobIssueArray.add(lob);
            }

            //CLIENT SPECIFIC ISSUE FILING
            //Do we have this issue in our HashMap already?
            if (ltype.lobHASH_Issue.containsKey(issueStringArray[q])) {
              //YES, we have that issue
              LobbyIssue litype = ltype.lobHASH_Issue.get(issueStringArray[q]);
              //Add one to the LobbyIssue countissues
              litype.countissues ++;

              //turn the amountSpent into a long, so it can be added to a sum
              String strcash = lob.amountSpent;
              int dotPos = strcash.lastIndexOf(".");
              strcash = strcash.substring(0, dotPos);
              float getcash = floor(Integer.parseInt(strcash));
              litype.spentOnIssue += getcash;
              allSpentArray = (float[]) append(allSpentArray, getcash);
              //add in all the specific issues
              litype.specIssueType = append(litype.specIssueType, issueSpecString);

              //Add this LobbyIssue to the list of LobbyBlobs that share the issue (ie. "Taxes")
              ltype.issueArrayList.add(litype);
              //println("litype.countissues = "+litype.countissues);
            } 
            else {
              //NO, we don't have that issue
              LobbyIssue litype = new LobbyIssue();
              litype.issuetype = issueStringArray[q];
              litype.countissues = 1;

              //turn the amountSpent into a long, so it can be added to a sum
              String strcash = lob.amountSpent;
              int dotPos = strcash.lastIndexOf(".");
              strcash = strcash.substring(0, dotPos);
              float getcash = floor(Integer.parseInt(strcash));
              litype.spentOnIssue = getcash;
              allSpentArray = (float[]) append(allSpentArray, getcash);
              //set the first specific issue
              litype.specIssueType = append(litype.specIssueType, issueSpecString);

              ltype.lobHASH_Issue.put(litype.issuetype, litype);
              //Add one LobbyIssue to the list of unique LobbyIssues
              ltype.issueArrayList.add(litype);
            }
          }//close the int q for loop
        }//close the lob.issuesPack if statement
      }//close the int i for loop
    }
  }
  catch (JSONException e) {
    println (e.toString());
  }
  println("clientArrayList.size = " + clientArrayList.size());
  println("issueArrayList.size = " + issueArrayList.size());
  println("allSpentArray.size = " + allSpentArray.length);
  allSpentArray = sort(allSpentArray);
  println("loadAndSort() has completed");
  
  for(int i=0; i<clientArrayList.size(); i++){
    alphaClients.add(clientArrayList.get(i).clname); 
  }
  Collections.sort(alphaClients);
  //println(alphaClients);
  
}

public void listClientsAlpha(){
  for(int i=0; i<alphaClients.size(); i++){
//    for(int j=0; j<bubbles.length; j++){
//      if (alphaClients.get(i).equals(bubbles[j].bclient)){
        if(mouseX < width/2+200 ){
          listMap = map(mouseY, 0, alphaClients.size()/19, +300, height+140);
        }
        textFont(f2, 14);
        fill(50);
        text(bubbles[i].bclient, 200, -listMap+(i*20));
        if(mouseX < width && mouseX>(width/2)+200 && mouseY < (-listMap+(i*20)-0)+(height/2) && mouseY > (-listMap+(i*20)-20)+(height/2)){
            fill(50,50,50,40);
            rect(200,-listMap+(i*20)-20, 400, 20);
            //trackColorSet(bubbles[j]);
        }
        
//      } 
//    }
  }
}

public void sortByClient() {
  for (int i = 0; i <clientArrayList.size(); i++) {
    LobbyClient ltype = clientArrayList.get(i);
    theseSpentArray = (float[]) append(theseSpentArray, ltype.totalMoney);
    theseSpentFILTER = (float[]) append(theseSpentArray, ltype.totalMoney);
  }
  theseSpentArray = sort(theseSpentArray);
  theseSpentFILTER = sort(theseSpentFILTER);
  theseSpentMax = max(theseSpentArray);
  theseSpentMin = min(theseSpentArray);
} 

public void sortByMoney() { 
  centerGravity = false;
  float mod = width/37;
  float s = width*0.014f;
  for (int i = 0; i<bubbles.length; i++) {
    for (int j = 0; j<theseSpentArray.length; j++) {
      if (bubbles[i].total == theseSpentArray[j]) {
        bubbles[i].xMiddle = ((i % mod) * s)-380;
        bubbles[i].yMiddle= (floor(i/mod) * s)-200;
      }
    }
  }
}

public void sortTest() {
  int qount = 0;
  for (int i = 0; i<clientArrayList.size(); i++) {
    LobbyClient ltype = clientArrayList.get(i);
    if (addNewBases == true) {
      matchFound = false;
      for (int j = theseSpentFILTER.length-1; j>0; j--) {
        if (clientArrayList.get(i).totalMoney == theseSpentFILTER[j]) {
          if (!matchFound && qount<799) {
            theseSpentFILTER[j] = 0;
            Bubble temps = new Bubble(random(-width*0.2f, width*0.2f), random(-height*0.2f, height*0.2f), ltype.count, ltype.clname, ltype.money, i, ltype.totalMoney, ltype.issuesSpec);
            bubbles = (Bubble[])append (bubbles, temps);
            qount++;
            matchFound = !matchFound;
          }
        }
      }
    }
  }
  bubblesAdded = true;
  setSort = false;
  chartPlace = (bubbles.length/2);
} 

public void lineChartOverlay() {
  fill(160, 120, 100, 40);
  stroke(160, 120, 100, 80);
  beginShape();
  vertex((chartPlace), (hHeight-100));
  for (int bs = 0; bs<bufferResize.size(); bs++) {
    vertex((chartPlace)-bufferResize.get(bs).index, (hHeight-100)-bufferResize.get(bs).monease);
  }
  vertex((chartPlace)-bubbles.length, (hHeight-105));
  endShape();
  fill(120, 140, 190, 40);
  stroke(120, 140, 190, 80);
  beginShape();
  vertex((chartPlace), (hHeight-100));
  for (int bs = 0; bs<bufferResize.size(); bs++) {
    vertex((chartPlace)-bufferResize.get(bs).index, (hHeight-100)-bufferResize.get(bs).zease/4);
  }
  vertex((chartPlace)-bubbles.length, (hHeight-105));
  endShape();
}


public void keyReleased() {
  if (key == 'l') {
    setSort = true;
  }
  if (key == 'm') {
    goIssues = !goIssues;
    goWidth = true;
  }
  if (key == 'c') {
    centerGravity = !centerGravity;
  }
  if (key == '3') {
    toggle3D = !toggle3D;
  }
  if (key == '4') {
    sortByMoney();
    moneySort = !moneySort;
    lineTime = !lineTime;
    centerGravity = !centerGravity;
  }
  if (key == 's') {
    showSpecific = !showSpecific;
  }
  if (key == CODED) {
    if (keyCode == LEFT) {
      goIssues = false;
      goWidth = true;
      if (incrementIssue==0) {
        incrementIssue=issueArrayList.size()-1;
      } 
      else {
        incrementIssue--;
        firstTimeAround = true;
        if (!moneySort) {
          lineTime=true;
        }
      }
      connects.clear();
      bufferResize.clear();
    }
    if (keyCode == RIGHT) {
      goIssues = false;
      goWidth = true;
      if (incrementIssue>issueArrayList.size()-2) {
        incrementIssue=0;
      } 
      else {
        incrementIssue++;
        firstTimeAround = true;
        if (!moneySort) {
          lineTime=true;
        }
      }
      connects.clear();
      bufferResize.clear();
    }
    if (keyCode == DOWN) {
      scrollCount++;
    }
    if (keyCode == UP) {
      if (scrollCount>1) {
        scrollCount--;
      } 
      else {
        scrollCount = 0;
      }
    }
  }//END if key == CODED
}

public void mouseReleased() {
   if(mouseX > 200 && mouseX < 274 && mouseY > 38 && mouseY < 72  && chooseYears){
     showYearsNow = !showYearsNow;
   } 
//   if(mouseX > 200 && mouseX < 274 && mouseY > 38 && mouseY < 72  && trackCo){
//     trackThisBubble = !trackThisBubble;
//   } 
   if(mouseX > 280 && mouseX < 358 && mouseY > 38 && mouseY < 72 ){ 
     choosenYear = "2011";
     clearAllRestart();
     loadAndSort(choosenYear);
     runAllStart();
   }
   if(mouseX > 285+85 && mouseX < 285+85+74 && mouseY > 38 && mouseY < 72 ){ 
     choosenYear = "2010";
     clearAllRestart();
     loadAndSort(choosenYear);
     runAllStart();
   }
   if(mouseX > 285+85+85 && mouseX < 285+85+74+86 && mouseY > 38 && mouseY < 72 ){ 
     choosenYear = "2009";
     clearAllRestart();
     loadAndSort(choosenYear);
     runAllStart();
   }
   if (mouseX > 285+85+85+85 && mouseX < 285+85+74+86+86 && mouseY > 38 && mouseY < 72 ) {
     choosenYear = "2008";
     clearAllRestart();
     loadAndSort(choosenYear);
     runAllStart();
   }
   if (mouseX > 285+85+85+85+85 && mouseX < 285+85+74+86+86+86 && mouseY > 38 && mouseY < 72 ) {
     choosenYear = "2007";
     clearAllRestart();
     loadAndSort(choosenYear);
     runAllStart();
   }
   
   
   for(int i=0; i<bubbles.length; i++){
     if(moneySort && !showAlphaClients && mouseX < bubbles[i].x+(bubbles[i].originalSize/2)+(width/2) && mouseX > bubbles[i].x-(bubbles[i].originalSize/2)+(width/2) && mouseY < bubbles[i].y+(bubbles[i].originalSize/2)+(height/2) && mouseY > bubbles[i].y-(bubbles[i].originalSize/2)+(height/2)){
        trackColorSet(bubbles[i]);
      } else if (!moneySort && !showAlphaClients && mouseX < bubbles[i].x+(bubbles[i].dease/2)+(width/2) && mouseX > bubbles[i].x-(bubbles[i].dease/2)+(width/2) && mouseY < bubbles[i].y+(bubbles[i].dease/2)+(height/2) && mouseY > bubbles[i].y-(bubbles[i].dease/2)+(height/2)) {
        trackColorSet(bubbles[i]);
      } 
   }
   
   if(showAlphaClients){
   for(int i=0; i<alphaClients.size(); i++){
//    for(int j=0; j<bubbles.length; j++){
//      if (alphaClients.get(i).equals(bubbles[j].bclient)){
       if(mouseX < width && mouseX>(width/2)+200 && mouseY < (-listMap+(i*20)-0)+(height/2) && mouseY > (-listMap+(i*20)-20)+(height/2)){
          trackColorSet(bubbles[i]);
      }
//     }
//    }
   }
   }
   
   if(forwardGo){
     goIssues = false;
      goWidth = true;
      if (incrementIssue>issueArrayList.size()-2) {
        incrementIssue=0;
      } 
      else {
        incrementIssue++;
        firstTimeAround = true;
        if (!moneySort) {
          lineTime=true;
        }
      }
      connects.clear();
      bufferResize.clear();
   }
   if(backwardGo){
      goIssues = false;
      goWidth = true;
      if (incrementIssue==0) {
        incrementIssue=issueArrayList.size()-1;
      } 
      else {
        incrementIssue--;
        firstTimeAround = true;
        if (!moneySort) {
          lineTime=true;
        }
      }
      connects.clear();
      bufferResize.clear();
   }
   if(pauseGo){
     goIssues = !goIssues;
     goWidth = true;
   }
   if(gridGo){
     sortByMoney();
     moneySort = !moneySort;
     lineTime = !lineTime;
     centerGravity = !centerGravity;
   }
   if(threeDGo){
     toggle3D = !toggle3D;
   }
   if(specificGo){
     showSpecific = !showSpecific;
   }
   if(alphaGo){
     showAlphaClients = !showAlphaClients;
     goIssues = false;
     goWidth = true;
   }
}

public void trackColorSet(Bubble tbubble){
  tbubble.trackToggle = !tbubble.trackToggle;
}


public void showAllYears() {
  textFont(f, 20);
  textSize(20);  
  fill(55, 50, 45, 170);
  if (mouseX > 280 && mouseX < 358 && mouseY > 38 && mouseY < 72 ) {
    fill(135, 120, 105, 180);
    //pick2011 = true; 
  }
  rect(-hWidth+285, -hHeight+38, 74, 34);
  fill(255);
  text("2011", -hWidth+216+86, -hHeight+61);
  
  fill(55, 50, 45, 170);
  if (mouseX > 285+85 && mouseX < 285+85+74 && mouseY > 38 && mouseY < 72 ) {
    fill(135, 120, 105, 180);
    //pick2010 = true;
  }
  rect(-hWidth+285+85, -hHeight+38, 74, 34);
  fill(255);
  text("2010", -hWidth+216+170, -hHeight+61);
  
  fill(55, 50, 45, 170);
  if (mouseX > 285+85+85 && mouseX < 285+85+74+86 && mouseY > 38 && mouseY < 72 ) {
    fill(135, 120, 105, 180);
    //pick2009 = true;
  }
  rect(-hWidth+285+85+85, -hHeight+38, 74, 34);
  fill(255);
  text("2009", -hWidth+216+170+86, -hHeight+61);
  
  fill(55, 50, 45, 170);
  if (mouseX > 285+85+85+85 && mouseX < 285+85+74+86+86 && mouseY > 38 && mouseY < 72 ) {
    fill(135, 120, 105, 180);
    //pick2008 = true;
  }
  rect(-hWidth+285+85+85+85, -hHeight+38, 74, 34);
  fill(255);
  text("2008", -hWidth+216+170+86+86, -hHeight+61);
  
  fill(55, 50, 45, 170);
  if (mouseX > 285+85+85+85+85 && mouseX < 285+85+74+86+86+86 && mouseY > 38 && mouseY < 72 ) {
    fill(135, 120, 105, 180);
    //pick2008 = true;
  }
  rect(-hWidth+285+85+85+85+85, -hHeight+38, 74, 34);
  fill(255);
  text("2007", -hWidth+216+170+86+86+86, -hHeight+61);
}

public void clearAllRestart(){
  bubbles = (Bubble[]) expand(bubbles, 0);
  incrementIssue = 0;
  //lob = (LobbyBlob) expand(lob, 0);
  lobList.clear();
  lobIssueArray.clear();
  clientArrayList.clear();
  issueArrayList.clear();
  lobHASH.clear();
  lobHASH_Issue.clear();
  bufferResize.clear();
  connects.clear();
  alphaClients.clear();
  allSpentArray = (float[]) expand(theseSpentArray,0);
  theseSpentArray = (float[]) expand(theseSpentArray,0);
  theseSpentFILTER = (float[]) expand(theseSpentArray,0);
  theseSpentMax = 0;
  theseSpentMin = 0;
}

public void runAllStart(){
  centerGravity = true;
  goIssues = true;
  goWidth = true;
  setSort = true;
  showYearsNow = false;
  lineTime = true;
  moneySort = false;
}

public void theInterface(){
  pushMatrix();
  translate(width/2, height/2);
  translate(0,0,3);
  textFont(f2, 34);
  textAlign(LEFT);
  if (bubblesAdded == true) {
    thisone = issueArrayList.get(incrementIssue).issuetype;
  } 
  else {
    thisone = "Contentious Issues";
  }
  float issuedisplayWIDTH = textWidth(thisone);
  
  noStroke();
  textSize(34);
  fill(55, 50, 45, 150);
  rect(-hWidth, -hHeight+80, issuedisplayWIDTH+40, 80); //ISSUES RECT
  fill(255);
  text(thisone, -hWidth+20, -hHeight+130);//ISSUES

  textFont(f, 20);
  textSize(20);
  fill(55, 50, 45, 170);
  rect(-hWidth, -hHeight+38, 190, 34); //FEDERAL LOBBYING RECT
  fill(255);
  text(intro, -hWidth+20, -hHeight+61); //FEDERAL LOBBYING
  
  fill(55, 50, 45, 170);
  if (mouseX > 200 && mouseX < 274 && mouseY > 38 && mouseY < 72  && !showYearsNow) {
    fill(135, 120, 105, 180);
    chooseYears = true;
  }
  if (showYearsNow){
    fill(55, 50, 45, 20);
  }
  rect(-hWidth+200, -hHeight+38, 74, 34); //CHOOSE YEAR RECT
  fill(255);
  text(choosenYear, -hWidth+216, -hHeight+61); //CHOOSE YEAR
  
  if(showYearsNow) showAllYears();
  
  
  if (mouseX > 0 && mouseX < 110 && mouseY > 186 && mouseY < 212) { //FORWARD RECT
    fill(85, 90, 102, 255);
    forwardGo = true;
  } else {
    fill(85, 90, 102, 200);
    forwardGo = false;
  }
  rect(-hWidth, -hHeight+186, 110, 26); 
  
  if (mouseX > 0 && mouseX < 110 && mouseY > 186+36 && mouseY < 212+36) { //FORWARD RECT
    fill(85, 90, 102, 255);
    backwardGo = true;
  } else {
    fill(85, 90, 102, 200);
    backwardGo = false;
  }
  rect(-hWidth, -hHeight+186+36, 110, 26); 
  
  if (mouseX > 0 && mouseX < 110 && mouseY > 186+66 && mouseY < 212+66) { //PLAY/PAUSE RECT
    fill(85, 90, 102, 255);
    pauseGo = true;
  } else {
    fill(85, 90, 102, 200);
    pauseGo = false;
  }
  rect(-hWidth, -hHeight+186+36+36, 110, 26); 
  
  if (mouseX > 0 && mouseX < 110 && mouseY > 186+99 && mouseY < 212+99) { //GRID MODE RECT
    fill(85, 90, 102, 255);
    gridGo = true;
  } else {
    fill(85, 90, 102, 200);
    gridGo = false;
  }
  rect(-hWidth, -hHeight+186+36+36+36, 110, 26); 
  
  if (mouseX > 0 && mouseX < 110 && mouseY > 186+142 && mouseY < 212+146) { //3D MODE RECT
    fill(85, 90, 102, 255);
    threeDGo = true;
  } else {
    fill(85, 90, 102, 200);
    threeDGo = false;
  }
  rect(-hWidth, -hHeight+186+36+36+36+36, 110, 26); 
  
  if (mouseX > 0 && mouseX < 110 && mouseY > 186+178 && mouseY < 212+182) { //CLIENT LIST MODE RECT
    fill(85, 90, 102, 255);
    alphaGo = true;
  } else {
    fill(85, 90, 102, 200);
    alphaGo = false;
  }
  rect(-hWidth, -hHeight+186+36+36+36+36+36, 110, 26);
  
  if (mouseX > 0 && mouseX < 110 && mouseY > 186+178+36 && mouseY < 212+182+36) { //SPECIFIC MODE RECT
    fill(85, 90, 102, 255);
    specificGo = true;
  } else {
    fill(85, 90, 102, 200);
    specificGo = false;
  }
  rect(-hWidth, -hHeight+186+36+36+36+36+36+36, 110, 26); 
  
  textFont(f, 13);
  textSize(13);
  fill(255);
  text("FORWARD>", -hWidth+20, -hHeight+204); //FORWARD
  text("BACKWARD<", -hWidth+20, -hHeight+204+36); //BACKWARD
  text("PLAY/PAUSE", -hWidth+20, -hHeight+204+36+36); //PLAY/PAUSE
  text("GRID MODE", -hWidth+20, -hHeight+204+36+36+36); //GRID MODE
  text("3D MODE", -hWidth+20, -hHeight+204+36+36+36+36); //3D MODE
  text("CLIENT LIST", -hWidth+20, -hHeight+204+36+36+36+36+36); //CLIENT LiST MODE
  text("SPECIFICS", -hWidth+20, -hHeight+204+36+36+36+36+36+36); //SPECIFIC MODE
  
  
  popMatrix();
}

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
  int barColor;
  int c = color(50, 50, 50, 30);
  int cm = color(0);
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
    diameter = map(sqrt(1), 0, sqrt(clientArrayList.size()*0.7f), 0, 200); //map(sqrt(1), 0, sqrt(clientArrayList.size()*0.4), 0, 200);
    diamStandard = map(sqrt(1), 0, sqrt(clientArrayList.size()*0.7f), 0, 200);
    bclient= iss;
    bmoney= mny;
    id = din;
    spring = 0.4f;
    gravity = 0;
    friction = 0;
    index = indexin;
    xMiddle = 0;
    yMiddle = 0;
    z = 0;//din*(-10);
    bufferSwitch = false;
    buffer = 1.2f;
    total = totalin;
    spec = specin;
    maxSpentSq = sqrt(max(allSpentArray)/205000);
    minSpentSq = sqrt(min(allSpentArray)/205000);
    originalSize = map(sqrt(1), 0, sqrt(clientArrayList.size()*0.7f), 0, 200);
  } 

  public void collide() {
    if (selected != this) {
      for (int i = 0; i < bubbles.length; i++) {
        if (selected != bubbles[i]) {
          float dx = bubbles[i].x - x;
          float dy = bubbles[i].y - y;
          float distance = sqrt(dx*dx + dy*dy);
          //minDist = ((bubbles[i].diameter*0.9))+ (diameter/2);//use this for middle gravity circle layout
          minDist = ((bubbles[i].dease*0.5f))+(dease*this.buffer); 
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

  public void motion() {
    //vy += gravity;
    if (centerGravity) {
      xMiddle = 0;
      yMiddle = 0;
      x += (xMiddle-x)*0.016f;
      y += (yMiddle-y)*0.016f;
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
    vx *= 0.9f;
    vy *= 0.9f;
    zease += (z-zease)*0.2f;
    dease += (diameter-dease)*0.08f;
    monease += (dmoney-monease)*0.08f;
  }
  public void moneyMotion() {
    dease += (diameter-dease)*0.08f;
    monease += (dmoney-monease)*0.08f;
    zease += (z-zease)*0.2f;
    x += (xMiddle-x)*0.04f;
    y += (yMiddle-y)*0.04f;
  }

  public void display() {
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

  public void widthFlux() {
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
            this.diameter = map(sqrt(clientArrayList.get(this.index).lobHASH_Issue.get(thisone).spentOnIssue / (theseSpentMin*0.55f)), minSpentSq, maxSpentSq, 10, 150);
            this.dmoney = map(sqrt(clientArrayList.get(this.index).lobHASH_Issue.get(thisone).spentOnIssue / (theseSpentMin*0.55f)), minSpentSq, maxSpentSq, 10, 150);
            //this.diameter = map(sqrt(clientArrayList.get(this.index).lobHASH_Issue.get(thisone).spentOnIssue / 205000), 0, sqrt(200), 0, 200);
            
            if (this.dease > diamStandard*10) {
              this.buffer = 0.6f;
            } else if (this.dease > diamStandard) {
              this.buffer = 1;
            }
            
            this.z = -clientArrayList.get(this.index).issueArrayList.get(j).countissues*10;
            
            if (lineTime) {
              float[][] xy = new float[1][2];
              Connector tempc = new Connector(this, this.x, this.y, this.bclient, thisone, lineCount, xy, this.diameter, this.z);
              connects.add(tempc);
              lineCount++;
            }
          } 
          else {
            if (this.colorSwitchCount != frameCount) { 
              this.c = color(50, 50, 50, 30); 
              this.bufferSwitch = false;
              this.buffer = 1.2f;
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

  public void chartTool() {
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
  
  public void companyTab(){
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

  public void specIssueDisplay() {
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
              fill(40);
              text(issueSpecStringArray[issa], -hWidth+130, (-hHeight+198)+(issa*30));
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



class Connector {

  String name;
  String issue;
  float x;
  float y;
  float xTarget;
  float yTarget;
  int index;
  Bubble mybubble;
  float distance;
  float diam;
  float z;
  float zease;

  Connector(Bubble bin, float xin, float yin, String namein, String issuein, int indexin, float[][] xyin, float diamin, float zin){
    name = namein;
    issue = issuein;
    x = xin;
    y = yin;
    index = indexin;
    mybubble = bin;
    diam = diamin;
    z = zin;
  }
  
  public void motion(){
    x = mybubble.x;
    zease += (z-zease)*0.2f;
    y = mybubble.y;
  }
  
  public void display(){
    stroke(255,255,255,22);
    //line(x, y, x+random(-20,20), y+random(-20,20));
    if (connects.size()>1 && this.index<connects.size()-1){
      distance =  dist(x, y, connects.get(this.index+1).x, connects.get(this.index+1).y);
      if (toggle3D == true){
          line(x, y, zease, connects.get(this.index+1).x,connects.get(this.index+1).y, connects.get(this.index+1).zease);
      } else {
        pushMatrix();
          translate(0,0,-1);
          line(x, y, connects.get(this.index+1).x, connects.get(this.index+1).y);
        popMatrix();
        //noFill();
        //bezier(x, y, x+distance, y+(distance*0.2),  connects.get(this.index+1).x+distance, connects.get(this.index+1).y+(distance*0.4), connects.get(this.index+1).x, connects.get(this.index+1).y);
      }
    }
  }
}

class LobbyBlob { //begin LobbyBlob class
  String clientName;
  String amountSpent;
  String clientCategory;
  String lyear;
  ArrayList issuesPack;
  ArrayList issuesSpecific;
   
  float x; // current x
  float y; // current y
  float rotation = 0;
  
  float tx; // target x
  float ty; // target y
  String isstring;
  
  int c = 255;
  boolean showText = false;

  LobbyBlob() { // "the constructor"    
    issuesPack = new ArrayList(0);
    issuesSpecific = new ArrayList(0);
  }

  public void update() {
    x  += (tx-x)*0.7f;
    y  += (ty-y)*0.7f;
  }
  
  public void render() {
    pushMatrix();
      translate(x,y);
      rotate(rotation);
      fill(c);
      noStroke();
      ellipse(0,0,4,4);
    popMatrix();    
  }
 
} //end LobbyBlob class
class LobbyClient {

  String clname;
  String money;
  float totalMoney;
  float moneyFloat;
  ArrayList issuesPk;
  ArrayList issuesSpec;
  int count;
  int originPlace;
  ArrayList<LobbyBlob> lobList = new ArrayList();
  int c = 255;

  HashMap<String, LobbyIssue> lobHASH_Issue = new HashMap();
  ArrayList<LobbyIssue> issueArrayList = new ArrayList();

  LobbyClient() {
  }
}

class LobbyIssue {

  String issuetype;
  String[] specIssueType = new String[0];
  String clname2;
  int countissues;
  float spentOnIssue;
  ArrayList<LobbyBlob> lobIssueArray = new ArrayList();
  
  LobbyIssue(){}
  
}
  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "LobbyData_Issues2ee" });
  }
}
