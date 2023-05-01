package com.law.convertilovelaw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Table(name = "convert_history")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ConvertHistory {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @NotNull
    @Size(max = 50)
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull
    @Column(name = "progress", nullable = false)
    private float progress;

    @Column(name = "filename")
    private String filename;

    @Column(name = "result")
    private String result;
}
