
public class Country {
  String name;
  String abr;
  String gold;
  String silver;
  String bronze;
  String total;

  public Country(String name, String abr, String gold, String silver, String bronze, String total) {
    this.name = name;
    this.abr = abr;
    this.gold = gold;
    this.silver = silver;
    this.bronze = bronze;
    this.total = total;
  }

  public String getName() {
    return name;
  }

  public String serialize(){
    return this.name + ';' + this.abr + ';' + this.gold + ';' +
      this.silver + ';' + this.bronze + ';' + this.total;
  }
}
