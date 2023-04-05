package com.vastly.ymh.hlht.config.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataResult {

    private boolean success;
    private String msg;//
    private int totalcount;
    private Object data;
    private int code;



}
