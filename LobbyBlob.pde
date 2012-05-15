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
  
  color c = 255;
  boolean showText = false;

  LobbyBlob() { // "the constructor"    
    issuesPack = new ArrayList(0);
    issuesSpecific = new ArrayList(0);
  }

  void update() {
    x  += (tx-x)*0.7;
    y  += (ty-y)*0.7;
  }
  
  void render() {
    pushMatrix();
      translate(x,y);
      rotate(rotation);
      fill(c);
      noStroke();
      ellipse(0,0,4,4);
    popMatrix();    
  }
 
} //end LobbyBlob class
