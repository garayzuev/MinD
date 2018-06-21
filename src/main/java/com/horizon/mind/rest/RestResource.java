package com.horizon.mind.rest;

import java.util.List;

/**
 * Created by garayzuev@gmail.com on 18.06.2018.
 */
public interface RestResource<S> {

    void add(S source);

    List<S> getAll();

    S getById(long id);
}
