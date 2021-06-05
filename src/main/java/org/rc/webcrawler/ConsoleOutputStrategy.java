package org.rc.webcrawler;

class ConsoleOutputStrategy implements OutputStrategy {

    int num=0;

    @Override
    public void print(String line) {
        System.out.println(num++ +" -" + line);
    }
}
