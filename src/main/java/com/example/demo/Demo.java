package com.example.demo;

/**
 * @author：lanjy
 * @date：2020/6/24
 * @description：
 */
public class Demo {

    public static void main(String[] args) {
        A ab = new B();
        System.out.println("===============");
        A ab1 = new B();
    }
}
class A {

    static {
        System.out.print("1");
    }

    public A() {
        System.out.print("2");
    }
}

class B extends A{

    static {
        System.out.print("a");
    }

    public B() {
        System.out.print("b");
    }
}
