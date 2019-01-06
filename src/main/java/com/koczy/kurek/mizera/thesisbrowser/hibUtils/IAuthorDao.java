package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Author;

public interface IAuthorDao {

    Author getAuthorByName(String filterName);

    Author getAuthorById(int id);

    void saveAuthor(Author author);

    int getAuthorsNum();

}
