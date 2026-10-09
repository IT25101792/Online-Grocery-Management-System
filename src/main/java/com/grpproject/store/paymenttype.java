package com.grpproject.store;

public class paymenttype {

    private String cash;
    private String debitcards;
    private String cashondelivery;


 paymenttype(String cash, String debitcards, String creditcards) {
     this.cash="cash";
     this.debitcards="debitcards";
     this.cashondelivery="creditcards";
 }

  void  setcash(String cash){
    this.cash=cash;
  }
  void  cashondelivery(String debitcards){
     this.debitcards=debitcards;
  }

  void  cashondelivery(String creditcards){
     this.creditcards=creditcards;
  }

   public String getcash(){
     return this.cash;

   }
   public String getdebitcards(){
     return this.debitcards;

   }
   public String getcashondeliverycreditcards(){
     return this.creditcards;
   }



}
