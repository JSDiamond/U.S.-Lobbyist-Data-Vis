// PARSING SUNLIGHT LABS LOBBYIST DATA FROM TEXT FILES
import org.json.*;
import processing.opengl.*;

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
String choosenYear = "year >";
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
float scrollPlace;
float[] theseSpentArray = new float[0];
float[] theseSpentFILTER = new float[0];
float[] allSpentArray = new float[0];


void setup() {
  //size(1300, 800, OPENGL);
  size(screenWidth, screenHeight, OPENGL);
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
  listMap = hHeight-20;
  scrollPlace = 0-hHeight;
}


void draw() {
  colorMode(RGB);
  //background(210, 215, 205);
  background(215, 220, 215);
  
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
    rotateX(map(mouseY, 0, screen.height, 1.5, 1.5));
    rotateY(22);
    rotate(map(mouseX, 0, screen.width, 0, 10));
    scale(1.15);
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
    lineTime = true;
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


void loadAndSort(String chYear) {
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
  
}//END loadAndSort()


void listClientsAlpha(){
  pushMatrix();
  translate(0,0,4);
  fill(100);
  rect(160, -hHeight, 20, height);
  fill(255);
  rect(180, -hHeight, 500, height);
  for(int i=0; i<alphaClients.size(); i++){
    for(int j=0; j<bubbles.length; j++){
      if (alphaClients.get(i).equals(bubbles[j].bclient)){
        if(mouseX < width/2+180 && mouseX > width/2+160 ){
          listMap = map(mouseY, 0, alphaClients.size()*0.0585, 200, height+140);
          scrollPlace = (mouseY-10)-hHeight;
        }
        fill(150);
        rect(160, scrollPlace, 20, 20);
        textFont(f2, 14);
        fill(50);
        text(bubbles[j].bclient, 200, -listMap+(i*20));
        if(mouseX < width && mouseX>(width/2)+200 && mouseY < (-listMap+(i*20)-0)+(height/2) && mouseY > (-listMap+(i*20)-20)+(height/2)){
            fill(50,50,50,40);
            rect(200,-listMap+(i*20)-20, 400, 20);
            //trackColorSet(bubbles[j]);
        }
        
      } 
    }
  }
  popMatrix();
}

void sortByClient() {
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

void sortByMoney() { 
  centerGravity = false;
  float mod = width/37;
  float s = width*0.014;
  for (int i = 0; i<bubbles.length; i++) {
    for (int j = 0; j<theseSpentArray.length; j++) {
      if (bubbles[i].total == theseSpentArray[j]) {
        bubbles[i].xMiddle = ((i % mod) * s)-380;
        bubbles[i].yMiddle= (floor(i/mod) * s)-200;
      }
    }
  }
}

void sortTest() {
  int qount = 0;
  for (int i = 0; i<clientArrayList.size(); i++) {
    LobbyClient ltype = clientArrayList.get(i);
    if (addNewBases == true) {
      matchFound = false;
      for (int j = theseSpentFILTER.length-1; j>0; j--) {
        if (clientArrayList.get(i).totalMoney == theseSpentFILTER[j]) {
          if (!matchFound && qount<799) {
            theseSpentFILTER[j] = 0;
            Bubble temps = new Bubble(random(-width*0.2, width*0.2), random(-height*0.2, height*0.2), ltype.count, ltype.clname, ltype.money, i, ltype.totalMoney, ltype.issuesSpec);
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
  
  for(int i=0; i<bubbles.length; i++){
    alphaClients.add(bubbles[i].bclient); 
  }
  Collections.sort(alphaClients);
  
//  for(int i=0; i<alphaClients.size(); i++){
//    for(int j=0; j<bubbles.length; j++){
//      if (alphaClients.get(i).equals(bubbles[j].bclient)){
//        bubbles[i] = bubbles[j];
//        println(bubbles[i].bclient+" = "+i);
//      }  
//    }
//  }
} 

void lineChartOverlay() {
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


void keyReleased() {
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
    lineTime = true;
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

void mouseReleased() {
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
//   if (mouseX > 285+85+85+85+85 && mouseX < 285+85+74+86+86+86 && mouseY > 38 && mouseY < 72 ) {
//     choosenYear = "2007";
//     clearAllRestart();
//     loadAndSort(choosenYear);
//     runAllStart();
//   }
   
   
   for(int i=0; i<bubbles.length; i++){
     if(moneySort && !showAlphaClients && mouseX < bubbles[i].x+(bubbles[i].originalSize/2)+(width/2) && mouseX > bubbles[i].x-(bubbles[i].originalSize/2)+(width/2) && mouseY < bubbles[i].y+(bubbles[i].originalSize/2)+(height/2) && mouseY > bubbles[i].y-(bubbles[i].originalSize/2)+(height/2)){
        trackColorSet(bubbles[i]);
      } else if (!moneySort && !showAlphaClients && mouseX < bubbles[i].x+(bubbles[i].dease/2)+(width/2) && mouseX > bubbles[i].x-(bubbles[i].dease/2)+(width/2) && mouseY < bubbles[i].y+(bubbles[i].dease/2)+(height/2) && mouseY > bubbles[i].y-(bubbles[i].dease/2)+(height/2)) {
        trackColorSet(bubbles[i]);
      } 
   }
   
   if(showAlphaClients){
   for(int i=0; i<alphaClients.size(); i++){
    for(int j=0; j<bubbles.length; j++){
     if (alphaClients.get(i).equals(bubbles[j].bclient)){
       if(mouseX < width && mouseX>(width/2)+200 && mouseY < (-listMap+(i*20)-0)+(height/2) && mouseY > (-listMap+(i*20)-20)+(height/2)){
          trackColorSet(bubbles[j]);
      }
     }
    }
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
     lineTime = true;
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

void trackColorSet(Bubble tbubble){
  tbubble.trackToggle = !tbubble.trackToggle;
}


void showAllYears() {
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
  //rect(-hWidth+285+85+85+85+85, -hHeight+38, 74, 34);
  //fill(255);
  //text("2007", -hWidth+216+170+86+86+86, -hHeight+61);
}

void clearAllRestart(){
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

void runAllStart(){
  centerGravity = true;
  goIssues = true;
  goWidth = true;
  setSort = true;
  showYearsNow = false;
  lineTime = true;
  moneySort = false;
}

void theInterface(){
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
    alphaGo = true;
  } else {
    fill(85, 90, 102, 200);
    alphaGo = false;
  }
  rect(-hWidth, -hHeight+186+36+36+36+36, 110, 26); 
  
  if (mouseX > 0 && mouseX < 110 && mouseY > 186+178 && mouseY < 212+182) { //CLIENT LIST MODE RECT
    fill(85, 90, 102, 255);
    specificGo = true;
  } else {
    fill(85, 90, 102, 200);
    specificGo = false;
  }
  rect(-hWidth, -hHeight+186+36+36+36+36+36, 110, 26);
  
//  if (mouseX > 0 && mouseX < 110 && mouseY > 186+178+36 && mouseY < 212+182+36) { //SPECIFIC MODE RECT
//    fill(85, 90, 102, 255);
//    threeDGo = true;
//  } else {
//    fill(85, 90, 102, 200);
//    threeDGo = false;
//  }
//  rect(-hWidth, -hHeight+186+36+36+36+36+36+36, 110, 26); 
  
  textFont(f, 13);
  textSize(13);
  textAlign(RIGHT);
  fill(255);
  text("FORWARD >", -hWidth+100, -hHeight+204); //FORWARD
  text("BACKWARD <", -hWidth+100, -hHeight+204+36); //BACKWARD
  text("PLAY/PAUSE", -hWidth+100, -hHeight+204+36+36); //PLAY/PAUSE
  text("GRID MODE", -hWidth+100, -hHeight+204+36+36+36); //GRID MODE
  text("CLIENT LIST", -hWidth+100, -hHeight+204+36+36+36+36); //3D MODE
  text("SPECIFICS", -hWidth+100, -hHeight+204+36+36+36+36+36); //CLIENT LiST MODE
  //text("SPECIFICS", -hWidth+20, -hHeight+204+36+36+36+36+36+36); //SPECIFIC MODE
  textAlign(LEFT);
  
  popMatrix();
}

