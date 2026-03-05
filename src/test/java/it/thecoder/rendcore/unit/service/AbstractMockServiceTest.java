package it.thecoder.rendcore.unit.service;

import io.ebean.*;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public abstract class AbstractMockServiceTest<T> {

    @Mock
    protected Database db;


    @Mock
    protected Transaction transaction;

    @Mock
    protected Query<T> query;

    @Mock
    protected ExpressionList<T> expressionList;

    @Mock
    protected PagedList<T> pagedList;


    protected void baseQuery (Class<T> clazz){
        when(db.find(clazz)).thenReturn(query);
        when(query.setLabel(anyString())).thenReturn(query);
        when(query.where()).thenReturn(expressionList);
    }
}
