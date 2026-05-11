package com.fyfe.form;

import lombok.Data;

import java.util.List;

@Data
public class MyPictureForm {
    private List<String> images;
    private String text;
}
