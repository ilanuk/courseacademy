package com.learning.courseacademy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Role extends BaseRole{
    @Enumerated(EnumType.STRING)
    @Column(length = 60, unique = true)
    private RoleName roleName;
}
