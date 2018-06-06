package com.example.meimeng.parse;

import java.util.List;

public abstract class BaseTypeParse<E> {

    public abstract List<E> beanParse(String json);


}
