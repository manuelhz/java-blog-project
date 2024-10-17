package org.bytebounty.app.util;

import java.util.Comparator;

import org.bytebounty.app.model.Comment;

public class SortCommentByDate implements Comparator<Comment> {

    // Sorting in descending order of date of publication
    @Override
    public int compare(Comment o1, Comment o2) {
        return o2.getPublishedAt().compareTo(o1.getPublishedAt());
    }

}
