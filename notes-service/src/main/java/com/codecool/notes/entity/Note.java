package com.codecool.notes.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue
    private Long noteId;

    @Column
    private Modules module;

    @Column
    private int week;

    @Column
    private Long userId;

    @Column
    private Date submissionTime;

    @Column
    private String noteTitle;

    @Column
    private String noteUrl;
}
