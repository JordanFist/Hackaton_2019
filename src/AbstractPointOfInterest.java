package pointOfInterest;

java.awt.Point
java.lang.String

abstract class AbstractPointOfInterest{
  private Point _localisation;
  private String _type;

  Point getlocalisation(){
    return _localisation;
  }
  void setlocalisation(Point loc){
    _localisation = loc;
  }

  String gettype(){
    return _type;
  }
  void settype(String type){
    _type = type;
  }

  int cmp_pointofinterest();
}
