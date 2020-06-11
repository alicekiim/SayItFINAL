// Code adapted from tutorial 'Chat App with Firebase' by KODDev.
// Tutorial found at: https://www.youtube.com/watch?v=WsyJlFjJkyE&list=PLzLFqCABnRQftQQETzoVMuteXzNiXmnj8&index=5

package com.example.siateacher;

public class Teachers {


    public String id;
    public String name;
    public String image;
    public String status;
    public int num;

    public Teachers(String id, String name, String image, String status, int num) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.status = status;
        this.num = num;
    }

    public Teachers() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

}
