package pcd.lab10.rmi;

import java.io.Serializable;

public class Message implements Serializable {

    private final String content;

    public Message(String s){
        content = s;
    }

    public String getContent(){
        return content;
    }
}
