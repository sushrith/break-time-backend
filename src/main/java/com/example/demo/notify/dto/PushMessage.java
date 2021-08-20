package com.example.demo.notify.dto;

public class PushMessage {
  private final String title;

  private final String body;

  private final String image;

  private final String icon;

  public PushMessage(String title, String body, String image, String icon) {
    this.title = title;
    this.body = body;
    this.image= image;
    this.icon=icon;
  }

  public String getIcon() {
    return icon;
  }

  public String getTitle() {
    return this.title;
  }

  public String getBody() {
    return this.body;
  }

  public String getImage(){ return this.image;}

  @Override
  public String toString() {
    return "PushMessage [title=" + this.title + ", body=" + this.body + "]";
  }

}
