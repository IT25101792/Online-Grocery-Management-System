package com.grpproject.store;

public class paymenttype {

    private String cash;
    private String debitcards;
    private String creditcards;


 paymenttype(String cash, String debitcards, String creditcards) {
     this.cash="cash";
     this.debitcards="debitcards";
     this.creditcards="creditcards";
 }

  void  setcash(String cash){
    this.cash=cash;
  }
  void  setdebitcards(String debitcards){
     this.debitcards=debitcards;
  }

  void  setcreditcards(String creditcards){
     this.creditcards=creditcards;
  }

   public String getcash(){
     return this.cash;

   }
   public String getdebitcards(){
     return this.debitcards;

   }
   public String getcreditcards(){
     return this.creditcards;
   }



}
