package com.github.merelysnow.mail.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class User {

    private final String name;
    private List<Mail> emails;

}
