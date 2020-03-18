package com.example.lib_network.request;

public class Student {
     public String  name="尤";
    public  int age =100 ;
    public  int height =192;
    public  String  sex="男";


    public void  myHight(){
        System.out.print("我的身高："+ height +"厘米"+"\n");
    }

    public  String myName(){
        System.out.print("我的名字是："+name+"\n");
        return  name;
    }


}


