package com.horizon.mind.rest;

import java.util.Collection;

/**
 * Created by garayzuev@gmail.com on 18.06.2018.
 */
public interface RestResource<S> {

    S add(S source);

    Collection<S> getAll();

    S getById(long id);
}
