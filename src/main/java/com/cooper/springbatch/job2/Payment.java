package com.cooper.springbatch.job2;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    private boolean successStatus;

    public Payment(Long amount, boolean successStatus) {
        this.amount = amount;
        this.successStatus = successStatus;
    }

    public void success() {
        this.successStatus = true;
    }

}
