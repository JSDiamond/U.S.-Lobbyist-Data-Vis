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
  float count;

  Connector(Bubble bin, float xin, float yin, String namein, String issuein, int indexin, float diamin, float zin){
    name = namein;
    issue = issuein;
    x = xin;
    y = yin;
    index = indexin;
    mybubble = bin;
    diam = diamin;
    z = zin;
  }
  
  void motion(){
    x = mybubble.x;
    zease += (z-zease)*0.2;
    y = mybubble.y;
  }
  
  void display(){
    stroke(255,255,255,15);
    count++;
    //line(x, y, x+random(-20,20), y+random(-20,20));
    if (connects.size()>1 && this.index<connects.size()-1){
      distance =  dist(x, y, connects.get(this.index+1).x, connects.get(this.index+1).y);
      if (toggle3D == true){
          line(x, y, zease, connects.get(this.index+1).x,connects.get(this.index+1).y, connects.get(this.index+1).zease);
      } else {
        pushMatrix();
          translate(0,0,-1);
          //line(x, y, connects.get(this.index+1).x, connects.get(this.index+1).y);
          noFill();
          bezier(x, y, 
                  connects.get(this.index+1).x, connects.get(this.index+1).y,
                  x, y,  
                  connects.get(this.index+1).x, connects.get(this.index+1).y);
        popMatrix();
        //noFill();
        //bezier(x, y, x+distance, y+(distance*0.2),  connects.get(this.index+1).x+distance, connects.get(this.index+1).y+(distance*0.4), connects.get(this.index+1).x, connects.get(this.index+1).y);
      }
    }
  }
}

