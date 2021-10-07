package models;

import play.data.validation.Constraints;

import java.util.List;

public class PostSaveData {
    @Constraints.Required
    public String title;

    @Constraints.Required
    public String content;

    public Boolean published;

    public List<Integer> tags;
}