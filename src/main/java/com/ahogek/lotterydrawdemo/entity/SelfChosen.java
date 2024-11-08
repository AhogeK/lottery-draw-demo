package com.ahogek.lotterydrawdemo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

/**
 * @author AhogeK ahogek@gmail.com
 * @since 2024-10-06 15:01:38
 */
@Entity
@DynamicInsert
public class SelfChosen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate drawTime;

    private String number;

    private Integer numberType;

    private Integer sort;

    public SelfChosen() {
    }

    public SelfChosen(String number, Integer numberType, Integer sort) {
        this.number = number;
        this.numberType = numberType;
        this.sort = sort;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(LocalDate drawTime) {
        this.drawTime = drawTime;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getNumberType() {
        return numberType;
    }

    public void setNumberType(Integer numberType) {
        this.numberType = numberType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
